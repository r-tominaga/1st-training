import java.io.*;

class Samplekaibun{
  public static void main(String[] args) {
  int ten = 11;
  while(true){
    String strten = String.valueOf(ten);
    StringBuffer revten = new StringBuffer(strten);
    revten.reverse();
    String str = revten.toString();
    int num = Integer.parseInt(str);

    String two = Integer.toBinaryString(num);
    int bin = Integer.parseInt(two);
    StringBuffer revtwo = new StringBuffer(two);
    revtwo.reverse();
    String str2 = revtwo.toString();
    int num2 = Integer.parseInt(str2);

    String eight = Integer.toOctalString(num);
    int oct = Integer.parseInt(eight);
    StringBuffer reveight = new StringBuffer(eight);
    reveight.reverse();
    String str8 = reveight.toString();
    int num8 = Integer.parseInt(str8);

    if(num == ten && bin == num2 && oct == num8){
      System.out.println(num);
      System.out.println(num2);
      System.out.println(num8);
        break;
      }else{
        ten = ten + 2;
      }
  }

  //int型をString型に変換
  //String str = String.valueOf(val);
  /*String bin = Integer.toBinaryString(dec);
  System.out.println(bin); */
  }
}
