class Car{
  protected int num;
  protected double gas;

  public Car(){
    num = 0;
    gas = 0.0;
    System.out.println("車を作成しました。");
  }
  public Car(int n, double g){
    num = n;
    gas = g;
    System.out.println("ナンバー" + num + "ガソリン量" + gas + "の車を作成しました。");
  }
  public void setCar(int n, double g){
    num = n;
    gas = g;
    System.out.println("ナンバーを"+ num + "にガソリン量を" + gas + "にしました。");
  }
  public void show(){
    System.out.println("車のナンバーは" + num + "です。");
    System.out.println("ガソリン量は" + gas + "です。");
  }
}

class RacingCar extends Car{
  private int course;

  public RacingCar(){
    course = 0;
    System.out.println("レーシングカーを作成しました。");
  }
  public RacingCar(int n, double g, int c){
    //superクラスの引数2個のコンストラクタを呼び出し
    super(n, g);
    course = c;
    System.out.println("コース番号" + course + "のレーシングカーを作成しました。");
  }
  public void setCourse(int c){
    course = c;
    System.out.println("コース番号を" + course + "にしました。");
  }
  public void show(){
    System.out.println("レーシングカーのナンバーは" + num + "です。");
    System.out.println("ガソリン量は" + gas + "です。");
    System.out.println("コース番号は" + course + "です。");
  }
}

class Sample5{
  public static void main(String[] args) {
    Car car1 = new RacingCar();

    car1.setCar(1234,20.5);
    //Carクラスの変数だからといってCarクラスのメソッドが呼ばれるわけではない。
    //オブジェクトがRacingCarで作成されているので呼ばれるのはRacingCarクラスのメソッド
    car1.show();
  }
}
