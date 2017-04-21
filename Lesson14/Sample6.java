import java.io.*;

class Sample6{
  public static void main(String[] args) {
    System.out.println("文字列を入力してください。");

    try{
      BufferedReader br =
       new BufferedReader(new InputStreamReader(System.in));

      String str = br.readLine();
      System.out.println(str +"が入力されました。");
    }
    catch(IOException e){
      System.out.println("入出力エラーです。");
    }
  }
}
