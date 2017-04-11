import java.io.*;

class Sample12
{
  public static void main(String[] args) throws IOException
  {
    System.out.println("5段階の成績をを入力してください。");

    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

    String str1 = br.readLine();
    int num1 = Integer.parseInt(str1);

    switch(num1){
      case 1:
        System.out.println("もっとがんばりましょう。");
        break;
      case 2:
        System.out.println("もう少しがんばりましょう。");
        break;
      case 3:
        System.out.println("さらに上を目指しましょう。");
        break;
      case 4:
        System.out.println("たいへんよくできました。");
      case 5:
        System.out.println("たいへん優秀です。");
        break;
    }

    }

}
