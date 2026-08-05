// Dynamic Method Dispatch, Upcasting , Downcasting, instanceof Operator

class parent
{
     void m1()
     {
          System.out.println("Method m1 of Parent Class");
     }  
     void m4()
     {
          System.out.println("Method m4 of Parent Class");
     } 
}
class child extends parent
{
     void m1()
     {
          System.out.println("Method m1 of Child Class");
     } 
     void m2()
     {
          System.out.println("Method m2 of Child Class");
     }

}
class run
{
     public static void main(String[] args) 
     {
          parent p = new parent();
          p.m1(); //Method m1 of Parent Class
          p.m4(); //Method m4 of Parent Class

          child c = new child();
          c.m1(); //Method m1 of Child Class
          c.m2(); //Method m2 of Child Class
          c.m4(); //Method m4 of Parent Class
        // ------------------ Upcasting ------------------------ //
          parent pob1 = new child(); // Upcasting (Implicit)
          pob1.m1(); // Method m1 of Child Class
          pob1.m4(); // Method m4 of Parent Class

          parent pob2 = (parent) new child(); // Upcasting (Explicit)
          pob2.m1(); // Method m1 of Child Class
          pob2.m4(); // Method m4 of Parent Class

          // ----------------- Downcasting ---------------------//
          // child cob1 = new parent(); // Implicit downcasting
          //CE: Type mismatch: cannot convert from parent to child

           // child cob1 = (child) new parent(); // Explicit downcasting
          //RE: java.lang.ClassCastException

          //child dc = pob2;
          //CE: Type mismatch: cannot convert from parent to child

          child dc1 =(child) pob2; // Downcasting (Explicit)
          dc1.m1(); //Method m1 of Child Class
          dc1.m2(); //Method m2 of Child Class
          dc1.m4(); //Method m4 of Parent Class

          //-------------instanceof Operator --------------//
          System.out.println(p instanceof parent); //true
          System.out.println(p instanceof child); //false
          System.out.println(c instanceof child); //true
          System.out.println(c instanceof parent); //true
          System.out.println(pob1 instanceof child); //true
          System.out.println(pob1 instanceof parent); //true  

          // Special Cases]
          String s = "PAT";
          System.out.println(s instanceof String); //true
          System.out.println(s instanceof Object); //true
          //System.out.println(s instanceof StringBuffer);
          //CE: Incompatible conditional operand types String and StringBuffer

          System.out.println(null instanceof String); //false
          System.out.println(null instanceof StringBuffer); //false
          System.out.println(null instanceof Object); //false
     }
}


