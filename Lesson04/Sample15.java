import java.io.*;

class Sample15
{
  public static void main(String[] args) throws IOException{

    System.out.println("5人の年収を入力してください。");

    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

    String str1 = br.readLine();
    String str2 = br.readLine();
    String str3 = br.readLine();
    String str4 = br.readLine();
    String str5 = br.readLine();

    int num1 = Integer.parseInt(str1);
    int num2 = Integer.parseInt(str2);
    int num3 = Integer.parseInt(str3);
    int num4 = Integer.parseInt(str4);
    int num5 = Integer.parseInt(str5);

    int total_salary  = (num1+num2+num3+num4+num5);
    double average_salary = total_salary/5;


    System.out.println("5人の平均年収は" + average_salary + "億円です。");

  }
}
