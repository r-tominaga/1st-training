require_relative 'ruby_chain/block_chain'
require_relative 'ruby_chain/miner'
require 'json'
require 'sinatra'
require 'pstore'

# ブロックチェーンが動いているかどうか
$init_flg = false
# initDist時のパラメータ雛形
$init_data = {to: "", amount:0}
# 現在使用しているブロックチェーン
$receive_block_chain

$db = PStore.new('./transactions/history')

# データベースが存在するかチェック
# あれば$init_flgをtrueにして、$receive_block_chainにセット
def checkDatabase
end

def is_blockchain_exist?
end

# 指定ユーザーの残高を計算
def queryAmount user_name
end

def create_miner args, params
  miner = Miner.new args
  miner.accept $receive_block_chain
  tx_data = {from: params[:from], to: params[:to], amount: params[:amount], comment: params[:comment]}
  miner.add_new_block tx_data , $receive_block_chain.last_block
  $db.transaction do
    $db['root'] = $receive_block_chain
    p $db['root']
  end
  broadcast miner
end

def broadcast miner
  puts "#{miner.name} broadcasted"
  $receive_block_chain = miner.block_chain
end

# もしDBにデータがあれば$init_flgをtrueに変えて、$receive_block_chainに取得したブロックチェーン情報をセット
# 本当は実行時にチェックしたいが$receive_block_chainが初期化されていないためエラーがでる
# なので再開時に'/restartしてもらう'
get '/restart' do
end

# 初期配布(=ブロックチェーンを開始)
post '/initDist' do
  if $init_flg == false && params[:to] != "" && !params[:to].nil?
    $init_data = params
    $receive_block_chain = BlockChain.new
    block = $receive_block_chain.last_block
    $db.transaction do
      $db['root'] = $receive_block_chain
      p $db['root']
    end
    $init_flg = true
    return JSON.generate({"status" => true, "msg" => "Success"})
  else
    return JSON.generate({"status" => false, "msg" => "Blockchain has already initialized"})
  end
end

get '/queryAll' do
end

post '/send' do
end

post '/queryUser' do
end
