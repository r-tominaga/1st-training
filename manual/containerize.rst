======================
SDKのコンテナ化
======================
SDKのコンテナ化について説明をする

既にOrionのSDKは手元にある想定で話を進めていく

| app.js eslintrc.json package.jsonがOrionWalletディレクトリ直下にある場合
| libディレクトリに移動すること
| 理由は、OrionWalletまるごとコンテナにマウントしてしまうとnode_modulesが入ってしまい
| 上手く動作しないことが多々ある
| なのでpackage.jsonを元にしてnpm installをコンテナ起動時にさせるためである
| app.jsの移動に伴って一部パスを変更する必要がでてくる

.. code-block:: js

  var queryUser = require('./queryUser');
  var createUser = require('./createUser');
  var queryAll = require('./queryAll')
  var addRaw = require('./addRaw')
  var sendTbc = require('./sendTbc')
  var changeAuth = require('./changeAuth')
  var changeMax = require('./changeMax')
  var queryMax = require('./queryMax')
  var queryLimit = require('./queryLimit')
  var initDist = require('./initDist')
  var modifyLimit = require('./modifyLimit')
  var install = require('./install')
  var upgrade = require('./upgrade')
  var initializer = require('./initializer')

requireするファイルのパスを上記のように変更する必要があるので注意

次に現在使っているdocker-compose.yamlを編集する

.. code-block:: yaml

  nodeapp:
    image: node:6.12.0
    ports:
      - '8000:8000'
    tty: true
    volumes:
      - ../lib:/app/OrionWallet/lib
      - ../creds:/app/OrionWallet/creds
      - $HOME/.hfc-key-store:/root/.hfc-key-store
    working_dir: /app/OrionWallet/lib
    command: node app.js
    networks:
      - basic
    depends_on:
     - orderer.example.com
     - peer0.org1.example.com
     - peer1.org1.example.com

docker-compose.yamlの最後に上記を付け加える

SDKコンテナがHyperledgerのコンテナと接続できるようにするためにlib/にある.jsファイルを変更していく(7-16行目あたりにある)

.. code-block:: js

  var queryAllObj = {
    run: (callback) => {
      var options = {
        wallet_path: path.join(__dirname, '../creds'),
        user_id: 'PeerAdmin',
        channel_id: 'mychannel',
        chaincode_id: 'tbc',
        //変更するのはnetwork_urlのlocalhost部分
        network_url: 'grpc://peer0.org1.example.com:7051',
      };

query系のoptionは1つだが、台帳に変更を与えるようなファイルは3箇所変更する必要がある

.. code-block:: js

  var initDistObj = {
    run: (params, callback) => {
      var options = {
        wallet_path: path.join(__dirname, '../creds'),
        user_id: 'PeerAdmin',
        channel_id: 'mychannel',
        chaincode_id: 'tbc',
        //peer_url event_url orderer_urlの3つを変更する
        peer_url: 'grpc://peer0.org1.example.com:7051',
        event_url: 'grpc://peer0.org1.example.com:7053',
        orderer_url: 'grpc://orderer.example.com:7050'
      };

| 該当箇所すべて書き換えれば完了
| いつも通りstartFabric.shを実行する

.. note::

  | まれにComposeでコンテナ起動時にコンテナがきちんとすべて起動していないためにチェンネル追加が失敗することがある
  | sleep時間が短すぎるために引き起こされていることがあるので
  | その場合は、start.shのexport FABRIC_START_TIMEOUT=50の数値を変更することで解決できる

| docker-compose.yamlのコンテナひとつだけ起動したいときは
| docker-compose up -d nodeapp
| など引数にservice名(≠コンテナ名)を指定することで可能
