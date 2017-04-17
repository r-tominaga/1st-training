import java.io.*;

class SampleP2{
  public static void main(String[] args)throws IOException {
    System.out.println("文字列を入力してください。");
    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

    String str1 = br.readLine();

    StringBuffer str2 = new StringBuffer(str1);
    str2.reverse();

    System.out.println(str1 + "を逆順にすると" + str2 + "です。");
  }
}
