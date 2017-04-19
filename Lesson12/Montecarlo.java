class Montecarlo{
  public static void main(String[] args) {
    double x;
    double y;
    double n = 0.0;
    for(int i=0; i<100000000; i++){
      x = Math.random();
      y = Math.random();
        if((x*x)+(y*y) < 1.0){
          n++;
        }else{
          ;
        }
    }
    System.out.println(4*n);
  }
}
