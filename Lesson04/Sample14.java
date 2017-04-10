import java.io.*;

class Sample14
{
  public static void main(String[] args) throws IOException{

    System.out.println("２辺の長さを指定してください。");

    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

    String str1 = br.readLine();
    String str2 = br.readLine();

    int num1 = Integer.parseInt(str1);
    int num2 = Integer.parseInt(str1);

    int sq = num1*num2;

    System.out.println("長方形の面積は" + sq + "cm^2です。");

  }
}
