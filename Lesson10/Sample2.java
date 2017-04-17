import java.io.*;

class Sample2{
  public static void main(String[] args)throws IOException {
    System.out.println("英字を入力してください。");
    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

    String str = br.readLine();

    String stru = str.toUpperCase();
    String str1 = str.toLowerCase();

    System.out.println("大文字に変換すると" + stru + "です。");
    System.out.println("小文字に変換すると" + str1 + "です。");
  }
}
