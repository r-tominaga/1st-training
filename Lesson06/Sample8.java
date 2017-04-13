class Sample8
{
  public static void main(String[] args)
  {
    boolean boo = false;
    for(int i=0; i<5; i++){
      for(int j=0; j<5; j++){
        if(boo){
          System.out.print("*");
          boo = true;
        }else{
          System.out.print("-");
          boo = false;
        }
      }
      System.out.print("\n");
    }
  }
}
