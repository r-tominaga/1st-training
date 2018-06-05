module SessionsHelper
  def log_in(user)
    session[:user_id] = user.id
  end

  def current_user
    @current_user ||= User.find_by(id: session[:user_id])
  end

  # ユーザーがログインしていればtrue、その他ならfalseを返す
  # http://totutotu.hatenablog.com/entry/2016/08/18/132150
  def logged_in?
    !current_user.nil?
  end

  def logout
    session.delete(:user_id)
    @current_user = nil
  end
end
