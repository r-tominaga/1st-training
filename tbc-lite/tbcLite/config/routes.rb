Rails.application.routes.draw do
  # ログイン関連
  get 'login' => 'sessions#new'
  post 'login' => 'sessions#create'
  get 'logout' => 'sessions#destroy'
  get 'sessions/new'

  # 全ブロック表示
  get 'home' => 'tokens#queryAll'

  # アカウント照会

  # 送金
  get 'sendToken' => 'tokens#index'
  post 'sendToken' => 'tokens#sendToken'
  # 初期配布

  # User周り
  resources :users
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html
end
