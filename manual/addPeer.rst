=======================================
ピア追加のおおまかな流れ
=======================================

| まず注意点として、ここでのピア追加は静的な追加を想定している
| 既に開始しているネットワーク上にピアを追加する方法ではない

  **暗号生成→設定ブロックの生成→Dockerコンテナ起動→チャンネル作成・追加→チェーンコードのインストール・有効化**

この流れはイチからネットワークを構成し、開始するときと同じである

チェーンコードのファイルはこちら
:download:`チェーンコードサンプル <./assets/tbc.go>`

.. note::

  Hyperledger Fabric v1.0では動的なピアの追加は可能だとしているが、既存のOrgにピアを追加できないなど様々な制約がある

暗号生成について
======================


| Hyperledger Fabricでは、コンテナ間のやり取りに必要な秘密鍵や証明書が含まれたディレクトリを生成する(コマンドで自動生成してくれる)
| crypto-config.yamlに設定が書かれている
| 設定するにあたって、ピアの機関(Organization)とピアの数をこの時点で決めておく必要がある

.. note::

  | ※機関とは、トランザクションが承認される際の条件の違いを生み出す
  | 例えば、複数の企業同士でネットワークを構成する場合にそれぞれの企業ごとに機関を分けておくことで機密性を担保できるなどの利点がある
  | 尚、実行可能なチェーンコードや参照可能なDB情報などを制約したい場合にはチャンネルというサブネット機能がある

.. code-block:: yaml

  # crypto-config.yaml

  OrdererOrgs:
    - Name: Orderer
      Domain: example.com
      Specs:
        - Hostname: orderer
  PeerOrgs:
    # ---------------------------------------------------------------------------
    # Org1
    # ---------------------------------------------------------------------------
    # 機関名
    - Name: Org1
      # ドメイン名
      Domain: org1.example.com
      Template:
        Count: 2 # ピアの数を指定する
      Users:
        Count: 1 #CAで管理するユーザーの数。これ以外にデフォルトでAdminが存在する

この場合、Org1というグループにピアが2つ存在するということになり、グループとピアの数だけ秘密鍵と証明書が発行される

| 設定が終わったら、以下コマンドを実行
| するとグループとピアの数だけ秘密鍵と証明書が入ったcrypto-configディレクトリが生成される
| 既にcrypto-configディレクトリが存在する場合は削除するかどこか他の場所に移す

.. code-block:: shell

  cryptogen generate --config=./crypto-config.yaml  # yamlのパスは各自変更すること

設定ブロック生成
======================

| ここでは
| ・orderer genesis block(ブロックチェーンの先頭ブロックのこと)
| ・channel configuration transaction(チャンネルの設定をするトランザクション。チャンネルとはピアのグループ。機関とは異なり、DB情報やチェーンコードなどを共有するサブネットの役割を果たす)
| ・anchor peer transaction(アンカーピアとはいかなるピアからも接続可能なピアのこと)
| を生成する

.. code-block:: shell

  configtxgen -profile OneOrgOrdererGenesis -outputBlock ./config/genesis.block
  configtxgen -profile OneOrgChannel -outputCreateChannelTx ./config/channel.tx -channelID mychannel # mychannelの部分は好きなチャンネル名に変更可能
  configtxgen -profile OneOrgChannel -outputAnchorPeersUpdate ./config/Org1MSPanchors.tx -channelID mychannel -asOrg Org1MSP

Dockerコンテナ起動
======================

| ここまでで準備は整ったので、次は実際にコンテナを起動していく
| Hyperledger Fabricでは
| ・Orderer
| ・CA
| ・Peers(作成したい分だけ)
| ・CouchDB(リッチなクエリー可能なDB)

| に相当するコンテナを用意する必要がある
| 各コンテナの設定はdocker-compose.yaml内に書いていく

services:内にコンテナのドメイン名を指定していく

.. code-block:: yaml

  # docker-compose.yaml

  services:
    ca.example.com:
      image: hyperledger/fabric-ca:x86_64-1.0.0
      environment:
        - FABRIC_CA_HOME=/etc/hyperledger/fabric-ca-server
        - FABRIC_CA_SERVER_CA_NAME=ca.example.com
        - FABRIC_CA_SERVER_CA_CERTFILE=/etc/hyperledger/fabric-ca-server-config/ca.org1.example.com-cert.pem
        - FABRIC_CA_SERVER_CA_KEYFILE=/etc/hyperledger/fabric-ca-server-config/197a7096ed8aa0d66b994b9d421ca7169df6b030da3c5bcd3459d9320eb7a649_sk # ここは生成された鍵ごとに異なるので各自crypto-config内の名称に変更すること
      ports:
        - "7054:7054"
      command: sh -c 'fabric-ca-server start -b admin:adminpw -d'
      volumes:
        - ./crypto-config/peerOrganizations/org1.example.com/ca/:/etc/hyperledger/fabric-ca-server-config
      container_name: ca.example.com
      networks:
        - basic

    orderer.example.com:
      container_name: orderer.example.com
      image: hyperledger/fabric-orderer:x86_64-1.0.0
      environment:
        - ORDERER_GENERAL_LOGLEVEL=debug
        - ORDERER_GENERAL_LISTENADDRESS=0.0.0.0
        - ORDERER_GENERAL_GENESISMETHOD=file
        - ORDERER_GENERAL_GENESISFILE=/etc/hyperledger/configtx/genesis.block
        - ORDERER_GENERAL_LOCALMSPID=OrdererMSP
        - ORDERER_GENERAL_LOCALMSPDIR=/etc/hyperledger/msp/orderer/msp

      working_dir: /opt/gopath/src/github.com/hyperledger/fabric/orderer
      command: orderer
      ports:
        - 7050:7050
      volumes:
          - ./config/:/etc/hyperledger/configtx
          - ./crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/:/etc/hyperledger/msp/orderer
          - ./crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/:/etc/hyperledger/msp/peerOrg1
      networks:
        - basic

    peer0.org1.example.com:
      container_name: peer0.org1.example.com
      image: hyperledger/fabric-peer:x86_64-1.0.0
      environment:
        - CORE_VM_ENDPOINT=unix:///host/var/run/docker.sock
        - CORE_PEER_ID=peer0.org1.example.com
        - CORE_LOGGING_PEER=debug
        - CORE_CHAINCODE_LOGGING_LEVEL=DEBUG
        - CORE_PEER_LOCALMSPID=Org1MSP
        - CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/peer/
        - CORE_PEER_ADDRESS=peer0.org1.example.com:7051
        - CORE_PEER_GOSSIP_EXTERNALENDPOINT=peer0.org1.example.com:7051
        - CORE_VM_DOCKER_HOSTCONFIG_NETWORKMODE=${COMPOSE_PROJECT_NAME}_basic
        - CORE_LEDGER_STATE_STATEDATABASE=CouchDB
        - CORE_LEDGER_STATE_COUCHDBCONFIG_COUCHDBADDRESS=couchdb:5984

      working_dir: /opt/gopath/src/github.com/hyperledger/fabric
      command: peer node start
      ports:
        - 7051:7051
        - 7053:7053
      volumes:
          - /var/run/:/host/var/run/
          - ./crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/msp:/etc/hyperledger/msp/peer
          - ./crypto-config/peerOrganizations/org1.example.com/users:/etc/hyperledger/msp/users
          - ./config:/etc/hyperledger/configtx
      depends_on:
        - orderer.example.com
      networks:
        - basic

    peer1.org1.example.com:
      container_name: peer1.org1.example.com
      image: hyperledger/fabric-peer:x86_64-1.0.1
      environment:
        - CORE_VM_ENDPOINT=unix:///host/var/run/docker.sock
        - CORE_PEER_ID=peer1.org1.example.com
        - CORE_LOGGING_PEER=debug
        - CORE_CHAINCODE_LOGGING_LEVEL=DEBUG
        - CORE_PEER_LOCALMSPID=Org1MSP
        - CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/peer/
        - CORE_PEER_ADDRESS=peer1.org1.example.com:7051
        # 以下２つは詳しいことがまだわかっていない
        - CORE_PEER_GOSSIP_EXTERNALENDPOINT=peer1.org1.example.com:7051
        - CORE_PEER_GOSSIP_BOOTSTRAP=peer0.org1.example.com:7051
        - CORE_VM_DOCKER_HOSTCONFIG_NETWORKMODE=${COMPOSE_PROJECT_NAME}_basic
        - CORE_LEDGER_STATE_STATEDATABASE=CouchDB
        - CORE_LEDGER_STATE_COUCHDBCONFIG_COUCHDBADDRESS=couchdb:5984
      working_dir: /opt/gopath/src/github.com/hyperledger/fabric
      command: peer node start
      ports:
        - 7061:7051
        - 7063:7053
      volumes:
          - /var/run/:/host/var/run/
          - ./crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/msp:/etc/hyperledger/msp/peer
          - ./crypto-config/peerOrganizations/org1.example.com/users:/etc/hyperledger/msp/users
          - ./config:/etc/hyperledger/configtx
      depends_on:
        - orderer.example.com
      networks:
        - basic


    couchdb:
      container_name: couchdb
      image: hyperledger/fabric-couchdb:x86_64-1.0.0
      ports:
        - 5984:5984
      environment:
        DB_URL: http://localhost:5984/member_db
      networks:
        - basic

    cli:
      container_name: cli
      image: hyperledger/fabric-tools:x86_64-1.0.0
      tty: true
      environment:
        - GOPATH=/opt/gopath
        - CORE_VM_ENDPOINT=unix:///host/var/run/docker.sock
        - CORE_LOGGING_LEVEL=DEBUG
        - CORE_PEER_ID=cli
        - CORE_PEER_ADDRESS=peer0.org1.example.com:7051
        - CORE_PEER_LOCALMSPID=Org1MSP
        - CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
        - CORE_CHAINCODE_KEEPALIVE=10

      working_dir: /opt/gopath/src/github.com/hyperledger/fabric/peer
      command: /bin/bash
      volumes:
          - /var/run/:/host/var/run/
          # チェーンコードのコピーを行っている左側がホストのコピー元になるので適宜変更すること
          - ./../chaincode/:/opt/gopath/src/github.com/
          - ./crypto-config:/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/
          - ./crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/msp:/etc/hyperledger/msp/peer
          - ./crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/msp:/etc/hyperledger/msp/peer
          - ./crypto-config/peerOrganizations/org1.example.com/users:/etc/hyperledger/msp/users
          - ./config:/etc/hyperledger/configtx

      networks:
          - basic
      depends_on:
       - orderer.example.com
       - peer0.org1.example.com
       - peer1.org1.example.com
       - couchdb


| - imageはDockerイメージ名に該当するので、好きなものに変更可
| - environmentで環境変数を指定していく(dockerコマンド実行時に適用される)
| - volumesは作成するコンテナにホストのファイルをマウント(コピー)する設定
|   - ここに書かれたファイルがコンテナ内に作成されることになる
| - cliはSDKなしでピアをネットワークを操作するためのもの

.. note::

  crypto-config作成時に設定した以上のピアやグループを作成することはできないので注意

設定がすべて終わったら、以下コマンドでコンテナ起動

.. code-block:: shell

  docker-compose -f docker-compose.yml up -d

| Dockerコンテナが正しく起動していれば
| % docker ps コマンドで以下のように表示されるはず

.. code-block:: shell

  CONTAINER ID        IMAGE                                     COMMAND                  CREATED             STATUS             PORTS                                            NAMES
  bad89e3e7d3b        hyperledger/fabric-tools:x86_64-1.0.0     "/bin/bash"              24 hours ago        Up 10 minutes                                                        cli
  efbdaec53766        hyperledger/fabric-peer:x86_64-1.0.1      "peer node start"        24 hours ago        Up 10 minutes       0.0.0.0:7061->7051/tcp, 0.0.0.0:7063->7053/tcp   peer1.org1.example.com
  709dcd816757        hyperledger/fabric-peer:x86_64-1.0.0      "peer node start"        24 hours ago        Up 10 minutes       0.0.0.0:7051->7051/tcp, 0.0.0.0:7053->7053/tcp   peer0.org1.example.com
  89fcc6aae143        hyperledger/fabric-orderer:x86_64-1.0.0   "orderer"                24 hours ago        Up 10 minutes       0.0.0.0:7050->7050/tcp                           orderer.example.com
  81d1e312867f        hyperledger/fabric-ca:x86_64-1.0.0        "sh -c 'fabric-ca-..."   24 hours ago        Up 10 minutes       0.0.0.0:7054->7054/tcp                           ca.example.com
  e0ac3cccfb10        hyperledger/fabric-couchdb:x86_64-1.0.0   "tini -- /docker-e..."   24 hours ago        Up 10 minutes       4369/tcp, 9100/tcp, 0.0.0.0:5984->5984/tcp       couchdb

表示されれば成功

チャンネル作成・追加
======================

各ピアはそろったのでチェンネルを作成して、追加する

.. code-block:: shell

  docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0.org1.example.com peer channel create -o orderer.example.com:7050 -c mychannel -f /etc/hyperledger/configtx/channel.tx

| 上記コマンドでpeer0.org1.example.com内にmychannelのブロックが生成される
| 生成されただけではチェンネルには誰もいないので
| 以下コマンドでpeer0をmychannelに追加

.. code-block:: shell

  docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0.org1.example.com peer channel join -b mychannel.block

peer1も追加する場合は、実行させるピアはそのままで対象のピアをオプションで変更して実行する

.. code-block:: shell

  docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" -e "CORE_PEER_ADDRESS=peer1.org1.example.com:7051" peer0.org1.example.com peer channel join -b mychannel.block

これでpeer1もmychannelに追加される

チェーンコードのインストール・有効化
=====================================

チェーンコードはそれぞれのピアにインストールする必要がある
::

  # cliコンテナからpeer0へ"tbc"というチェーンコードのバージョン1.0をインストールさせる
  # -nはチェーンコード名、-vはバージョン、-pはチェーンコードのパスを指す
  docker exec cli peer chaincode install -n tbc -v 1.0 -p github.com/tbc
  docker exec cli peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n tbc -v 1.0 -c '{"Args":[""]}'
  docker exec -e "CORE_PEER_ADDRESS=peer1.org1.example.com:7051" cli peer chaincode install -n tbc -v 1.0 -p github.com/tbc
  docker exec -e "CORE_PEER_ADDRESS=peer1.org1.example.com:7051" cli peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n tbc -v 1.0 -c '{"Args":[""]}'

| installは特定のピアに対してチェーンコードのインストールを行う
| instantiateはインストールしたチェーンコードを有効化させる
| インストールし、有効化もさせたので実際に動くか確かめる

.. code-block:: shell

  # queryAllという関数で現在の台帳のデータを取ってきている
  docker exec cli peer chaincode query -C mychannel -n tbc -c '{"Args":["queryAll"]}'
  # initLedgerという関数で台帳を初期化(初期値が与えられる)
  docker exec cli peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n tbc -c '{"function":"initLedger","Args":[""]}'
  # 上記はcli(peer0)に対して関数を実行している
  # peer0に実行した結果が、peer1にも反映されているか確認するためにpeer1にqueryAllを実行
  docker exec peer1.org1.example.com peer chaincode query -C mychannel -n tbc -c '{"Args":["queryAll"]}'

実行結果が正しければ終了


チェーンコードを変更したいとき
==============================

チェーンコードはupgradeコマンドで更新が可能

まず更新したいチェーンコードをインストールする

.. code-block:: shell

  docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode install -o peer0.org1.example.com:7051 -n tbc -v 1.1 -p github.com/tbc

次にupgradeをする

.. code-block:: shell

  docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode upgrade -C mychannel -n tbc -v 1.1 -c '{"Args":[""]}' -p hyperledger/tbc

変更した関数などを呼び出してきちんと動いているか確認したら完了
