=======================================
開発環境構築の手順
=======================================
| **前提条件**
| Git client
| Go - 1.7 or later (for releases before v1.0, 1.6 or later)
| MacOSの場合、Xcodeも必要
| Docker - 1.12 or later
| Docker Compose - 1.8.1 or later
| Pip

※gnutarをインストールしたい場合はHomebrewを用いる

.. code-block:: shell

  brew install gnu-tar --with-default-names

Pipについては以下コマンドをターミナルで実行することでアップグレード可能

.. code-block:: shell

  pip install --upgrade pip
  pip install behave nose docker-compose
  pip install -I flask==0.10.1 python-dateutil==2.2 pytz==2014.3 pyyaml==3.10 couchdb==1.0 flask-cors==2.0.1 requests==2.4.3 pyOpenSSL==16.2.0 pysha3==1.0b1 grpcio==1.0.4
  pip install urllib3 ndg-httpsclient pyasn1 ecdsa python-slugify grpcio-tools jinja2 b3j0f.aop six

| DockerはCE(Community Edition)を入れておけばDocker/Docker Compose/Docker Machineすべて入ってくる
| `ダウンロードはこちら <https://www.docker.com/community-edition>`_

インストールしたらターミナルにて

.. code-block:: shell

  docker --version
  docker-compose --version
  docker-machine --version

でバージョンを確認すること

Hyperledger FabricはGoで書かれているので、$ GOPATH/srcにHyperledger Fabricのソースリポジトリをクローンする必要がある

.. code-block:: shell

  cd $GOPATH/src
  mkdir -p github.com/hyperledger
  cd github.com/hyperledger

| ここでクローンするがその際にLinux Foundation ID(以後LFIDと呼ぶ)が必要
| したがって登録していない場合サイト( https://identity.linuxfoundation.org/ )で登録する
| また、Hyperledger FabricのソースはGerritを用いて管理しているので、SSHキーの登録が必要になる
| キーペアを用意する必要があるので以下のコマンドをターミナルで実行

.. code-block:: shell

  ssh-keygen -t rsa -C "Taro Tanaka t-tanaka@example.com" # 名前とアドレスは自分のものに変えること

そうするとキーペアを保存するファイル名を聞かれるので適当に名前をつける
そのあとパスワードを聞かれるので任意のものに設定する
そして以下コマンド実行

.. code-block:: shell

  ssh-add "ファイル名のパス(さきほど決めたもの)"

| 次にGerrit( https://gerrit.hyperledger.org/r/#/admin/projects/fabric )にてLFIDでログインする
| 右上の隅にあるアカウント名をクリックし、ポップアップメニューからSettingsを選択
| 左のサイドメニューからSSH Public Keysをクリック
| そこに作成したキーペアのうち.pubの中身(エディタで開ける)をコピーしたものを貼り付けて、Add keyをクリック
| これでSSHキーの登録は終了したので、github.com/hyperledgerにクローンする。LFIDの部分は自分のものを入力

.. code-block:: shell

  git clone ssh://LFID@gerrit.hyperledger.org:29418/fabric && scp -p -P 29418 LFID@gerrit.hyperledger.org:hooks/commit-msg fabric/.git/hooks/

Hyperledger Fabricをビルドするには以下コマンド

.. code-block:: shell

  cd $GOPATH/src/github.com/hyperledger/fabric
  make dist-clean all

ユニットテストを行う場合は、

.. code-block:: shell

  cd $GOPATH/src/github.com/hyperledger/fabric
  make unit-test

ここまでで環境構築は終了
