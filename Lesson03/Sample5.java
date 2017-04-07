import java.io.*;
class Sample5
{
  public static void main(String[] args) throws IOException
  {
    System.out.println("整数を入力してください。");

    BufferedReader br =
      new BufferedReader(new InputStreamReader(System.in));

    String num = br.readLine();

    System.out.println(num + "が入力されました");
  }
}
