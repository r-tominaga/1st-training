class Car{
  private int num;
  private double gas;

  public void setCar(int n) {
    num = n;
    System.out.println("ナンバーを" + num + "にしました。");
  }
  public void setCar(double g){
    gas = g;
    System.out.println("ガソリン量は" + gas + "にしました。");
  }
  public void setCar(int n, double g){
    num = n;
    gas = g;
    System.out.println("ナンバーを" + num + "にガソリン量を" + gas + "にしました。");
  }
  public void show(){
    System.out.println("車のナンバーは" + num + "です。");
    System.out.println("ガソリン量は" + gas + "です。");
  }
}

class Sample3{
  public static void main(String[] args) {
    Car car1 = new Car();

    car1.setCar(1234, 20.5);
    car1.show();

    System.out.println("車のナンバーだけ変更してみます。");
    car1.setCar(2345);
    car1.show();

    System.out.println("ガソリン量だけ変更してみます。");
    car1.setCar(30.5);
    car1.show();
  }

}
