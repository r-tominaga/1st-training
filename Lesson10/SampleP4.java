import java.io.*;

class SampleP4{
  public static void main(String[] args)throws IOException {
    System.out.println("整数を2つ入力してください。");

    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

    String str1 = br.readLine();
    String str2 = br.readLine();

    int num1 = Integer.parseInt(str1);
    int num2 = Integer.parseInt(str2);

    int ans = Math.min(num1, num2);

    System.out.println(num1 + "と" + num2 + "のうち小さい方は" + ans + "です。");
  }
}
