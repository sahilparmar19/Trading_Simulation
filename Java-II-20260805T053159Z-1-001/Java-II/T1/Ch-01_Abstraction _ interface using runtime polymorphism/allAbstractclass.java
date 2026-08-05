abstract class abs
{
    abstract void m1(); // default method
    //final abstract void m2(); // CE
    //private abstract void m2(); //CE
    //static abstract void m2(); // CE
    public abstract void m2();
    protected abstract void m3();

    //----------concrete methods-------------//
    void m4(){}
    final void m5(){}
    private void m6(){}
    static void m7(){}
    protected void m8(){}

    //------variables-----------------//
     int x =10;
     final int x1 = 20;
     static final int x2 = 30;
     public int x3 = 40;
     protected int x4 = 50; 
     int x5 ;

    //-------------static block---------------//
    static
    {
        System.out.println("Static block of abstract class abs");
        // static block will  execute automatically after creation of object
    }
     //-------------instance block---------------//
    {
        System.out.println("Instance block of abstract class abs");
        // instance block will execute after execution of static block
    }

    //--------------constructor---------------//
    abs()
    {
            System.out.println("abs constructor called");
    }
}
abstract class childabs extends abs
{
    void m1(){System.out.println("m1 method of childabs");}
    public void m2(){System.out.println("m2 method of childabs");}

    void m99(){}

    childabs()
    {
        System.out.println("childabs constructor called");
    }
    
}
class subchildabs extends childabs
{
    protected void m3(){System.out.println("m3 method of childabs");}// or public void m3(){}

    void m100(){}

    subchildabs()
    {
        System.out.println("subchiled constructor called");
    }
}
class run
{
    public static void main(String[] args) 
    {
        subchildabs ob = new subchildabs();
        ob.m1();  // all methods except m6 can be called using this object 
        
        childabs ob1 = new  subchildabs();
        ob1.m1(); // all methods except m6 can be called using this object  

        abs ob2 = new subchildabs();
        ob2.m1(); // all methods except m6,m99,m100 can be called using this object  


    }
}