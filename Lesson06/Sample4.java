import java.io.*;

class Sample4
{
  public static void main(String[] args) throws IOException
  {
    System.out.println("いくつまでの掛け合わせを求めますか？");

    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

     String str = br.readLine();
     int num = Integer.parseInt(str);

     int total = 1;
     for(int i=1; i<=num; i++){
       //System.out.print("");
       total *= i;
     }

     System.out.println("1から" + num + "までの合計は" + total + "です。");
  }
}
