class Car{
  int num;
  double gas;
}

class Sample2{
  Car car1 = new car();

  car1.num = 1013;
  car1.gas = 30.2;

  System.out.println("車のナンバーは" + car1.num + "です。");
  System.out.println("車のガソリン量は" + car1.gas + "です。");
}
