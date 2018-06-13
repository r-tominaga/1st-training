class CreateHistories < ActiveRecord::Migration[5.1]
  def change
    create_table :histories do |t|
      t.integer :from
      t.integer :to
      t.integer :amount
      t.string :comment

      t.timestamps
    end
  end
end
