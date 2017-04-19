class Car{
  private int num;
  private double gas;

  public Car(){
    num = 0;
    gas = 0.0;
    System.out.println("車を作成しました。");
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
  public void setCourse(int c){
    course = c;
    System.out.println("コース番号を" + course + "にしました。");
  }
}
class Sample1{
  public static void main(String[] args) {
    RacingCar rccar1;
    rccar1 = new RacingCar();

    rccar1.setCar(1234, 20.5);
    rccar1.setCourse(5);
  }
}
