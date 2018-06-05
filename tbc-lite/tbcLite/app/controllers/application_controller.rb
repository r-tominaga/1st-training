class ApplicationController < ActionController::Base
  protect_from_forgery with: :exception
  include SessionsHelper

  def current_user_wallet
    uri = URI.parse('http://localhost:4567/queryUser')
    http = Net::HTTP.new(uri.host, uri.port)
    req = Net::HTTP::Post.new(uri.path)
    current_user
    req.set_form_data({'user_name' => @current_user.user_name})
    ret = ActiveSupport::JSON.decode(http.request(req).body)
    @amount = ret['msg']
  end
end
