class Car{
  int num;
  double gas;

  void show(){
    System.out.println("車のナンバーは" + num + "です。");
    System.out.println("ガソリンの量は" + gas + "です。");
  }
}
class Sample2{
  public static void main(String[] args) {
    Car car1;
    car1 = new Car();

    car1.num = 1013;
    car1.gas = 30.2;

    car1.show();
    car1.show();
  }
 }
