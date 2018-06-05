class User < ApplicationRecord
  has_secure_password
  def password_confirmation=(val)
    if val.present?
      self.password_digest = BCrypt::Password.create(val)
    end
    @password_digest = val
  end
end
