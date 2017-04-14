class Car{
  int num;
  double gas;

  void setNum(int n){
    num = n;
    System.out.println("ナンバーを" + num + "にしました。");
  }

  void setGas(double g){
    gas = g;
    System.out.println("ガソリン量を" + gas + "にしました。");
  }
  void show()
  {
    System.out.println("車のナンバーは" + num + "です。");
    System.out.println("ガソリン量は" + gas + "です。");
  }
}

class Sample4{
  public static void main(String[] args) {
    Car car1;
    car1 = new Car();

    int number = 1234;
    double gasoline = 20.5;

    car1.setNum (number);
    car1.setGas (gasoline);

  }
}
