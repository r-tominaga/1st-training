import java.io.*;

class Sample3
{
  public static void main(String[] args) throws IOException {
    System.out.println("整数を2つ入力してください。");

    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

    String str1 = br.readLine();
    String str2 = br.readLine();

    int num1 = Integer.parseInt(str1);
    int num2 = Integer.parseInt(str2);

    System.out.println("足し算の結果は[" + (num1 + num2) +"]です。");
  }
}
