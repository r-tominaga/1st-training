import java.util.Random;
class Montyhall{
  public static void main(String[] args) {
    Random rnd1 = new Random();
    Random rnd2 = new Random();
    Random rnd3 = new Random();

    int sld = 0;
    int cord = 0;
    int cntAC = 0;
    int cntNC = 0;
    int rand = 0;
    int shwd = 0;
    int n = 10000;
        //最初に選択するドアを決める
        for(int i=0; i<n; i++){
          int ran = rnd1.nextInt(3);
          if(ran == 0){
            sld = 0;
          }else if(ran == 1){
            sld = 1;
          }else{
            sld = 2;
          }
          //正解のドアを決める
          for(int s=0; s<n; s++){
            rand = rnd2.nextInt(3);
            if(rand == 0){
              cord = 0;
            }else if(rand == 1){
              cord = 1;
            }else{
              cord = 2;
            }
          }
          //最初から正解のドアを選んできた場合
          if(sld == cord){
            shwd = (sld + (rnd3.nextInt(2)+1))%3;
            cntNC++;
          }else if((sld + cord) == 1){
            shwd = 2;
            cntAC++;
          }else if((sld + cord) == 2){
            shwd = 1;
            cntAC++;
          }else if((sld + cord) == 3){
            shwd = 0;
            cntAC++;
          }

        }

        System.out.println(cntAC);
        System.out.println(cntNC);


  }
}