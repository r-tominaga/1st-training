import java.io.*;

class Sample2
{
    public static void main(String[] args)throws IOException {

      System.out.println("整数を入力してください。");

      BufferedReader br =
       new BufferedReader(new InputStreamReader(System.in));

      String str = br.readLine();
      int res =Integer.parseInt(str);

      if(res == 1){
        System.out.println("1が入力されました。");
        System.out.println("1が選択されました。");
      }
      
      System.out.println("処理を終了します。");
    }
}
