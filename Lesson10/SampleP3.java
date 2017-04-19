import java.io.*;

class SampleP3{
  public static void main(String[] args)throws IOException {
    System.out.println("文字列を入力してください。");

    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

    String str1 = br.readLine();

    System.out.println("aの挿入位置を入力してください。");

    String str2 = br.readLine();
    int pose = Integer.parseInt(str2) -1;

    StringBuffer sb = new StringBuffer(str1);

    sb.insert(pose, 'a');

    System.out.println(str1 + "の" + str1 + "番目にaを追加すると" + sb + "です。");
  }
}
