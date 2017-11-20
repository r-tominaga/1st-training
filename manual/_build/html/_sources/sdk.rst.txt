========================
SDKの使い方
========================

| ここではSDKの使い方を説明していく
| SDKにもいくつか種類があるがNode.js版を用いる

前提条件
======================

- Python2.7(npm installの際に3系だとエラーが出るため)
- Node.js 6.9.x 以上だが、7.x未満

.. note::

  | Python3系と使い分けたい場合は、
  | 複数のPythonのバージョンを管理するツールであるpyenvあるいは、anyenv(さらにenv系ツールを管理するもの)をインストール


anyenv導入方法
-------------------------

まずはインストール

.. code-block:: shell

  git clone https://github.com/riywo/anyenv ~/.anyenv

| 次に、環境変数の設定
| これによってanyenvコマンドをターミナルから実行可能にする

.. code-block:: shell

  echo 'export PATH="$HOME/.anyenv/bin:$PATH"' >> ~/.your_profile
  echo 'eval "$(anyenv init -)"' >> ~/.your_profile

.. note::

  | your_profileの部分は、
  | bashの場合は.bashrc or .bash_profile のどちらか
  | zshの場合は.zshrc or .zprofile のどちらか

完了したらターミナルを再起動

.. code-block:: shell

  exec $SHELL -l

続いて、anyenv経由でのpyenvインストールおよびpyenvでのpythonインストール方法

.. code-block:: shell

  # pyenvのインストール
  anyenv install pyenv
  # python 3.6.1のインストール
  pyenv install 3.6.1
  # python 2.7のインストール
  pyenv install 2.7
  # 3.6.1をグローバル環境に変更
  pyenv global 3.6.1

2.7に変えたい場合は

.. code-block:: shell

  pyenv global 2.7

pythonのインタープリタを起動したときにきちんと切り替わっているか確認する

.. code-block:: shell

  $ python
  Python 3.6.1 (default, Jul 12 2017, 18:44:32)
  [GCC 4.2.1 Compatible Apple LLVM 8.1.0 (clang-802.0.42)] on darwin
  Type "help", "copyright", "credits" or "license" for more information.
  >>>

Node.js導入方法
-------------------------

| 6.9.xのものをインストールすればよい
| ただし7.xはサポートしていないので間違えないように
| くれぐれも最新版を落としてこないように気をつけてほしい

公式サイト(https://nodejs.org/en/download/releases/)

npmツールを最新版にするために以下コマンドを実行するが、Python2.7でないとコケるので注意

.. code-block:: shell

  npm install npm@3.10.10 -g

Node.js SDKチュートリアル
==========================

**本チュートリアルの目的**

基本的な機能である"query"と"update"をSDK経由で行えるようにする

| サンプルは用意されており、fabric-samplesをクローンすればすぐに試すことができる
| このチュートリアルでは、3つのステップでアプリケーションの作り方(いじり方)を学んでいく

| 1. Hyperledger Fabricネットワークを立ち上げる
| 台帳をqueryしたり、updateするには3つの基本の部品が必要
|	  ・a peer node
|	  ・ordering node
|	  ・CA(Certificate Authority)
|	この3つの部品がネットワークの根幹をなしている
|	また、CLIコンテナを用いていくつかの管理コマンドを使うこともできる
|	ネットワークは一つのスクリプトでダウンロードと起動ができる

| 2. サンプルのスマートコントラクトのパラメータを理解する
|  スマートコントラクトには様々な機能が含まれており、異なる方法で台帳と通信ができる

| 3. レコードをquery(参照)したり、update(更新)できるようにするためにアプリケーションに手を加える
| 	アプリケーションではSDKのAPIを使うことで、ネットワークと通信し、最終的にはqueryやupdateといった機能を呼び出す

**テストネットワークを入手する**

以下コマンドで好きなところにサンプルをクローンし、fabcarディレクトリに移動

.. code-block:: shell

  git clone https://github.com/hyperledger/fabric-samples.git
  cd fabric-samples/fabcar

fabcarディレクトリには以下が存在する

.. code-block:: shell

  > chaincode    invoke.js       network         package.json    query.js        startFabric.sh

ネットワークを起動するためにstartFabric.shを実行する

.. code-block:: shell

  ./startFabric.sh

ここでなにをしているかを以下に簡単にまとめる

- a peer node, ordering node, CA, CLI Containerを起動
- チャンネルを作成し、チャンネルにピアを追加する
- ピアのファイルシステムにスマートコントラクトをインストールし、チャンネル上でチェーンコードと呼ばれるものをインスタンス化する
- 10台の車の情報を台帳に入れるためにinitLedger関数を呼び出す

.. note::

  | ここではCLIを用いてスクリプトのコマンドを実行しているが、SDKでもサポートしている
  | 興味があればリポジトリを参照(https://github.com/hyperledger/fabric-sdk-node)

ここでdocker psコマンドを使うと、startFabric.shによって開始されたプロセス(コンテナの状況)を見ることができる

.. code-block:: shell

  docker ps

.. note::

  | **どのようにしてネットワークと通信するか**
  | アプリケーションはスマートコントラクトを実行するためにAPIを用いる
  | スマートコントラクトはネットワーク内でホストされ、名前やバージョンによって識別される
  | 例えば、ここでのチェーンコードコンテナは"dev-peer0.org1.example.com-fabcar-1.0"だが、
  | 名前はfabcar、バージョンは1.0、ピアはdev-peer0.org1.example.comに対して動いている

台帳をqueryする
--------------------------------
| queryとは台帳からデータを読み込むことである
| 単一のキーからも複数のキーからもqueryできる
| もし、台帳がJSONのようなリッチデータ・ストレージの形式で書かれている場合には、複雑な検索も可能
| 先にも述べたように、サンプルネットワークにはアクティブなチェーンコードコンテナと10台の車データが用意された台帳が存在する
| また、fabcarディレクトリ内にはいくつかのJavascriptコードもある
| query.jsは車の詳細に対して検索を可能にする

まず、SDK nodeモジュールをプログラム内で機能させるために以下コマンドでインストールする

.. code-block:: shell

  npm install

これでjavascriptプログラムを動かせるようになった
それでは、query.jsを実行して台帳からすべての車のリストを返してもらう

.. code-block:: shell

  node query.js

.. code-block:: shell

  Query result count =  1
  Response is  [{"Key":"CAR0", "Record":{"colour":"blue","make":"Toyota","model":"Prius","owner":"Tomoko"}},
  {"Key":"CAR1",   "Record":{"colour":"red","make":"Ford","model":"Mustang","owner":"Brad"}},
  {"Key":"CAR2", "Record":{"colour":"green","make":"Hyundai","model":"Tucson","owner":"Jin Soo"}},
  {"Key":"CAR3", "Record":{"colour":"yellow","make":"Volkswagen","model":"Passat","owner":"Max"}},
  {"Key":"CAR4", "Record":{"colour":"black","make":"Tesla","model":"S","owner":"Adriana"}},
  {"Key":"CAR5", "Record":{"colour":"purple","make":"Peugeot","model":"205","owner":"Michel"}},
  {"Key":"CAR6", "Record":{"colour":"white","make":"Chery","model":"S22L","owner":"Aarav"}},
  {"Key":"CAR7", "Record":{"colour":"violet","make":"Fiat","model":"Punto","owner":"Pari"}},
  {"Key":"CAR8", "Record":{"colour":"indigo","make":"Tata","model":"Nano","owner":"Valeria"}},
  {"Key":"CAR9", "Record":{"colour":"brown","make":"Holden","model":"Barina","owner":"Shotaro"}}]

| これが返ってくる
| 台帳はkey/valueベースになっていることがわかる

ここでエディタを使ってquery.jsの中身を見がてら、内容を少し変えてみる

.. code-block:: js

  var options = {
        wallet_path : path.join(__dirname, './network/creds'),
        user_id: 'PeerAdmin',
        channel_id: 'mychannel',
        chaincode_id: 'fabcar',
        network_url: 'grpc://localhost:7051',

| 最初のセクションはこうなっている
| ここではchaincode ID、channel name、network endpointsといったいくつかの変数が定義されている

.. code-block:: js

  // queryCar - requires 1 argument, ex: args: ['CAR4'],
  // queryAllCars - requires no arguments , ex: args: [''],
  const request = {
     chaincodeId: options.chaincode_id,
     txId: transaction_id,
     fcn: 'queryAllCars',
     args: ['']

| ここでchaincode_idをfabncarとして定義している(これによって特定のchaincodeをターゲットにすることを許している)
| そして、このチェーンコード内で定義されたqueryAllCars関数が呼ばれることになっている
| しかし、わたしたちが使える関数はこれだけではない
| chaincodeディレクトリ内のfabcar.goをエディタで開く
| するとそこには、initLedger, queryCar, queryAllCars, createCar, changeCarOwnerといった関数が用意されているのがわかる

.. code-block:: go

  func (s *SmartContract) queryAllCars(APIstub shim.ChaincodeStubInterface) sc.Response {

       startKey := "CAR0"
       endKey := "CAR999"

       resultsIterator, err := APIstub.GetStateByRange(startKey, endKey)

このqueryAllCars関数ではshimインターフェースのGetStateByRange関数を用いることで、引数のstartKeyとendKeyの間の台帳データを返してくれることがわかった

それではまたquery.jsに戻り、コードを少し変更してみる
fcn:で定義されているqueryAllCarsをqueryCarに変え、keyとして用いる引数の部分をCAR4に変える

.. code-block:: js

  const request = {
        chaincodeId: options.chaincode_id,
        txId: transaction_id,
        fcn: 'queryCar',
        args: ['CAR4']

保存したらfabcarディレクトリに戻り、以下コマンドで再度実行

.. code-block:: shell

  > node query.js

  {"colour":"black","make":"Tesla","model":"S","owner":"Adriana"} //結果

引数を変えればいろんな検索が可能である

台帳をupdateする
--------------------------------

次に台帳を更新していく

| 台帳の更新はtransaction proposalを生成するアプリケーションとともに始まる
| queryのとき同様に、リクエストはchannel ID、関数、特定のスマートコントラクトを識別するために構成されている
| そこでプログラムは承認用にピアに向けてtransaction proposalを送るために、channel.SendTransactionProposal APIを呼ぶ
| ネットワークはアプリケーションがトランザクション要求をビルドし、署名するために用いるproposal responseを返す
| リクエストはchannel.sendTransaction APIを呼ぶことでordering serviceに送られる
| ordering serviceはブロックの内部にトランザクションを組み込んでいく
| そして、validation用にチェンネル上のすべてのピアに向けてブロックを配達する
| 最終的にアプリケーションはピアのイベントリスナーポートへ接続するためにeh.setPeerAddr APIを使い、特定のトランザクションIDと結びついているイベントを登録するためにeh.registerTxEventを呼ぶ
| このAPIはトランザクションの行方を知ることをアプリケーションに許可する
| 通知のメカニズムだと思えばよい

| それでは、新しく車を作成する
| invoke.jsをエディタで開く

.. code-block:: js

  // createCar - requires 5 args, ex: args: ['CAR11', 'Honda', 'Accord', 'Black', 'Tom'],
  // changeCarOwner - requires 2 args , ex: args: ['CAR10', 'Barry'],
  // send proposal to endorser
  var request = {
      targets: targets,
      chaincodeId: options.chaincode_id,
      fcn: '',
      args: [''],
      chainId: options.channel_id,
      txId: tx_id

| ここには2つの関数があることがわかる
| - createCar
| - changeCarOwner

| これからredで車種はChevy Volt、所有者はNickの車を作成する
| CAR9までは存在するのでCAR10をkeyとする

.. code-block:: javascript

  var request = {
      targets: targets,
      chaincodeId: options.chaincode_id,
      fcn: 'createCar',
      args: ['CAR10', 'Chevy', 'Volt', 'Red', 'Nick'],
      chainId: options.channel_id,
      txId: tx_id

保存して実行する

.. code-block:: shell

  > node invoke.js

  > The transaction has been committed on peer localhost:7053 //結果

ピアはイベント通知を送ってきている
アプリケーションがこの通知を受け取れるのはさきほど紹介したeh.registerTxEvent APIのおかげだ

ここで本当にCAR10が作成されているか調べるためにquery.jsの引数をCAR10に変更して実行する

.. code-block:: shell

  > {"colour":"Red","make":"Chevy","model":"Volt","owner":"Nick"}

これが返ってくれば成功

最後に、所有者の名前を変更してみる

.. code-block:: javascript

  var request = {
     targets: targets,
     chaincodeId: options.chaincode_id,
     fcn: 'changeCarOwner',
     args: ['CAR10', 'Barry'],
     chainId: options.channel_id,
     txId: tx_id

と変更し、再度実行

.. code-block:: shell

  > node invoke.js
  > {"colour":"Red","make":"Chevy","model":"Volt","owner":"Barry"}　//結果

以上でチュートリアルは終了する

=============================
本番SDKの使い方
=============================

Orionで用いているSDKについて説明する

| OrionではWebアプリからのリクエストに呼応するようになっている
| app.jsというファイルにて8000番ポートからのリクエストを受け付けている

.. code-block:: js

  var express = require('express');
  var queryAll = require('./lib/queryAll')
  var initDist = require('./lib/initDist')

  var app = express();
  var bodyParser = require('body-parser');

  app.use(bodyParser.urlencoded({
    extended: true
  }));
  app.use(bodyParser.json());

  app.listen(8000, function() {
    console.log('Example app listening on port 8000!');
  });

| OrionではExpressというNode.js向けのWebフレームワークを使用している
| 日本語のドキュメントも出ているので詳しく知りたい場合は `こちら <http://expressjs.com/ja/>`_
| app.jsを実行すると、8000番からのリクエストを受け付けるようになる

.. code-block:: shell

  % node app.js
  > Example app listening on port 8000!

ここでqueryAllしたい場合はcurlでGETメソッドを用いて、queryAll関数を呼び出す

.. code-block:: shell

  % curl -X GET http://localhost:8000/queryAll
  > [{"Key":"LIMIT","Record":{"limit":"1966-12-09T23:59:00Z"}},{"Key":"MAX","Record":{"max":0}},{"Key":"USR-1","Record":{"auth":0,"coin":0,"name":"","raw":0}}]

上記のcurlコマンドはapp.js内で以下の関数で処理される

.. code-block:: shell

  app.get('/queryAll', function(req, res) {
    queryAll.run((ret) => {
      if (ret == "") {
        res.send("null");
      } else {
        res.json(JSON.parse(ret));
      }
    });
  });

| queryAll.run()でqueryAllオブジェクトのrun()メソッドが実行される
| この場合は、./lib/queryAllが実行される(node queryAll.jsしたのと実質同等)

初期配布をしたい場合は、

.. code-block:: shell

  % curl -X POST http://localhost:8000/initDist -d 'max=1000&limit='2017-12-31''
  > {"result":true,"msg":"3733e78e5e66decee2f84eba7a6dfef8b9373fca8c07a48787b5e4d47dbdb842"}

POSTメソッドで呼び出し、引数を与える

.. code-block:: js

  app.post('/initDist', function(req, res) {
      // リクエストボディを出力
      console.log(req.body);
      var params = [req.body.max,req.body.limit]
      initDist.run(params,(ret) => {
          if (ret.result) {
              res.send(ret);
          } else {
              console.log(ret)
              res.send(ret);
          }
      });
  })


この時注意してほしいのは、var params = [req.body.max,req.body.limit]へ引数を渡すために

::

  'max=1000&limit='2017-12-31'

という表記をしている点である

引数を複数渡す場合は、key=value&key=valueという書き方をする

本番SDKの使い方は以上

=========================
チェーンコード
=========================

| チェーンコードはGo言語で書かれている
| チェーンコードはピアがどのような動きをするか(関数)定義し、データ構造を決定する根幹部分にあたる
| SDKではこのチェーンコードへアクセスして、関数を呼び出している

.. code-block:: go

  type UserInfo struct {
  // ユーザー名
  Name string `json:"name"`
  // １次配布用コイン
  Raw int64 `json:"raw"`
  //　決済可能な有効コイン
  Coin int64 `json:"coin"`
  // 権限情報
  Auth uint64 `json:"auth"`
  }

| ここではユーザー情報に関する構造体を定義している
| Go言語は逆ポーランド記法なので変数名を先に宣言してから型を宣言する

.. code-block:: go

  `json:"name"`

はjson表記する際の記法なので特にここでは触れない

| 実際にqueryAllがどのような流れで行われるかを説明する
| SDKあるいはcliを通してチェーンコードへのアクセスがあると、まずはInvokeが呼ばれる

.. code-block:: go

  //呼ぶ関数を決定するために、ここで引数を検証する。基本機能のインデックスのようなもの。
  func (s *SmartContract) Invoke(APIstub shim.ChaincodeStubInterface) sc.Response {

  // スマートコントラクトの関数名と引数を受け取る
  function, args := APIstub.GetFunctionAndParameters()
  // Route to the appropriate handler function to interact with the ledger appropriately
  //以下に関数名ごとの処理先を明示
  if function == "initLedger" {
    return s.initLedger(APIstub)
  } else if function == "queryUser" {
    return s.queryUser(APIstub, args)
  } else if function == "queryAll" {
    return s.queryAll(APIstub)
  } else if function == "queryMax" {
    return s.queryMax(APIstub)
  } else if function == "queryLimit" {
    return s.queryLimit(APIstub)
  } else if function == "createUser" {
    return s.createUser(APIstub, args)
  } else if function == "addRaw" {
    return s.addRaw(APIstub, args)
  } else if function == "changeAuth" {
    return s.changeAuth(APIstub, args)
  } else if function == "changeMax" {
    return s.changeMax(APIstub, args)
  } else if function == "sendTbc" {
    return s.sendTbc(APIstub, args)
  } else if function == "initDist" {
    return s.initDist(APIstub, args)
  } else if function == "modifyLimit" {
    return s.modifyLimit(APIstub, args)
  } else if function == "initializer" {
    return s.initializer(APIstub)
  }
  return shim.Error("無効なスマートコントラクト名です")
  }

| APIstub.GetFunctionAndParameters()で引数として渡された関数名と引数をそれぞれfunctionとargsに入れる
| そして、function名とマッチする関数を探して、一致したらその関数を呼び出す
| functionがqueryAllならs.queryAll(APIstub)が呼ばれる

.. code-block:: go

  //全台帳参照
  func (s *SmartContract) queryAll(APIstub shim.ChaincodeStubInterface) sc.Response {
  	startKey := ""
  	endKey := ""

  	resultsIterator, err := APIstub.GetStateByRange(startKey, endKey)
  	if err != nil {
  		return shim.Error(err.Error())
  	}
  	defer resultsIterator.Close()

  	// bufferは参照結果を含むJSON配列
  	var buffer bytes.Buffer
  	buffer.WriteString("[")

  	bArrayMemberAlreadyWritten := false
  	for resultsIterator.HasNext() {
  		queryResponse, err := resultsIterator.Next()
  		if err != nil {
  			return shim.Error(err.Error())
  		}
  		// 配列内を","で区切っている
  		//Add a comma before array members, suppress it for the first array member
  		if bArrayMemberAlreadyWritten == true {
  			buffer.WriteString(",")
  		}
  		buffer.WriteString("{\"Key\":")
  		buffer.WriteString("\"")
  		buffer.WriteString(queryResponse.Key)
  		buffer.WriteString("\"")

  		buffer.WriteString(", \"Record\":")
  		//レコードはJSONオブジェクト。なのでそのまま書く。
  		buffer.WriteString(string(queryResponse.Value))
  		buffer.WriteString("}")
  		bArrayMemberAlreadyWritten = true
  	}
  	buffer.WriteString("]")

  	//デバッグ
  	fmt.Printf("- queryAll:\n%s\n", buffer.String())

  	return shim.Success(buffer.Bytes())
  }

| queryAllで実行されているのは、APIstub.GetStateByRange(startKey, endKey)
| startKeyからendKeyまでのデータを取ってくるというものだが、なにもいれないと台帳のデータをまるごと取得できる
| 取得したデータをresultsIteratorに入れたのちに、文字列を追加してJSON形式に編集しているが本筋でないのでここでは説明しない

| 次にinitDistについて説明する
| この関数は初期配布時にadminユーザーであるUSR-1のRawコイン(P)を増やし、その分だけ最大発行額も設定し、有効期限も決定する

.. code-block:: go

  //配布準備時
  func (s *SmartContract) initDist(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
  	if len(args) != 2 {
  		return shim.Error("引数の数は2つです")
  	}
  	//増額は正の整数、減額は負の整数
  	max := Max{}
  	init, err := strconv.ParseUint(args[0], 10, 0)
  	if err != nil {
  		return shim.Error(err.Error())
  	}
  	max.Max = init

  	maxAsBytes, err := json.Marshal(max)
  	if err != nil {
  		return shim.Error(err.Error())
  	}
  	APIstub.PutState("MAX", maxAsBytes)

  	//有効期限の初期化
  	input := []string{args[1]}
  	s.modifyLimit(APIstub, input)

  	//中央銀行に発行額を送る
  	addraw := []string{"USR-1", args[0]}
  	s.addRaw(APIstub, addraw)

  	return shim.Success(nil)
  }

| 変数maxを構造体Maxで初期化
| 引数の1番目をstrconv.ParseUint(args[0], 10, 0)でUint型に変換してinitに代入
| そしてinitをMax構造体内に代入し、json.Marshal(max)でByte型に変換して、maxAsBytesに代入

::

  APIstub.PutState("MAX", maxAsBytes)

でKeyをMAXとして、maxAsBytesをValueとして台帳に保存している

補足だが、台帳からKeyを元にして呼び出す際はGetState()を使う
