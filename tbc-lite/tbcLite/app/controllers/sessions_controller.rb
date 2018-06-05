class SessionsController < ApplicationController
  include SessionsHelper

  def new
    render 'new', user: @user
  end

  def create
    #emailをログインアカウント情報として使用している
    user = User.find_by(email: params[:user][:email].downcase)
    if user && user.authenticate(params[:user][:password_digest])
      log_in(user)
      redirect_to '/home'
    else
      flash.now[:danger] = 'emailまたはpasswordが間違っています'
      render "new"
    end
  end

  def destroy
    logout
    redirect_to '/login'
  end
end
