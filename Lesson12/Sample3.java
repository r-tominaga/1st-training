interface iVehicle{
  void show();
  }

class Car implements iVehicle{
  private int num;
  private double gas;

  public Car(int n, double g){
    num = n;
    gas = g;
    System.out.println
    ("ナンバーを" + num + " ガソリン量" + gas + "の車を作成しました。");
  }
  public void show(){
    System.out.println("車のナンバーは" + num);
    System.out.println("ガソリン量は" + gas);
  }
}

class Plane implements iVehicle{
  private int flight;

  public Plane(int f){
    flight = f;
    System.out.println("便" + flight + "の飛行機を作成しました。");
  }
  public void show(){
    System.out.println("飛行機の便は" + flight + "です。");
  }
}

class Sample3{
  public static void main(String[] args) {
    iVehicle[] ivc;
    ivc = new iVehicle[2];

    ivc[0] = new Car(1234, 20.5);

    ivc[1] = new Plane(232);

    for(int i=0; i<ivc.length; i++){
      ivc[i].show();
  }
}
}
