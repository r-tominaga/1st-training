import java.io.*;
import java.util.*;
import java.util.Map.Entry;

class WordBook{
  public static void main(String[] args) {
    if(args.length != 1){
      System.out.println("ファイル名を正しく指定してください。");
      System.exit(1);
    }
    try{

    BufferedReader br = new BufferedReader(new FileReader(args[0]));

    String str;

    while((str = br.readLine()) != null){
        ;
    }

    String[] tango = str.split("[ ,.\"\'’1234567890]+");

    for(int i=0; i<tango.length; i++){
      System.out.println(tango[i]);
    }

    Map<String, Integer> m = new HashMap<String, Integer>();

    for(String s : tango){
      int v;
      if(m.containsKey(s)){
        // Mapに登録済
        v = m.get(s) + 1;
        }else{
        // Mapに未登録
          v = 1;
        }
        m.put(s, v);
    }

    for(Entry<String, Integer> entry : m.entrySet()){
      System.out.printf("%s %d%n", entry.getKey(), entry.getValue());
    }
    br.close();
   }
    catch(IOException e){
      System.out.println("入出力エラーです。");
    }
  }
}
