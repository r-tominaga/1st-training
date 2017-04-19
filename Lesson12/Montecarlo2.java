class Montecarlo2{
  public static void main(String[] args) {
    double x = 0.0;
    double y = 0.0;
    double n = 0.0;
    while(x < 1.0 && y < 1.0){
      if((x*x)+(y*y) < 1.0){
          n++;
        }else{
          ;
        }
        x = x + 0.001;
      if(x > 1.0){
        x = 0.0;
        y = y + 0.001;
      }else{
        ;
      }
    }
    System.out.println(4*n);
  }
}
