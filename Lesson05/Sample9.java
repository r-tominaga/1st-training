import java.io.*;

class Sample9
{
  public static void main(String[] args) throws IOException
  {
    System.out.println("整数を入力してください。");

    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

    String str = br.readLine();
    int num = Integer.parseInt(str);

    if(num%2 == 1){
      System.out.println(num +  "は奇数です。");
    }else{
      System.out.println(num + "は偶数です。");
    }

    System.out.println("終了します。");

    }



}
