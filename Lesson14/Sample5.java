class CarException extends Exception{
}

class Car{
  private int num;
  private double gas;

  public Car(){
    num = 0;
    gas = 0.0;
    System.out.println("車の作成をしました。");
  }
  public void　setCar(int n, double g) throws CarException{
    if(g<0){
      CarException e = new CarException();
      throw e;
    }else{
      num = n;
      gas = g;
      System.out.println("ナンバーを"+ num +"にガソリン量を"+ gas +"にしました。");
    }
  }
  public void show(){
    System.out.println("車のナンバーは"+ num +"です。");
    System.out.println("ガソリン量は"+ gas +"です。");
  }
}

class Sample5{
  public static void main(String[] args) {
    Car car1 = new Car();
    try{
      car1.setCar(1234, -10.0);
    }
    catch(ArrayIndexOutOfBoundsException e){
      System.out.println("e +"が送出されました。"");
    }
    car1.show();
  }
}
