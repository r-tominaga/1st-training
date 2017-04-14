class MyPoint{
    int x;
    int y;

    void setX(int px){
      x = px;
    }

    void setY(int py){
      y = py;
    }

    int getX(){
      return x;
    }

    int getY(){
      return y;
    }
}

class SampleP5{
  public static void main(String[] args) {
    MyPoint point1 = new MyPoint();

    point1.setX(5);

    point1.setY(10);

    int xx = point1.getX();
    int yy = point1.getY();

    System.out.println("xの座標は" + xx + "yの座標は" + yy + "です。");
  }
}
