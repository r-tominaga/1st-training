import java.io.*;
import java.util.*;
import java.util.Map.Entry;

class WordBook{
  public static void main(String[] args) {
    String str ="dog, cat, cow cow 'Pig' 2elephant bird bird. bull stone right left king low law reel fire tree knight wall ball ball pop pop law left stone cow egg egg egg egg dream fire";
    String[] tango = str.split("[ ,.\"\'’]+");
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

    }
  }
