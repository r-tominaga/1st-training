require 'net/http'
require 'uri'
require 'json'
require 'digest/sha2'

class TokensController < ApplicationController
  include SessionsHelper
  before_action :require_login, :current_user_wallet

  def showInitDist
    render 'initDist'
  end

  def initDist
    # 下でも呼んでるからprivate関数にまとめたほうが良い
    uri = URI.parse('http://localhost:4567/initDist')
    http = Net::HTTP.new(uri.host, uri.port)
    req = Net::HTTP::Post.new(uri.path)
    req.set_form_data({'to' => params[:user][:user_name], 'amount' => params[:amount]})
    @res = ActiveSupport::JSON.decode(http.request(req).body)
    render 'initDist'
  end

  def indexSendToken
    render 'sendToken'
  end

  def sendToken
    uri = URI.parse('http://localhost:4567/send')
    http = Net::HTTP.new(uri.host, uri.port)
    req = Net::HTTP::Post.new(uri.path)
    current_user
    # idで送らないとユーザー名を変えられるとアカウントの残高が引き継げない
    req.set_form_data({'from' => @current_user.user_name, 'to' => params[:user][:user_name], 'amount' => params[:amount], 'comment' => params[:comment]})
    res = ActiveSupport::JSON.decode(http.request(req).body)
    if res['status']
      redirect_to '/sendToken', success: 'Success'
      return
    end
    redirect_to '/sendToken', danger: res['msg']
    return
  end

  def queryAll
    uri = URI.parse('http://localhost:4567/queryAll')
    json = Net::HTTP.get(uri)
    result = JSON.parse(json)
    @blocks = result["msg"]
    @sha = calc_hash_with_nonce @blocks
    render 'queryAll'
  end

  def calc_hash_with_nonce(blocks)
    sha = []
    unless @blocks == "Blockchain doesn't exist"
      blocks.each do |key, value|
        sha << Digest::SHA256.hexdigest({
          timestamp: value["timestamp"],
          transactions: value["transactions"],
          previous_hash: value["previous_hash"],
          nonce: value["nonce"]
        }.to_json)
      end
    end
    sha
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
