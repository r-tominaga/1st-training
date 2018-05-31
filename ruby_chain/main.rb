require_relative 'ruby_chain/block_chain'
require_relative 'ruby_chain/miner'
require 'json'
require 'sinatra'

$receive_block_chain
$init_data

## blockchain simulator
post '/initDist' do
  $init_data = params
  $receive_block_chain = BlockChain.new
  block = $receive_block_chain.last_block
  data = {block.height => {
    'hash' => block.hash,
    'previous_hash' => block.previous_hash,
    'timestamp' => block.timestamp,
    'transactions' => block.transactions,
    'nonce'=> block.nonce
  }}
  file = File.open('./transactions/genesis.json','w')
  file.puts JSON.pretty_generate(data)
  file.close
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

# ここをトリガーで発動させる
def create_miner args, params
  miner = Miner.new args
  miner.accept $receive_block_chain
  tx_data = {from: params[:from], to: params[:to], amount: params[:amount], comment: params[:comment]}
  miner.add_new_block tx_data , $receive_block_chain.last_block

  block = $receive_block_chain.last_block
  if block.height == 0
    data = {block.height => {
      'hash' => block.hash,
      'previous_hash' => block.previous_hash,
      'timestamp' => block.timestamp,
      'transactions' => block.transactions,
      'nonce'=> block.nonce
    }}
    file = File.open('./transactions/genesis.json','w')
    file.puts JSON.pretty_generate(data)
    file.close
  else
    file = File.open("./transactions/#{block.timestamp}.json",'w')
    data = {block.height => {
      'hash' => block.hash,
      'previous_hash' => block.previous_hash,
      'timestamp' => block.timestamp,
      'transactions' => block.transactions,
      'nonce'=> block.nonce
    }}
    file.puts JSON.pretty_generate(data)
    file.close  
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
