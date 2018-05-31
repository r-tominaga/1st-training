require 'socket'

server = TCPServer.new 2000
pids = []
loop do
  Thread.start(server.accept) do |client|
    p [Thread.current]
    headers = []
    while header = client.gets
      # Content-Lengthの値をlengthに格納
      if header.include? "Content-Length"
        length = buffer.split[1].to_i
      end
  
      # 改行のみ→次の行以降はBody
      if header == "\r\n"
        # BodyからContent-Length文字読み出す
        length.times do
          putc socket.getc
        end
        break
      end
      break
    end
    p [Thread.current, headers]

    client.puts "HTTP/1.0 200 OK"
    client.puts "Content-Type: text/plain"
    client.puts
    client.puts "message body"
    client.close
  end
end