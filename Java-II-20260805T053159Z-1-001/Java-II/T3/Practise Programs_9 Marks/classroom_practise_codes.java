====== LJ student practise programs for reading reference only to understand logics========== 
====== There may chance of some errors, so rectify errors in your own way if any ============
====== Avoid printing this file and save paper ============
/*
 * Aim:-InputStreamReader for reading data in byte Stream
 */
import java.io.*;

class Ex100 {
    public static void main(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream("A2.txt"); // Byte Stream
        InputStreamReader isr = new InputStreamReader(fis); // Works as Bridge
        BufferedReader br = new BufferedReader(isr); // Character Stream
        String line = br.readLine();
        System.out.println(line);

    }

}
/*
 * Aim : FileInputStream class and its method 
*/

// import statements
import java.io.*;

// main class
class Ex101 {
    // main method compulsory handles the exception
    public static void main(String[] args) throws IOException {
        // creating object of file for making byte array
        File file = new File("test.txt");
        // creating object of file input stream
        FileInputStream fileInputStream = new FileInputStream(file);
        // creating byte array
        byte[] b = new byte[(int) file.length()];
        // reading from file
        fileInputStream.read(b);
        // how to convert byte array to String
        String s = new String(b);
        System.out.println(s); // giving proper output
        System.out.println(b); // hashcode
        // closing object
        fileInputStream.close();
    }
}
/*
    Aim  : Ask user to enter line1 and line2 about team India.
         - Ask him to create a new file and store both lines in byte stream.
         - Read data from the same file and print on console.
 */

import java.io.*;
import java.util.Scanner;

public class Ex102 {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter line1 : ");
        String line1 = sc.nextLine();
        System.out.println("Enter line2 : ");
        String line2 = sc.nextLine();
        System.out.println("Enter file name : ");
        String fileName = sc.nextLine();
        FileOutputStream fos1 = new FileOutputStream(fileName + ".txt");
        fos1.write((line1 + "\n").getBytes());
        fos1.write(line2.getBytes());
        fos1.close();
        System.out.println("Enter name of file2 : ");
        String file2 = sc.nextLine();
        FileInputStream fis = new FileInputStream(fileName + ".txt");
        FileOutputStream fos2 = new FileOutputStream(file2 + ".txt");

        // Read from file1 and write to file2

        int i = fis.read();
        while (i != -1) {
            fos2.write(i);
            i = fis.read();
        }
        fos2.close();
        fis.close();
    }
}
/*
 AIM :- ask user to create file 1 and enter 2 lines for team india 
        Create copy of this file in file 2 
        Both file name will be entered by user 
*/
import java.io.*;
import java.util.Scanner;

class Ex103 {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("enter line 1 : ");
        String line1 = sc.nextLine();
        System.out.println("enter line 2 : ");
        String line2 = sc.nextLine();
        System.out.println("enter file-1 name : ");
        String file1 = sc.nextLine();
        FileOutputStream fos1 = new FileOutputStream(file1+".txt");
        fos1.write((line1+"\n").getBytes());
        fos1.write(line2.getBytes());
        fos1.close();
        //read from file 1 and write to file-2
        System.out.println("enter file-2 name : ");
        String file2 = sc.nextLine();
        FileInputStream fis = new FileInputStream(file1+".txt");
        FileOutputStream fos2 = new FileOutputStream(file2+".txt");
        int i = fis.read();
        while(i!=-1){
            fos2.write(i);
            i = fis.read();
        }
        fos2.close();
        fis.close();
    }
}

/*
 AIM :- ask user to create file 1 and enter 2 lines for team india 
        Create copy of this file in file 2 where user will enter word-1 and word-2
        word-1 from the file-1 will be replaced by word-2 and atlast copy will be store in file-2
        Use character string class
        Both file name will be entered by user 
*/
import java.io.*;
import java.util.Scanner;

class Ex104 {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("enter line 1 : ");
        String line1 = sc.nextLine();
        System.out.println("enter line 2 : ");
        String line2 = sc.nextLine();
        System.out.println("enter file-1 name : ");
        String file1 = sc.nextLine();
        FileWriter fw1 = new FileWriter(file1 + ".txt");
        fw1.write((line1 + "\n"));
        fw1.write(line2);
        fw1.close();
        System.out.println("enter word-1");
        String word1 = sc.nextLine();
        System.out.println("enter word-2");
        String word2 = sc.nextLine();
        // read from file 1 and write to file-2
        System.out.println("enter file-2 name : ");
        String file2 = sc.nextLine();
        FileReader fr = new FileReader(file1 + ".txt");
        FileWriter fw2 = new FileWriter(file2 + ".txt");
        BufferedReader br = new BufferedReader(fr);
        String data = br.readLine();
        while (data != null) {
            data = data.replace(word1, word2);
            fw2.write(data + "\n");
            data = br.readLine();
        }
        fw2.close();
        fr.close();
        br.close();
    }
}
/*
 AIM :- RandomAccessFile
*/
import java.io.*;
import java.util.Scanner;
class Ex105 {
    public static void main(String[] args) throws IOException {
        RandomAccessFile raf = new RandomAccessFile("india.txt", "rw");
        System.out.println("Pointer : " + raf.getFilePointer());
        raf.writeInt(100);
        System.out.println("Pointer : " + raf.getFilePointer());
        raf.seek(0);
        System.out.println("Data    : " + raf.readInt());
        raf.writeDouble(10.5);
        System.out.println("Pointer : " + raf.getFilePointer());
        raf.seek(4);
        System.out.println("Data    : " + raf.readDouble());
        raf.writeUTF("Hi java");
        System.out.println("Pointer : " + raf.getFilePointer());
        raf.writeUTF("Lj LJU");
        System.out.println("Pointer : " + raf.getFilePointer());
        raf.seek(12);
        System.out.println("Data    : " + raf.readUTF());
        System.out.println("Data    : " + raf.readUTF());
        raf.seek(15);
        System.out.println("Data    : " + raf.readInt());
    }
}
/*
 AIM :- create a file with list of 2 digit integer number 
        user is going to enter 5 integer number 
        create a new file which stores all this integer number in sorted order
*/
import java.io.*;
import java.util.Scanner;
import java.util.Collections;
import java.util.ArrayList;

class Ex106 {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("enter file-1 name :");
        String file1 = sc.nextLine();
        FileWriter fw1 = new FileWriter(file1 + ".txt");
        System.out.println("enter file-2 name :");
        String file2 = sc.nextLine();
        for (int i = 1; i <= 5; i++) {
            System.out.println("enter 2 digit number " + i + ": ");
            int num = sc.nextInt();
            fw1.write(num + "\n");
        }
        fw1.close();
        ArrayList<Integer> al = new ArrayList<>();
        FileReader fr = new FileReader(file1 + ".txt");
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            al.add(Integer.parseInt(line));
            line = br.readLine();
        }
        System.out.println(al);
        Collections.sort(al);
        fr.close();
        br.close();
        FileWriter fw2 = new FileWriter(file2 + ".txt");
        BufferedWriter bw = new BufferedWriter(fw2);
        for (Integer i : al) {
            fw2.write(i + "\n");
        }
        fw2.close();

    }
}/*
 * Aim : copy image from location 1 to other
 */

import java.io.*;

public class Ex107 {
    public static void main(String[] args) throws IOException {
        // read image file (copy)
        File f = new File("D:/java/lj.png");
        FileInputStream fis = new FileInputStream(f);
        byte b[] = new byte[(int) f.length()];
        fis.read(b);
        fis.close();
        // wright data t another file
        FileOutputStream fos = new FileOutputStream("D:/lju.png");
        fos.write(b);
        fos.close();
    }
}

/*
  aim  : Ask prakhar to enter name of 5 famouse restorent of ahmedabad,
         Ask Ayush to enter name of 5 famouse restorent of ahmedabad.
         Store data in prakhar.txt and Ayush.txt
         create a new file with name : FinalList.txt
         which includes all unique restorent name given by prakhar and ayush in sorted order.
 */
import java.util.*;
import java.io.*;

public class Ex108 {
        public static void main(String[] args) throws IOException {
                Scanner sc = new Scanner(System.in);
                // create file objects
                File prakhar = new File("Prakhar.txt");
                File ayush = new File("Ayush.txt");
                // write data to file 1
                FileWriter fw1 = new FileWriter(prakhar);
                BufferedWriter bw1 = new BufferedWriter(fw1);

                for (int i = 0; i < 5; i++) {
                        System.out.print("Prakhar : Enter restorent name " + (i + 1) + " ==>");
                        String name = sc.nextLine();
                        bw1.write(name);
                        bw1.newLine();
                }
                bw1.close();
                fw1.close();

                // write data to file 2
                FileWriter fw2 = new FileWriter(prakhar);
                BufferedWriter bw2 = new BufferedWriter(fw1);

                for (int i = 0; i < 5; i++) {
                        System.out.print("Ayush : Enter restorent name " + (i + 1) + " ==>");
                        String name = sc.nextLine();
                        bw2.write(name);
                        bw2.newLine();
                }
                bw2.close();
                fw2.close();
                // create arraylist to store data of both files in different array list s
                ArrayList<String> al1 = new ArrayList<>();
                ArrayList<String> al2 = new ArrayList<>();
                FileReader fr1 = new FileReader(prakhar);
                FileReader fr2 = new FileReader(ayush);
                BufferedReader br1 = new BufferedReader(fr1);
                BufferedReader br2 = new BufferedReader(fr2);

                // read data from file 1 and store it to array list 1

                String line1 = br1.readLine();
                while (line1 != null) {
                        al1.add(line1);
                        line1 = br1.readLine();
                }

                br1.close();
                fr1.close();

                // read data from file 2 and store it to array list 2

                String line2 = br2.readLine();
                while (line2 != null) {
                        al2.add(line2);
                        line2 = br2.readLine();
                }

                br2.close();
                fr2.close();
                System.out.println("Prakhar Restaurant List : " + al1);
                System.out.println("Ayush Restaurant List : " + al2);

                // now merge addd both lists in to HashSet to make them Unique

                HashSet<String> mergeList = new HashSet<>();
                mergeList.addAll(al1);
                mergeList.addAll(al2);
                System.out.println("Merge List in hashSet : " + mergeList);

                // now to sort the list we need to again store in ArrayList
                ArrayList<String> finalSortedList = new ArrayList<>();
                finalSortedList.addAll(mergeList);

                Collections.sort(finalSortedList);

                System.out.println("Sorted List = " + finalSortedList);
                // fetch list from the Sorted List and Store into FinalList.txt

                FileWriter fw3 = new FileWriter("finalList.txt");
                BufferedWriter bw3 = new BufferedWriter(fw3);
                for (String s : finalSortedList) {
                        bw3.write(s);
                        bw3.newLine();
                }

                bw3.close();
                fw3.close();

        }
}
/*
 * 
 * Aim : Ask virat to enter 5 best places of india 
 * Ask rohit to enter 5 best places of india 
 * store this data in file virat.txt and rohit.txt respectively
 * merge both data by bringing out from both files
 * create a file final places.txt with all unique places data in sorted order 
 */

import java.util.*;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.*;
import java.nio.Buffer;

public class Ex109 {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        File virat = new File("virat.txt");
        File rohit = new File("rohit.txt");

        FileWriter fw1 = new FileWriter(virat);
        BufferedWriter bw1 = new BufferedWriter(fw1);

        for (int i = 0; i < 5; i++) {
            System.out.println("virat : enter place " + (i + 1) + " : ");
            String place = sc.nextLine();
            bw1.write(place);
            bw1.newLine();
        }
        bw1.close();
        fw1.close();

        FileWriter fw2 = new FileWriter(rohit);
        BufferedWriter bw2 = new BufferedWriter(fw2);

        for (int i = 0; i < 5; i++) {
            System.out.println("rohit : enter place " + (i + 1) + " : ");
            String place = sc.nextLine();
            bw2.write(place);
            bw2.newLine();
        }
        bw2.close();
        fw2.close();

        // read from the both files and create list of them

        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();

        // read from virat and store to list 1

        FileReader fr1 = new FileReader(virat);
        BufferedReader br1 = new BufferedReader(fr1);
        String line1 = br1.readLine();

        while (line1 != null) {
            list1.add(line1);
            line1 = br1.readLine();
        }

        System.out.println("viratlist = "+list1);

        br1.close();
        fr1.close();

        // read from rohit and store to list 2

        FileReader fr2 = new FileReader(rohit);
        BufferedReader br2 = new BufferedReader(fr2);
        String line2 = br2.readLine();

        while (line2 != null) {
            list2.add(line2);
            line2 = br2.readLine();
        }

        System.out.println("rohitlist = "+list2);

        br2.close();
        fr2.close();

        // add both list in Hashset

        HashSet<String> final1 = new HashSet<>();
        final1.addAll(list1);
        final1.addAll(list2);

        ArrayList<String> finallist = new ArrayList<>(final1);
        Collections.sort(finallist);

        // now writeing sorted list to array list
        
        FileWriter fw3 = new FileWriter("final.txt");
        BufferedWriter bw3 = new BufferedWriter(fw3);

        for (String s : finallist) {
            bw3.write(s);
            bw3.newLine();
        }

        bw3.close();
        fw3.close();

    }
}
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

public class EX81 
{
    public static void main(String[] args) 
    {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
                ad1.add(10);
                ad1.add(20);
                ad1.add(30);
                ad1.add(40);
                System.out.println("ad1 : "+ ad1);
                ad1.addFirst(50);
                ad1.addLast(60);
                System.out.println("ad1 : "+ ad1);
                System.out.println(ad1.offer(70));
                System.out.println(ad1.offerFirst(80));
                System.out.println(ad1.offerLast(90));
                System.out.println("ad1 : "+ ad1);
                System.out.println(ad1.remove(90));
                System.out.println("ad1 : "+ ad1);
                System.out.println(ad1.removeFirst());
                System.out.println(ad1.removeLast());
                System.out.println("ad1 : "+ ad1);
                System.out.println(ad1.poll());
                System.out.println(ad1.pollFirst());
                System.out.println(ad1.pollLast());
                System.out.println("ad1 : "+ ad1);
                System.out.println(ad1.peek());
                System.out.println(ad1.peekFirst());
                System.out.println(ad1.peekLast());
                ArrayDeque<Integer> ad2 = ad1.clone();
                System.out.println(ad1.equals(ad2));
                System.out.println(ad2);
                System.out.println(ad1);

                ArrayList<Integer> al1 = new ArrayList<>(Arrays.asList(10,20,30));
                ArrayList<Integer> al2 = (ArrayList)al1.clone();
        System.out.println(al1);
        System.out.println(al2);
        System.out.println(al1.equals(al2));
        System.out.println(al1.hashCode());
        System.out.println(al2.hashCode());
        ArrayDeque<Integer> ad3 = ad1;
        System.out.println(ad3);

    }
}
// AIM : its kiara's marriage photograph, photographer wants following task to do 
//        he has to arrange of bride and groom in proper method , guest from bride side should stand leftside  
//        guest from groom side should stand righttside  
//        write a java program to perfrom following operation on guest;
//        name,side ()height
//1. addGuest() if user enters any other side rather than bride and groom then ask him aghain until he enters correct details, remove guest by side
//2. removeGuestBySide()
//3. removeGuestByName()
//4. checkFirstOrLastGuest()
//5. display()
//6. displayGroomSide()
//7. displayBrideSide()
//8. SortByHeight()
//9. forExit()

import java.util.*;
import java.util.Collections;
import java.util.Comparator;
import  java.util.ArrayList;

public class EX82 {
    public static void main(String[] args) 
    {
        Scanner sc = new Scanner(System.in);
        EX82 e = new EX82();
        int ch;
        
        boolean b = true;
        while (b) 
        {
            System.out.println("1.ADD GUEST\t2.removeGuestBySide\t3.removeGuestByName\t4.checkFirstOrLastGuest\t5.display\t6.displayGroomSide\t7. displayBrideSide\t8.SortByHeight\t9.forExit"); 1

            ch = sc.nextInt();
            sc.nextLine();
           
            System.out.println("Enter your choice");
            switch (ch) 
            {
                case 1:
                e.addGuest();
                 break;

                case 2:
                e.removeGuestBySide();
                 break;

                    case 3:
                    e.removeGuestByName();
                    break;

                    case 4:
                    e.checkFirstOrLastGuest();
                    break;

                    case 5:
                    e.display();
                    break;

                    case 6:
                    e.displayGroomSide();
                    break;

                    case 7:
                    e.displayBrideSide();
                    break;

                    case 8:
                    e.SortByHeight();
                    break;

                    case 9:
                    System.out.println("THANKYOU FOR ATTENDING THE WEDDING");
                    b = false;
                    break;

                default:
                System.out.println("Please enter the valid choice");
                    break;
            }   
        }
    }
    Scanner sc = new Scanner(System.in);
    ArrayDeque<Guest> photo = new ArrayDeque<>();
    void addGuest()
    {
        System.out.println("Enter guest name");
        String name  = sc.nextLine();
        String side = "";
        while (true) 
        {
            System.out.println("Enter side");
            side = sc.nextLine();
            if(side.equalsIgnoreCase("Bride")|| side.equalsIgnoreCase("Groom") )
            {
                break;
            }
            else
            {
                System.out.println("Please enter either bride or groom");
            }
        }
        System.out.println("Enter height");
        double height = sc.nextDouble();
        sc.nextLine();
        Guest g = new Guest(name,side,height);
        if(g.side.equalsIgnoreCase("Groom"))
        {
            photo.addLast(g);
        }
        else
        {
            photo.addFirst(g);
        }
    }
    void removeGuestBySide()
    {
        System.out.println("enter guest side");
        String side = sc.nextLine();
        if(side.equalsIgnoreCase("Groom"))
        {
            System.out.println("Removing guest "+photo.removeLast() );
            
        }
        else if(side.equalsIgnoreCase("bride"))
        {
            System.out.println("Removing guest "+photo.removeLast() );
            
        }
        else
        {
            System.out.println("There is no side like "+ side);
        }

    }

    void removeGuestByName()
    {
        System.out.println("enter guest name");
        String name = sc.nextLine();
        int temp = 0;
        for (Guest guest : photo) 
        {
            if(guest.name.equalsIgnoreCase(name))
            {
                temp = 1;
            
             System.out.println("Removing guest "+photo.remove(guest)) ;
             break;
            }
            
        }
      if(temp==0)
       {
        System.out.println("No guest found with name "+ name);
       }
       
    }
    void display()
    {
        System.out.println("------GUEST LIST------");
        for (Guest guest : photo) 
        {
            System.out.println(guest);
        }
    }
    void displayGroomSide()
    {
        System.out.println("------GUEST LIST FOR GROOM------");
        for (Guest guest : photo) 
        {
            if(guest.side.equalsIgnoreCase("groom"))
            {
            System.out.println(guest);
            }
        }
    }

    void displayBrideSide()
    {
        System.out.println("------GUEST LIST FOR BRIDE------");
        for (Guest guest : photo) 
        {
            if(guest.side.equalsIgnoreCase("bride"))
            {
            System.out.println(guest);
            }
        }
    }

    void SortByHeight()
    {
        ArrayList<Guest> list = new ArrayList<>(photo);
        Collections.sort(list, Comparator.comparing(Guest::getHeight));
        for (Guest guest : list) 
        {
         System.out.println(guest);    
        }
    }
    void checkFirstOrLastGuest()
    {
        System.out.println("Enter 1 for groom and 2 for bride");
        int check = sc.nextInt();
        if(check==1)
        {
            System.out.println("Last guest is "+ photo.peekLast());
        }
        if(check==2)
        {
            System.out.println("first guest is "+ photo.peekFirst());
        }
    }


}



class Guest 
{
 String name,side;
 double height;
 
public Guest() 
{

}

public Guest(String name, String side, double height) 
{
    this.name = name;
    this.side = side;
    this.height = height;
}

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public String getSide() {
    return side;
}

public void setSide(String side) {
    this.side = side;
}

public double getHeight() {
    return height;
}

public void setHeight(double height) {
    this.height = height;
}

@Override
public String toString() {
    return "Guest [name=" + name + "|| side=" + side + "|| height=" + height + "]";
}

 
}/*
 * Aim : In T20 World cup India and Australia are finalist
 *       for prefinal photography ICC invites both team 
 *       The Indian players must stand on the right side of the world cup and
 *       Australians on the left side
 *       WAP that can provide following functionalities :
 *       class name : Player
 *       Instance variables : name, team, age
 *       ask user to do 
 *       1. addPlayer() --> if user enters any other team rather than Indaia and
 *       Australia than ask him again to enter the team until he enters correct 
 *       data
 *       2. removePlayerByTeam() --> here player will be removed from the photo either left
 *       side or right side
 *       3. removePlayerByName() --> remove the any perticular player by its name
 *       4. checkFirstOrLastPlayer() --> 
 *       5. display() -->
 *       6. displayIndianTeam() -->
 *       7. displayAustralianTeam() -->
 *       8. sortByAge() --> Indecending order
 *       9. Thank you --> exit
*/

// import statements
import java.util.Scanner;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;

// class Player
class Player {
    // Instance variables
    String playerName;
    int playerAge;
    String playerTeam;

    // default constructor
    public Player() {
    }

    // perameterized constructor
    public Player(String playerName, int playerAge, String playerTeam) {
        this.playerName = playerName;
        this.playerAge = playerAge;
        this.playerTeam = playerTeam;
    }

    // getters and setters
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerAge() {
        return playerAge;
    }

    public void setPlayerAge(int playerAge) {
        this.playerAge = playerAge;
    }

    public String getPlayerTeam() {
        return playerTeam;
    }

    public void setPlayerTeam(String playerTeam) {
        this.playerTeam = playerTeam;
    }

    // toString() method
    @Override
    public String toString() {
        return "Player [playerName=" + playerName + ", playerAge=" + playerAge + ", playerTeam=" + playerTeam + "]";
    }
}

class Ex83 {
    // instance variables
    ArrayDeque<Player> playersPhoto = new ArrayDeque<Player>();
    Scanner scanner = new Scanner(System.in);

    // 1. addPlayer() method --> for add the new player
    public void addPlayer() {
        System.out.println("Enter the player name");
        String pName = scanner.nextLine();
        System.out.println("Enter players age");
        int pAge = scanner.nextInt();
        scanner.nextLine();
        String team = "";
        while (true) {
            System.out.println("Enter player team name");
            team = scanner.nextLine();
            if (team.equalsIgnoreCase("India") || team.equalsIgnoreCase("Australia")) {
                break;
            } else {
                System.out.println("plz enter India or Australia");
            }
        }
        Player newPlayer = new Player(pName, pAge, team);
        // logic : Adding indian player at last and australian player at first
        if (team.equalsIgnoreCase("India")) {
            playersPhoto.addLast(newPlayer);
        } else {
            playersPhoto.addFirst(newPlayer);
        }
    }

    // 2. removePlayerByTeam() method --> remove the player of particular team
    void removePlayerByTeam() {
        System.out.println("Enter player team to remove");
        String pTeam = scanner.nextLine();
        // logic : removing indian player from last and australian player from first
        if (pTeam.equalsIgnoreCase("India")) {
            System.out.println("Removing player ; " + playersPhoto.removeLast());
        } else if (pTeam.equalsIgnoreCase("Australia")) {
            System.out.println("Removing player ; " + playersPhoto.removeFirst());
        } else {
            System.out.println("Plz re enter the valid team name");
        }
    }

    // 3. removePlayerByName() method --> remove the player by particular name
    void removePlayerByName() {
        int flag = 0; // variable for check that player exist or not
        System.out.println("Enter the name of the player you want to remove");
        String removePlayer = scanner.nextLine();
        // for each loop for find the perticular player
        for (Player player : playersPhoto) {
            if (player.playerName.equalsIgnoreCase(removePlayer)) {
                flag = 1;
                playersPhoto.remove(player);
                System.out.println("removing player : " + player);
            }
        }
        if (flag == 0) {
            System.out.println("Player is not exist ");
        }
    }

    // 4. checkFirstOrLastPlayer() method --> peek()
    void checkFirstOrLastPlayer() {
        System.out.println("Enter 1 for check first player");
        System.out.println("Enter 2 for check last player");
        int check = scanner.nextInt();
        scanner.nextLine();
        if (check == 1) {
            System.out.println("First player is " + playersPhoto.peek());
        } else if (check == 2) {
            System.out.println("Last player is " + playersPhoto.peekLast());
        } else {
            System.out.println("Plz enter valid choice");
        }
    }

    // 5.display() method
    void display() {
        System.out.println("------------List of player------------");
        for (Player player : playersPhoto) {
            System.out.println(player);
        }
        System.out.println("--------------------------------------");
    }

    // 6. displayIndianTeam() method
    void displayIndianTeam() {
        System.out.println("------------List of Indian player------------");
        for (Player player : playersPhoto) {
            if (player.playerName.equalsIgnoreCase("India")) {
                System.out.println(player);
            }
        }
        System.out.println("---------------------------------------------");
    }

    // 7. displayAustralianTeam() method
    void displayAustralianTeam() {
        System.out.println("------------List of Australia player------------");
        for (Player player : playersPhoto) {
            if (player.playerName.equalsIgnoreCase("Australia")) {
                System.out.println(player);
            }
        }
        System.out.println("------------------------------------------------");
    }

    // 8. sort players by age --> we can use ArrayList or LinkedList
    void sortByAge() {
        ArrayList<Player> photo = new ArrayList<Player>(playersPhoto);
        Collections.sort(photo, Comparator.comparing(Player::getPlayerAge).reversed());
        for (Player player : photo) {
            System.out.println(player);
        }
    }

    // main method
    public static void main(String[] args) {
        // Object of scanner class
        Scanner scanner = new Scanner(System.in);
        Ex83 e = new Ex83();
        int ch;
        // do while loop
        do {
            System.out.println(
                    "1. add Player \t 2.remove player by team\t 3. remove player by name\t 4.check first or last player \t 5.display \t 6. display indian team \t7. display Australian team \t 8.sort by age \t 9.exit");
            System.out.println("Enter your choice");
            ch = scanner.nextInt();
            scanner.nextLine();
            // switch case
            switch (ch) {
                case 1:
                    e.addPlayer();
                    break;

                case 2:
                    e.removePlayerByTeam();
                    break;

                case 3:
                    e.removePlayerByName();
                    break;

                case 4:
                    e.checkFirstOrLastPlayer();
                    break;

                case 5:
                    e.display();
                    break;

                case 6:
                    e.displayIndianTeam();
                    break;

                case 7:
                    e.displayAustralianTeam();
                    break;

                case 8:
                    e.sortByAge();
                    break;

                case 9:
                    System.out.println("Thank you for photograhy");
                    break;

                default:
                    System.out.println("Invalid choice");
                    break;
            }
        } while (ch != 9);
    }
}
/*
 * Aim:-HashSet 
 */
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

class Ex85 {
    public static void main(String[] args) {
        HashSet<Integer> hs1 = new HashSet<>();
        hs1.add(10);
        hs1.add(-10);
        hs1.add(30);
        hs1.add(40);
        hs1.add(15);
        System.out.println("HS1 =" + hs1);
        HashSet<Integer> hs2 = new HashSet<>(Arrays.asList(50, 60, 70, null, 10, 15));
        System.out.println("HS2 =" + hs2);
        hs2.add(null);
        System.out.println("HS2 =" + hs2);
        hs1.addAll(hs2);
        System.out.println("HS1 =" + hs1);
        hs1.remove(null);
        System.out.println("HS1 =" + hs1);
        hs1.removeAll(hs2);
        System.out.println("HS1 =" + hs1);

        Iterator itr = hs1.iterator();
        while (itr.hasNext()) {
            System.out.println(itr.next());

        }

        System.out.println(hs1.add(60));
                System.out.println(hs1.add(-10));

    }
}
/*
 * Aim : Hash Set
*/

// import statements
import java.util.Arrays;
import java.util.HashSet;

class Ex86 {
    public static void main(String[] args) {
        HashSet<Integer> hs1 = new HashSet<Integer>();
        hs1.add(10);
        hs1.add(-10);
        hs1.add(20);
        hs1.add(30);
        hs1.add(15);
        System.out.println("hs1 = " + hs1);
        System.out.println(hs1.remove(20));
        System.out.println("hs1 = " + hs1);
        System.out.println(hs1.remove(100));
        System.out.println("hs1 = " + hs1);
        hs1.add(30);
        System.out.println("hs1 = " + hs1);
        
        HashSet<Integer> hs2 = new HashSet<Integer>(Arrays.asList(15,10,2,3,4,5));
        hs1.addAll(hs2);
        System.out.println("hs2 = " + hs2);
        
    }
}import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/* 
Aim : HashSet */

public class Ex87 {
    public static void main(String[] args) {
        HashSet<Integer> hs1 = new HashSet<>();
        hs1.add(10);
        hs1.add(-10);
        hs1.add(25);
        hs1.add(15);
        hs1.add(30);
        System.out.println(" hs1 " + hs1);
        System.out.println(hs1.add(-10));
        System.out.println(hs1.add(40));
        System.out.println(" hs1 " + hs1);
        hs1.add(null);
        System.out.println(" hs1 " + hs1);
        HashSet<Integer> hs2 = new HashSet<>(Arrays.asList(30, 60, 15, 25, null, 50));
        System.out.println(" hs2 " + hs2);
        hs1.addAll(hs2);
        System.out.println(" hs1 " + hs1);
        Iterator itr = hs1.iterator();
        while(itr.hasNext()){
            System.out.println(itr.next());
        }
    }
}

/*
 * Aim:-First year Coordinator decided to prepare list of students who secure more than 80% in sem1 
 * To do this task in A2 batch she selected Hardik and Ishan 
 * Hardik is going to prepare a list of roll no as well as Ishan will do the same 
 * At the end FY coordinator must get list of students with no duplicate roll no         
 * 
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

class Ex88 {
    public static void main(String[] args) {
        ArrayList<Integer> Hardik = new ArrayList<>();
        ArrayList<Integer> Ishan = new ArrayList<>(Arrays.asList(203, 208, 210, 192, 201));
        Hardik.add(192);
        Hardik.add(210);
        Hardik.add(198);
        Hardik.add(191);
        Hardik.add(207);
        Hardik.addAll(Ishan); // merge both list

        HashSet<Integer> finallist = new HashSet<>(Hardik); // duplicates value get removed
        System.out.println(finallist);

    }
}

/*
 * Aim:-HashMap Ex2
 * Create a HashMap that stores name of ipl team and total runs of that team in Map
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

class Ex89 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HashMap<String, Integer> ipl = new HashMap<>();
        ipl.put("CSK", 2000);
        ipl.put("MI", 1000);
        ipl.put("GT", 2500);
        ipl.put("SRH", 3000);
        ipl.put("RCB", 1500);
        System.out.println(ipl);
        System.out.println("Enter team to search");
        String team = sc.nextLine();

        if (ipl.containsKey(team)) {
            System.out.println(team + " Found");
            System.out.println("Total runs = " + ipl.get(team));
        } else {
            System.out.println("No team found");
        }
        HashSet<String> keys = new HashSet<>(ipl.keySet()); // To get only keys from map
        System.out.println("KEYS " + keys);

        ArrayList<Integer> value = new ArrayList<>(ipl.values());
        System.out.println("VALUES " + value);

        System.out.println("Enter updated runs for csk");
        int run = sc.nextInt();
        sc.nextLine();
        ipl.replace("CSK", run);
        System.out.println(ipl);

        System.out.println("Enter team name to remove");
        String name = sc.nextLine();
        ipl.remove(name);
        System.out.println(ipl);

        System.out.println("-----ALL DATA-----");
        for (Map.Entry m : ipl.entrySet()) {
            System.out.println("KEYS " + m.getKey());
            System.out.println("VALUES " + m.getValue());

        }

        System.out.println("Iterate keys");
        for (String s : ipl.keySet()) {
            System.out.println(s);
        }

        System.out.println("Iterate Values");
        for (Integer i : ipl.values()) {
            System.out.println();
        }
    }
}
import java.util.Hashtable;

/* 
 * Aim : Hashtable
 */

public class Ex90 {
    public static void main(String[] args) {
        Hashtable<Integer, String> ht = new Hashtable<>();
        ht.put("A",10);
		ht.put("B",20);
		ht.put("C",30);
		ht.put("D",40);
		System.out.println(ht);

    }
}
/*
 * Aim : Create Hashtable to store name and phone number 
 *  > User can search number by name 
 *  > if Name is not found then print name doesnt exist 
 * 
 */

import java.util.Scanner;
import java.util.Hashtable;

class Ex91 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Hashtable<String, String> contact = new Hashtable<>();
        contact.put("goopu", "101010");
        contact.put("loopu", "202020");
        contact.put("goobie", "303030");
        contact.put("woobie", "404040");
        contact.put("muffin", "505050");
        boolean b = true;
        while (b) {
            System.out.println("Enter 1 to search");
            System.out.println("Enter 2 to exit");
            int n = sc.nextInt();
            sc.nextLine();
            if (n == 1) {
                System.out.print("Enter Name : ");
                String name = sc.nextLine().toLowerCase();
                if (contact.containsKey(name)) {
                    System.out.println(name + " is found & its phone number is " + contact.get(name));
                } else {
                    System.out.println("Phone number for given name not found !");
                }
            } else {
                b = false;
                break;
            }

        }
    }
}
/*
Aim :- Create Hashtable Thats store information about books in library 
        Take Book Title As Key And All Other Information's Like Author,Year,Price As Value 
        Create Function For AddBook , RemoveBook ,SearchBook By Book Title And List All Books. 
*/
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

class Ex92 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LibraryManagement l = new LibraryManagement();
        int ch;
        do {
            System.out.println("enter choices");
            System.out.println("Enter 1 for addbook");
            System.out.println("Enter 2 for removebook");
            System.out.println("Enter 3 for searchbook");
            ch = sc.nextInt();
            sc.nextLine();
            switch (ch) {
                case 1:
                    l.addBook();
                    break;
                case 2:
                    l.removeBook();
                    break;
                case 3:
                    l.searchBook();
                    break;
                case 4:
                    l.display();
                    break;
                case 5:
                    System.out.println("Exit");
                    break;
                default:
                    System.out.println("enter Valid choice");
                    break;
            }
        } while (ch != 5);
    }
}

class LibraryManagement {
    Scanner sc = new Scanner(System.in);
    Hashtable<String, Book> library;

    LibraryManagement() {
        library = new Hashtable<>();
    }

    class Book {
        String Author;
        double price;
        int year;

        public Book(String author, double price, int year) {
            Author = author;
            this.price = price;
            this.year = year;
        }

        @Override
        public String toString() {
            return "Book [Author=" + Author + ", price=" + price + ", year=" + year + "]";
        }
    }

    void addBook() {
        System.out.println("Enter Book Title");
        String title = sc.nextLine().toLowerCase();
        System.out.println("Enter Autor Name");
        String autor = sc.nextLine();
        System.out.println("Enter Price");
        double price = sc.nextDouble();
        System.out.println("Enter year");
        int year = sc.nextInt();
        sc.nextLine();
        Book b = new Book(autor, price, year);
        library.put("title", b);
    }

    void removeBook() {
        System.out.println("Enter Book Title To 'Remove");
        String title = sc.nextLine().toLowerCase();
        if (library.contains(title)) {
            System.out.println("Book Is Remove " + title + " - " + library.remove(title));
        } else {
            System.out.println("Book No Fond" + title);
        }
    }

    void searchBook() {
        System.out.println("Enter Book Title To Search");
        String title = sc.nextLine().toLowerCase();
        if (library.contains(title)) {
            System.out.println("Book Found " + title + " -- " + library.get(title));
        } else {
            System.out.println("Book No Fond" + title);
        }
    }

    void display() {
        System.out.println("----Book List----");
        for (Map.Entry m : library.entrySet()) {
            System.out.println("Book Title = " + m.getKey() + " ---- " + m.getValue());
        }
    }
}import java.util.HashMap;
import java.util.Scanner;

/*
 * Aim : HashMap
 */

class Team
{
    HashMap<String , Player> team;

    public Team() {
        team = new HashMap<>();
    }
    class Player
    {
        String name;
        double strikeRate;
        String type;

        public Player(String name, double strikeRate, String type) {
            this.name = name;
            this.strikeRate = strikeRate;
            this.type = type;
        }

        @Override
        public String toString() {
            return "Player [name=" + name + ", strikeRate=" + strikeRate + ", type=" + type + "]";
        }
    }
    void addPlayer()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter name  = ");
        String name  = sc.nextLine();
        System.out.println("Enter type  = ");
        String type  = sc.nextLine();
        System.out.println("Enter Strike Rate  = ");
        Double strikeRate  = sc.nextDouble();
    }

}
public class Ex93 {
    public static void main(String[] args) {
        
    }
}

/*
 * aim= fileReader class
 */
import java.io.*;

public class Ex94 {
    public static void main(String[] args) throws IOException {
        FileReader fr = new FileReader("A2.txt");
        int i = fr.read();
        while (i != -1) {
            System.out.print((char) i);
            i = fr.read();
        }
        fr.close();
    }

}

/*
 * aim= fileReader class read using char array
 */
import java.io.*;

public class Ex95 {
    public static void main(String[] args) throws IOException {
        File f = new File("A2.txt");
        FileReader fr = new FileReader(f);
        char ch[] = new char[(int) f.length()];
        fr.read(ch);
        for (char c : ch) {
            System.out.print(c);
        }

        fr.close();
    }

}

/*
 * Aim:-Ask user to enter team name,player,total runs store them in file India.txt(Name will be entered by user)
 * Read data from the same file and print on the console 
 * NOTE:-Store and retrive data in the form of characters 
 */
import java.io.*;
import java.util.*;

class Ex96 {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter team name");
        String team_name = sc.nextLine();
        System.out.println("Enter player name");
        String player_name = sc.nextLine();
        System.out.println("Enter run");
        int run = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter file name");
        String file_name = sc.nextLine();
        FileWriter fw = new FileWriter(file_name + ".txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Team Name : " + team_name);
        bw.newLine();
        bw.write("Player Name : " + player_name);
        bw.newLine();
        bw.write("Run : " + run);
        bw.close();
        fw.close();

        // Read data from file
        FileReader fr = new FileReader(file_name + ".txt");
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            System.out.println(line);
            line = br.readLine();
        }
        br.close();
        fr.close();
    }                     

}
/*
 * Aim : File Reader class --> How to read using read(char []ch)
 * class Charachter String
*/

// import statements
import java.io.*;

// main class
class Ex97 {
    // main method compulsory handles the exception
    public static void main(String[] args) throws IOException {
        // creating object of new file reader
        File f = new File("final.txt");
        FileReader fr = new FileReader(f);
        char ch [] = new char[(int)f.length()];
        fr.read(ch);
        for (char c : ch) {
            System.out.print(c);
        }
        fr.close();
    }
}/*
 * Aim : Ask user to create new file write name roll number and branch and store 
 * in data.txt than print data from the same file
 * read all the data from the txt and print it on console
 * 
*/

// import statements
import java.io.*;
import java.util.Scanner;

// main class
class Ex98 {
    // main method compulsory handles the exception
    public static void main(String[] args) throws IOException {
        // Taking input from the user using scanner class
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter name");
        String name = sc.nextLine();
        System.out.println("Enter branch name");
        String branch = sc.nextLine();
        System.out.println("Enter roll number");
        int rollNo = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter file name to create file");
        String fileName = sc.nextLine();
        // creating object of new file writer
        FileWriter fileWriter = new FileWriter(fileName + ".txt");
        // creating object of new buffered writer
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        // writing in file using buffered writer
        bufferedWriter.write("Name : " + name);
        bufferedWriter.newLine();
        bufferedWriter.write("branch : " + branch);
        bufferedWriter.newLine();
        bufferedWriter.write("Roll No : " + rollNo);
        bufferedWriter.newLine();
        // closing buffered writer
        bufferedWriter.close();
        // closing file writer
        fileWriter.close();
        // read data from the same file and print to console
        // creating object of new file reader
        FileReader fileReader = new FileReader(fileName + ".txt");
        // creating object of new buffered reader
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        // printing the lines of file in console using while loop
        String line = bufferedReader.readLine();
        while (line != null) {
            System.out.println(line);
            line = bufferedReader.readLine();
        }
        // closing buffered reader
        bufferedReader.close();
        // closing file reader
        fileReader.close();
        // closing scanner class
        sc.close();
    }
}/*
    Aim  : Ask user to enter fileName1.
         - Ask user to enter details like name of gamezone , area , price.
         - Store all above details in file1.
         - Read all details from file1 and show on console.
 */

import java.io.*;
import java.util.Scanner;

public class Ex99 {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter name of game zone:");
        String gameZone = sc.nextLine();
        System.out.println("Enter area:");
        String area = sc.nextLine();
        System.out.println("Enter price:");
        double price = sc.nextDouble();
        sc.nextLine();
        System.out.println("Enter file name:");
        String fileName = sc.nextLine();
        FileWriter fw = new FileWriter(fileName + ".txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Name of game zone = " + gameZone);
        bw.newLine();
        bw.write("Name of area = " + area);
        bw.newLine();
        bw.write("Price = " + price);
        bw.close();
        fw.close();

        // Now start read from file

        FileReader fr = new FileReader(fileName + ".txt");
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            System.out.println(line);
            line = br.readLine();
        }
        br.close();
        fr.close();
    }
}