class Sample10
{
  public static void main(String[] args){
    int[][] test = {
          {80,60,22,50},{90,55,68,72},{33,75,63}
    };

    for(int i=0; i<test.length; i++){
      System.out.println((i+1) + "番目の配列要素の長さは" + test[i].length + "です。");
    }
  }
}
