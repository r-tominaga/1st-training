class Sample1
{
  public static void main(String[] args)
  {
    int[] test;
    test = new int[5];

    test[0] = 45;
    test[1] = 85;
    test[2] = 64;
    test[3] = 100;
    test[4] = 99999;

    for(int i=0; i<6; i++){
      System.out.println((i+1) + "番目の人の点数は" + test[i] + "です。");
    }
  }
}
