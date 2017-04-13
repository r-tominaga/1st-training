class Sample10
{
  public static void main(String[] args){
    int[][] test;
    test = new[2][5];
    //国語の点数
    test[0][0] = 80;
    test[0][1] = 60;
    test[0][2] = 22;
    test[0][3] = 50;
    test[0][4] = 54;
    //数学の点数
    test[1][0] = 43;
    test[1][1] = 80;
    test[1][2] = 100;
    test[1][3] = 200;
    test[1][4] = 4;

    for(int i=0; i<5; i++){
      System.out.println((i+1) + "番目の人の国語の点数は" + test[0][i] + "です。");
      System.out.println((i+1) + "番目の人の数学の点数は" + test[1][i] + "です。");
    }
  }
}
