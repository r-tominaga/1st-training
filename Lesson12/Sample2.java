import java.io.*;
class Sample2{
  public static void main(String[] args)throws IOException {
    System.out.println("テキストを入力してください。");
    BufferedReader br =
      new BufferedReader(new InputStreamReader(System.in));

    String importext = br.readLine();
    String scha = "a";
    int counta = 0;
    int s = 0;
    while (s < importext.length()) {
        int index = importext.indexOf(scha, s);
        s = (importext.indexOf(scha, s) + scha.length());
        counta++;
    }
    System.out.println(counta);

    String schb = "b";
    int countb = 0;
    s = 0;
    while (s < importext.length()) {
        int index = importext.indexOf(schb, s);
        s = (importext.indexOf(scha, s) + schb.length());
        countb++;
    }
    System.out.println(countb);
}
}
