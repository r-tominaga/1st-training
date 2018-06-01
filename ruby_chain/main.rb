require_relative 'ruby_chain/block_chain'
require_relative 'ruby_chain/miner'
require 'json'
require 'sinatra'
require 'pstore'

$init_flg = false
$init_data = {to: "", amount:0}
$receive_block_chain

$db = PStore.new('./transactions/history')

# もしDBにデータがあれば$init_flgをtrueに変えて、$receive_block_chainに取得したブロックチェーン情報をセット
# 本当は実行時にチェックしたいが$receive_block_chainが初期化されていないのでエラーがでるので再開時に'/restartしてもらう'
get '/restart' do
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

## blockchain simulator
post '/initDist' do
  if $init_flg == false
    $init_data = params
    $receive_block_chain = BlockChain.new
    block = $receive_block_chain.last_block
    $db.transaction do
      $db['root'] = $receive_block_chain
      p $db['root']
    end
    $init_flg = true
    end
end

get '/chain_info' do
  "#{$receive_block_chain.last_block.height} \n#{$receive_block_chain.last_block.hash} \n#{$receive_block_chain.last_block.previous_hash}"
end

get '/queryAll' do
  tx = []
  $receive_block_chain.blocks.each do |block|
    tx << block.transactions
  end
  "#{tx}"
end

post '/send' do
  args = {name: "miner1", block_chain: $receive_block_chain}
  if (queryAmount(params[:from]).to_i - params[:amount].to_i) < 0
    '残高不足'
  else
    create_miner args, params
  end
end

post '/queryUser' do
  "#{queryAmount(params[:user_name])}"
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

  # puts "start"
  # th1 = create_miner name: "miner1"
  # th2 = create_miner name: "miner2"
  # th3 = create_miner name: "miner3"
  # [th1, th2, th3].each{|t| t.join}

  # puts "block chain result"

  # $receive_block_chain.blocks.each do |block|
  #   puts "*** Block #{block.height} ***"
  #   puts "hash: #{block.hash}"
  #   puts "previous_hash: #{block.previous_hash}"
  #   puts "timestamp: #{block.timestamp}"
  #   # TODO: merkle root
  #   # puts "transactions_hash: #{block.transactions_hash}"
  #   puts "transactions: #{block.transactions}"
  #   puts "nonce: #{block.nonce}"
  #   puts ""
  # end
