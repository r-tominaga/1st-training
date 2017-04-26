import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.*;

class WordBook{
  public static void main(String[] args) throws IOException{
    if(args.length != 1){
      System.out.println("ファイル名を正しく指定してください。");
      System.exit(1);
    }
    String regex = ".txt$";
    Pattern p = Pattern.compile(regex);

    Matcher mch = p.matcher(args[0]);

    if(!mch.find()){
      System.out.println(".txtのファイルを指定してください。");
      System.exit(1);
    }

    try{
      ArrayList<String> tango = new ArrayList<String>();

      BufferedReader br = new BufferedReader(new FileReader(args[0]));
      BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));

      String str;

      while((str = br.readLine()) != null){
          String str2 = str.replaceAll("[0-9]+" , "").trim().toLowerCase();
          String[] strs = str2.split("[\\s ,.\"\'’\n\b\t1234567890]+");
          for(String s:strs)
            tango.add (s);
      }

      Map<String, Integer> m1 = new HashMap<String, Integer>();

      for(String s : tango){
        int v;
        if(m1.containsKey(s)){
          // Mapに登録済
          v = m1.get(s) + 1;
        }else{
          // Mapに未登録
          v = 1;
        }
        m1.put(s, v);
      }

      for(Entry<String, Integer> entry : m1.entrySet()){
        System.out.printf("%s %d%n", entry.getKey(), entry.getValue());
      }

      List<Entry<String, Integer>> list_entries = new ArrayList<Entry<String, Integer>>(m1.entrySet());

      // 比較関数Comparatorを使用して.Entryの値を比較する(降順)
      Collections.sort(list_entries, new Comparator<Entry<String, Integer>>(){
        public int compare(Entry<String, Integer> obj1, Entry<String, Integer> obj2){
          return obj2.getValue().compareTo(obj1.getValue());
        }
      });

      // ループで要素順に値を取得する
      List<Entry<String, Integer>> entry2 = new ArrayList<Entry<String, Integer>>();

      for (int i=0; i<list_entries.size(); i++){
        entry2.add(list_entries.get(i));
        System.out.println(entry2.get(i).getKey() + " : " + entry2.get(i).getValue());
      }

      Map<String, String> m2 = new HashMap<String, String>();

      for(int i=0; i<20; i++){
        String m2key = entry2.get(i).getKey();
        System.out.println(m2key + "の意味を教えてください。");
        String m2value = br2.readLine();
        m2.put(m2key, m2value);
      }

      System.out.println("★☆★☆英単語一覧★☆★☆");
      System.out.print(String.format("%8s", "出現個数"));
      System.out.print(String.format("%8s", "英単語"));
      System.out.println(String.format("%8s", "日本語訳"));

      for(int i=0; i<20; i++){
        System.out.print(String.format("%8d", entry2.get(i).getValue()));
        System.out.print(String.format("%15s", entry2.get(i).getKey()));
        System.out.println(String.format("%10s", m2.get(entry2.get(i).getKey())));
      }
      br.close();
    }
    catch(IOException e){
      System.out.println("入出力エラーです。");
    }
  }
}
