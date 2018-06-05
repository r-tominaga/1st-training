require 'net/http'
require 'uri'
require 'json'

class TokensController < ApplicationController
  add_flash_types :success, :info, :warning, :danger
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
    req.set_form_data({'to' => params[:target], 'amount' => params[:amount]})
    res = ActiveSupport::JSON.decode(http.request(req).body)
    if res['status']
      redirect_to '/initDist', success: 'Success'
      return
    end
    redirect_to '/initDist', danger: res['msg']
    return
  end

  def indexSendToken
    render 'sendToken'
  end

  def sendToken
    uri = URI.parse('http://localhost:4567/send')
    http = Net::HTTP.new(uri.host, uri.port)
    req = Net::HTTP::Post.new(uri.path)
    current_user
    req.set_form_data({'from' => @current_user.user_name, 'to' => params[:target], 'amount' => params[:amount], 'comment' => params[:comment]})
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
