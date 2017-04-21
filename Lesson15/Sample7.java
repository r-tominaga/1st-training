class Company{
  private int sum = 0;

  public void add(int a){
    int tmp = sum;
    System.out.println("現在、合計額は" + tmp + "円です。");
    System.out.println(a + "円稼ぎました。");
    tmp = tmp + a;
    System.out.println("合計額を"+ tmp +"円にします。");
    sum = tmp;
  }
}

class Driver extends Thread{
  private Company comp;

  public Driver(Company c){
    comp = c;
  }
  public void run(){
    for(int i=0; i<3; i++){
      comp.add(50);
    }
  }
}

class Sample7{
  public static void main(String[] args) {
    Company cmp = new Company();

    Driver drv1 = new Driver(cmp);
    drv1.start();

    Driver drv2 = new Driver(cmp);
    drv2.start();
 }
}
