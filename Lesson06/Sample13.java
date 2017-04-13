import java.io.*;
class Sample13
{
  public static void main(String[] args)throws IOException
  {
    System.out.println("テストの点数を入力してください。(0で終了)");

    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

     int sum = 0;
     int test = 0;

     do{
       String str = br.readLine();
       test = Integer.parseInt(str);
       sum += test;
     }while(test !=0);
         System.out.println(sum);
     }
   }
