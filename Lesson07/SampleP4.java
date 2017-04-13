import java.io.*;

class SampleP4
{
  public static void main(String[] args)throws IOException {

    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

    System.out.println("5人の点数を入力してください。");

    int[] test = new int[5];

    for(int i=0; i<5; i++){
      String str = br.readLine();
      int tmp = Integer.parseInt(str);
      test[i] = tmp;
    }

    for(int i=0; i<5; i++){
      System.out.println((i+1) + "番目の人の点数は" + test[i] + "です。");
    }

    int max = 0;

    for (int i=0;i<5 ;i++) {
      if(max < test[i+1]){
        max = test[i+1];
      }
    }
    System.out.println("最高点は" + max + "点です。");
   }
 }
