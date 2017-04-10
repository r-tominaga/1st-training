import java.io.*;

class Sample6
{
  public static void main(String[] args) throws IOException{
    System.out.println("整数を3つ入力してください。");

    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

    String str1 = br.readLine();
    String str2 = br.readLine();
    String str3 = br.readLine();

    double sum = 0;

    sum += Double.parseDouble(str1);
    sum += Double.parseDouble(str2);
    sum += Double.parseDouble(str3);

    System.out.println("3つの数の合計は" + sum + "です。");

    }
}
