require 'net/http'
require 'uri'
require 'json'

class TokensController < ApplicationController
  include SessionsHelper
  before_action :require_login

  def index
    uri = URI.parse('http://localhost:4567/queryUser')

    http = Net::HTTP.new(uri.host, uri.port)

    # 別に今回はSSL通信する必要ないかな？
    # http.use_ssl = true
    # http.verify_mode = OpenSSL::SSL::VERIFY_NONE

    req = Net::HTTP::Post.new(uri.path)
    # current_userから取ってくること
    req.set_form_data({'user_name' => 'hamano'})

    res = http.request(req)
    @amount = ActiveSupport::JSON.decode(res.body)
    current_user
    render 'sendToken'
  end

  def sendToken
    uri = URI.parse('http://localhost:4567/send')
    http = Net::HTTP.new(uri.host, uri.port)
    req = Net::HTTP::Post.new(uri.path)
    # current_userから取ってくること
    req.set_form_data({'from' => 'hamano', 'to' => params[:target], 'amount' => params[:amount], 'comment' => params[:comment]})
    http.request(req)

    redirect_to action: 'index'
  end

  def queryAll
    uri = URI.parse('http://localhost:4567/queryAll')
    json = Net::HTTP.get(uri)
    @result = JSON.parse(json)
    render 'queryAll'
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def require_login
      redirect_to '/login' unless logged_in?
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    # def token_params
    #   params.require(:user).permit(:user_name, :email, :password, :password_confirmation)
    # end

end
