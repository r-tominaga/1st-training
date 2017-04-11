import java.io.*;

class Sample11
{
  public static void main(String[] args) throws IOException
  {
    System.out.println("整数を入力してください。");

    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

    String str1 = br.readLine();
    int num1 = Integer.parseInt(str1);

    if(0<=num1 && num1<=10){
      System.out.println("正解です。");
    }else{
      System.out.println("まちがいです。");

    }


    }



}
