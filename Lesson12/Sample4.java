interface iVehicle{
  void vShow();
  }
interface iMaterial{
  void mShow();
}

class Car implements iVehicle, iMaterial{
  private int num;
  private double gas;

  public Car(int n, double g){
    num = n;
    gas = g;
    System.out.println
    ("ナンバーを" + num + " ガソリン量" + gas + "の車を作成しました。");
  }
  public void vShow(){
    System.out.println("車のナンバーは" + num);
    System.out.println("ガソリン量は" + gas);
  }
  public void mShow(){
    System.out.println("車の材質は鉄です");
}
}

class Sample4{
  public static void main(String[] args) {
    Car car1 = new Car(1234, 20.5);
    car1.vShow();
    car1.mShow();
  }
}
