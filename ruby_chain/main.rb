require_relative 'ruby_chain/block_chain'
require_relative 'ruby_chain/miner'
require 'json'
require 'sinatra'
require 'pstore'

$init_flg = false
$receive_block_chain
$db = PStore.new('./transactions/history')

def is_blockchain_exist?
  return false if $receive_block_chain.nil? || $receive_block_chain.blocks.nil?
  true
end

def queryAmount user_name
  sending = 0
  receiving = 0
  $receive_block_chain.blocks.each do |block|
    if block.transactions[:from] == user_name
      sending += block.transactions[:amount].to_i
    elsif block.transactions[:to] == user_name
      receiving += block.transactions[:amount].to_i
    end
  end
  receiving - sending
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
  $receive_block_chain = miner.block_chain
end

# もしDBにデータがあれば$init_flgをtrueに変えて、$receive_block_chainに取得したブロックチェーン情報をセット
# 本当は実行時にチェックしたいが$receive_block_chainが初期化されていないのでエラーがでるので再開時に'/restartしてもらう'
get '/restart' do
  $db.transaction(true) do
    if $db.root?('root')
      $init_flg = true
      $receive_block_chain = $db['root']
      return JSON.generate({"status" => true, "msg" => "Success"})
    else
      return JSON.generate({"status" => false, "msg" => "Database doesn't exist"})
    end
  end
end

post '/initDist' do
  if $init_flg == false && params[:to] != "" && !params[:to].nil?
    $receive_block_chain = BlockChain.new params
    $db.transaction do
      $db['root'] = $receive_block_chain
    end
    $init_flg = true
    return JSON.generate({"status" => true, "msg" => "Success"})
  else
    return JSON.generate({"status" => false, "msg" => "Blockchain has already initialized"})
  end
end

get '/queryAll' do
  return JSON.generate({"status" => false, "msg" => "Blockchain doesn't exist"}) unless is_blockchain_exist?
  tx = {}
  $receive_block_chain.blocks.each do |block|
    tx[block.height] = {
      "hash": block.hash,
      "height": block.height,
      "transactions": block.transactions,
      "timestamp": block.timestamp,
      "nonce": block.nonce,
      "previous_hash": block.previous_hash
    }
  end
  JSON.generate({"status" => true, "msg" => tx})
end

post '/send' do
  unless is_blockchain_exist?
    return JSON.generate({"status" => false, "msg" => "Blockchain doesn't exist"})
  elsif params[:amount].to_i < 0
    return JSON.generate({"status" => false, "msg" => "You do not steal other's property"})
  elsif params[:from] == params[:to]
    return JSON.generate({"status" => false, "msg" => "You can't send yourself"})
  elsif params[:from] == "" || params[:to] == ""
    return JSON.generate({"status" => false, "msg" => "Cannot be blank user's id"})
  end

  args = {block_chain: $receive_block_chain}
  if (queryAmount(params[:from]).to_i - params[:amount].to_i) < 0
    return JSON.generate({"status" => false, "msg" => "There is not enough money"})
  else
    create_miner args, params
    return JSON.generate({"status" => true, "msg" => 'Success'})
  end
end

post '/queryUser' do
  return JSON.generate({"status" => false, "msg" => "Blockchain doesn't exist"}) unless is_blockchain_exist?
  JSON.generate({"status" => true, "msg" => queryAmount(params[:user_name])})
end
