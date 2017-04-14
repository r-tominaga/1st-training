class Car{
  public static int sum = 0;

  private int num;
  private double gas;

  public Car(){
    num =0;
    gas = 0.0;
    sum++;
    System.out.println("車を作成しました。");
  }

  public void setCar(int n, double g){
    num = n;
    gas = g;
    System.out.println("ナンバーを" + num + "ガソリン量を" + gas + "にしました。");
  }
  public static void showSum(){
    System.out.println(num);
    System.out.println("車は全部で" + sum + "台あります。");
  }
  public void show(){
    System.out.println("車のナンバーは" + num + "です。");
    System.out.println("ガソリン量は" + gas + "です。");
  }
}

class Sample8{
  public static void main(String[] args) {
    Car car1 = new Car();
    car1.setCar(1234, 20.5);
    car1.show();

    car1.showSum();

    Car car2 = new Car();
    car2.setCar(4567, 30.5);

    car2.showSum();
  }
}
