import java.io.*;

class Sample4
{
    public static void main(String[] args)throws IOException {

      System.out.println("整数を入力してください。");

      BufferedReader br =
       new BufferedReader(new InputStreamReader(System.in));

      String str1 = br.readLine();
      int res =Integer.parseInt(str1);

      if(res == 1){
        System.out.println("1が入力されました。");
      } else if (res == 2) {
        System.out.println("2が入力されました。");
      } else {
        System.out.println("1か2を入力してください。");
        String str2 = br.readLine();
        int res2 =Integer.parseInt(str2);

//if(1 ==  res2)になることも
        if(res2 == 1) {
          System.out.println("1が入力されました。");
        } else if (res2 == 2) {
          System.out.println("2が入力されました。");
        } else {
          System.out.println("1か2を入力してください。");
        }

      }

    }
}
