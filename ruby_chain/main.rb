require_relative 'ruby_chain/block_chain'
require_relative 'ruby_chain/miner'
require 'json'
require 'sinatra'
require 'pstore'

$init_flg = false
$init_data = {to: "", amount:0}
$receive_block_chain

$db = PStore.new('./transactions/history')

def checkDatabase
  $db.transaction(true) do
    if $db.root?('root')
      $init_flg = true
      $receive_block_chain = BlockChain.new
      $db['root'].each {|key, value|
        $receive_block_chain.blocks[key] = Block.new(
              hash: value['hash'],
              height: key,
              transactions: value['transactions'],
              timestamp: value['timestamp'],
              nonce: value['nonce'],
              previous_hash: value['previous_hash']
        )
      }
    end
  end
end

def is_blockchain_exist?
  if $receive_block_chain.nil? || $receive_block_chain.blocks.nil?
    return false
  end
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
  puts "#{miner.name} broadcasted"
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
  return JSON.generate({"status" => false, "msg" => "Blockchain doesn't exist"}) unless is_blockchain_exist?
  return JSON.generate({"status" => false, "msg" => "You do not steal other's property"}) if params[:amount].to_i < 0
  return JSON.generate({"status" => false, "msg" => "You can't send yourself"}) if params[:from] == params[:to]
  return JSON.generate({"status" => false, "msg" => "You can't send yourself"}) if params[:from] == "" || params[:to] == ""
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
