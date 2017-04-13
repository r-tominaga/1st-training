import java.io.*;

class SampleP5
{
  public static void main(String[] args)throws IOException {

    BufferedReader br =
     new BufferedReader(new InputStreamReader(System.in));

    System.out.println("100人の点数を入力してください。");

    int[] test = new int[100];

    for(int i=0; i<100; i++){
      String str = br.readLine();
      int tmp = Integer.parseInt(str);
      test[i] = tmp;
    }

  /*  int max = 0;
    int s = test.length;

    for (int i=0;i<test.length-1 ;i++) {
      if(max < test[i+1]){
        max = test[i+1];
      }
    }*/

    System.out.println("探したい数を入力してください。");

    String str = br.readLine();
    int sch = Integer.parseInt(str);
    int half = (test.length - 1) /2;


    int max = test.length-1;
    int min = 0;

    while (sch != test[half] && min < max) {
        if(sch < test[half]){
          max = half;
          half = half/2;
        }else{
          min = half;
          half = (min + max)/2;
        }
    }

    System.out.println(test[half]);
   }
 }
