class Sample3
{
  public static void main(String[] args)
  {
    int[] test = new int[5];

    test[0] = 985;
    test[1] = 65;
    test[2] = 567;
    test[3] = 32;
    test[4] = 9;

    for(int i=0; i<5; i++){
      System.out.println((i+1) + "番目の人の点数は" + test[i] + "です。");
    }
  }
}
