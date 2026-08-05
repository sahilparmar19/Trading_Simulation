//------CHAP 6 COLLECTION-II QB SOLUTION Q261 TO Q274 -----------//

/* QB261	
Write a Java program that takes a list of integers as input from the user and stores them in a PriorityQueue. The program should then remove and display the top three highest integers from the PriorityQueue.*/

import java.util.*;

public class TopThreeHighestIntegers {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(Collections.reverseOrder());

        System.out.print("Enter the number of integers: ");
        int numIntegers = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character left by nextInt()

        for (int i = 0; i < numIntegers; i++) {
            System.out.print("Enter integer " + (i + 1) + ": ");
            int value = scanner.nextInt();
            priorityQueue.add(value);
        }

        System.out.println("Top three highest integers:");

        // Removing and displaying the top three highest integers
        for (int i = 0; i < 3; i++) {
            if (!priorityQueue.isEmpty()) {
                int highestInt = priorityQueue.poll();
                System.out.println(highestInt);
            } else {
                System.out.println("Not enough integers in the PriorityQueue.");
                break;
            }
        }

        scanner.close();
    }
}

//-------------------------------------------------------------------------------


/* QB262	
Sports tournament organizer: Write a program that simulates a sports tournament using a PriorityQueue. The program should allow the user to input team names and their win-loss records. The program should then prioritize teams based on their win-loss records and add them to the PriorityQueue. When its time for the next match, the program should remove the two highest priority teams from the PriorityQueue and display their names.*/

import java.util.*;

class Team {
    String name;
    int wins;
    int losses;

    public Team(String name, int wins, int losses) {
        this.name = name;
        this.wins = wins;
        this.losses = losses;
    }

    public int getWinLossRatio() {
        return wins - losses;
    }

    @Override
    public String toString() {
        return name;
    }
}

public class SportsTournamentSimulator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PriorityQueue<Team> teamsQueue = new PriorityQueue<>(Comparator.comparing(Team::getWinLossRatio).reversed());

        // Input team names and win-loss records
        System.out.println("Enter the number of teams: ");
        int numTeams = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character after reading the integer.

        for (int i = 0; i < numTeams; i++) {
            System.out.println("Enter team name: ");
            String name = scanner.nextLine();
            System.out.println("Enter number of wins: ");
            int wins = scanner.nextInt();
            System.out.println("Enter number of losses: ");
            int losses = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character after reading the integer.

            Team team = new Team(name, wins, losses);
            teamsQueue.add(team);
        }

        // Display the next match
        System.out.println("Next match:");
        if (!teamsQueue.isEmpty()) {
            Team team1 = teamsQueue.poll();
            Team team2 = teamsQueue.poll();
            System.out.println(team1 + " vs. " + team2);
        } else {
            System.out.println("Not enough teams to start the match.");
        }
    }
}


//-------------------------------------------------------------------------------

/*
 * QB 263 
 * Write a program that simulates a queue of customers at a coffee
shop using an ArrayDeque. The program should allow the user to
perform the following actions:
Add a new customer to the back of the queue
Serve the next customer in the queue (i.e. remove the customer
from the front of the queue)
View the current queue of customers
The program should continue to prompt the user for actions until
they choose to quit.

 */
import java.util.*;

public class QB263 {
    public static void main(String[] args) {
        ArrayDeque<String> customerQueue = new ArrayDeque<>();
        Scanner sc = new Scanner(System.in);
        int ch;
         do{
            System.out.println("\nMenu:");
            System.out.println("1. Add new customer to the back of the queue");
            System.out.println("2. Serve the next customer");
            System.out.println("3. View the current queue");
            System.out.println("4. Quit");

            System.out.print("Enter your choice: ");
            ch = sc.nextInt();
            sc.nextLine(); // Clear the newline character

            switch (ch) {
                case 1:
                    System.out.print("Enter customer name: ");
                    String customer = sc.nextLine();
                    customerQueue.addLast(customer);
                    System.out.println(customer + " has been added to the queue.");
                    break;
                case 2:
                    if (!customerQueue.isEmpty()) {
                        String servedCustomer = customerQueue.pollFirst();
                        System.out.println("Serving " + servedCustomer + ".");
                    } else {
                        System.out.println("Queue is empty. No customers to serve.");
                    }
                    break;
                case 3:
                    if (!customerQueue.isEmpty()) {
                        System.out.println("Current queue of customers:");
                        for (String name : customerQueue) {
                            System.out.println("- " + name);
                        }
                    } else {
                        System.out.println("Queue is empty. No customers in the queue.");
                    }
                    break;
                case 4:
                    System.out.println("Exiting coffee shop queue simulation.");
                   
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }while(ch!=4);
    }
}
/*
 * QB 264 
 * 
 * Music player playlist: Write a program that simulates a music
player playlist using an ArrayDeque. The program should allow
the user to add songs to the playlist, play the next or previous
song in the playlist, and shuffle the playlist. The program should
keep track of the songs in the playlist using an ArrayDeque,
where each element in the deque represents a song. When the
user adds a song, you can use the addLast method to add it to
the end of the playlist. When the user plays the next or previous
song, you can use the removeFirst or removeLast method,
respectively, to remove the current song from the front or back
of the deque and add it to the end or beginning of the deque.
When the user shuffles the playlist, you can use the shuffle
method to randomly reorder the songs in the deque.

 * 
 */


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class QB264 {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayDeque<String> playlist = new ArrayDeque<>();

        System.out.println("Music Player Playlist Simulator");

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add song to playlist");
            System.out.println("2. Play next song");
            System.out.println("3. Play previous song");
            System.out.println("4. Shuffle playlist");
            System.out.println("5. Display current playlist");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("Enter song name: ");
                    String song = scanner.nextLine();
                    playlist.addLast(song);
                    System.out.println(song + " added to the playlist.");
                    break;
                case 2:
                    if (!playlist.isEmpty()) {
                        String nextSong = playlist.removeFirst();
                        System.out.println("Playing: " + nextSong);
                        playlist.addLast(nextSong);
                    } else {
                        System.out.println("Playlist is empty.");
                    }
                    break;
                case 3:
                    if (!playlist.isEmpty()) {
                        String previousSong = playlist.removeLast();
                        System.out.println("Playing: " + previousSong);
                        playlist.addFirst(previousSong);
                    } else {
                        System.out.println("Playlist is empty.");
                    }
                    break;
                case 4:
                    if (!playlist.isEmpty()) {
                        ArrayList<String> al = new ArrayList<>(playlist);
                        Collections.shuffle(al);
                        playlist = new ArrayDeque<>(al);

                        System.out.println("Playlist shuffled.");
                    } else {
                        System.out.println("Playlist is empty.");
                    }
                    break;
                case 5:
                    if (!playlist.isEmpty()) {
                        System.out.println("Current Playlist: " + playlist);
                    } else {
                        System.out.println("Playlist is empty.");
                    }
                    break;
                case 6:
                    System.out.println("Exiting Music Player Playlist Simulator.");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

// QB265	
/*Make the system as User interactive and give choices to user. 
Web browser history: Write a program that simulates a web browser history using an ArrayDeque. The program should allow the user to navigate back and forward through their history of visited web pages, similar to how a web browser works. The program should keep track of the URLs of visited web pages using an ArrayDeque, where each element in the deque represents a visited web page. When the user navigates to a new web page, you can push the URL onto the deque. When the user navigates back or forward, you can pop URLs off the front or back of the deque, respectively.*/

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

public class WebBrowserHistory {
    static Deque<String> history = new ArrayDeque<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            printMenu();
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character after reading the integer

            switch (choice) {
                case 1:
                    System.out.print("Enter the URL to visit: ");
                    String url = scanner.nextLine();
                    navigateTo(url);
                    break;
                case 2:
                    navigateBack();
                    break;
                case 3:
                    navigateForward();
                    break;
                case 4:
                    showHistory();
                    break;
                case 5:
                    System.out.println("Exiting the web browser.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);

        scanner.close();
    }

    static void printMenu() {
        System.out.println("=========== Web Browser History ===========");
        System.out.println("1. Visit a new web page");
        System.out.println("2. Navigate back");
        System.out.println("3. Navigate forward");
        System.out.println("4. Show history");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    static void navigateTo(String url) {
        history.addLast(url);
        System.out.println("Navigated to: " + url);
    }

    static void navigateBack() {
        if (history.isEmpty()) {
            System.out.println("No history to navigate back.");
        } else {
            String url = history.pollLast();
            System.out.println("Navigated back to: " + url);
        }
    }

    static void navigateForward() {
        if (history.isEmpty()) {
            System.out.println("No history to navigate forward.");
        } else {
            String url = history.pollFirst();
            System.out.println("Navigated forward to: " + url);
        }
    }

    static void showHistory() {
        if (history.isEmpty()) {
            System.out.println("No history available.");
        } else {
            System.out.println("Web Browser History:");
            int index = 1;
            for (String url : history) {
                System.out.println(index++ + ". " + url);
            }
        }
    }
}

//-------------------------------------------------------------------------------


/* QB266
 * 
 * In my restaurant I used to manage it well as per the order. I never
want to skip any order from the customer. So I prepare a rule:
When I get the order I add it in the last of my cook queue. And
when the order is ready I used to pick it up from the first of the
cook queue.
So, write a java program with class Restaurant. Create a queue
cook which contains item names. Ask user weather he wants to
Order or take food. 

If user press 1 then ask for the name of the
item and it should be added in the cook queue.

If user press 2 then The first item from the queue should be
removed from the queue.

If user press 3 then he can see the items of the queue. If user
press 4 then he should move out from the Restaurant. 

 */

import java.util.ArrayDeque;
import java.util.*;

class Restaurant{

    ArrayDeque<String> cook;
    
    Restaurant(){
        cook = new ArrayDeque<>();
    }

}
class QB266 {


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Restaurant r = new Restaurant();
        int ch; 
        do{
            System.out.println("Enter Your Choice 1 To What to Order? \n 2 for Serve the order \n 3. view Order  \n 4. Move out from restaurant");
            ch = sc.nextInt();
            sc.nextLine();
            switch(ch){
                case 1:
                    System.out.println("Enter Item to Order");
                    String item = sc.nextLine();
                    r.cook.addLast(item);
                    System.out.println("Your order for item "+item+" has been placed...");
                break;
                
                case 2: 
                    if(!r.cook.isEmpty()){
                        String servingItem = r.cook.pollFirst();
                        System.out.println("Serving item "+servingItem+" to customer....");    
                    }else{
                        System.out.println("NO ORDER TO SERVE");
                    }
                break;
                
                case 3: 
                
                    if(!r.cook.isEmpty()){
                      System.out.println("----- LIST OF ITEMS IN QUEUE TO BE COOKED ------");  
                      int i = 1; 
                      for(String s:r.cook){
                        System.out.println("Item "+i+" = "+s);
                        i++;
                      }
                    }else{
                        System.out.println("NO ORDER TO VIEW IN QUEUE");
                    }
                break;
                
                case 4: 
                    System.out.println("THANK YOU... VISIT AGAIN");
                break;
            }
        }while(ch!=4);
    }
    
}
// QB267
/*Write a java program that reads in a list of integers from the user
and stores them in a HashSet. The program should then compute
the sum of all unique integers in the HashSet and output the
result to the user. If the user enters the same integer multiple
times, it should only be counted once in the sum.
 */

 import java.util.*;

public class QB267 {
    public static void main(String[] args) {
        HashSet<Integer> uniqueValues = new HashSet<>();
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("Enter Integer Number ");
            int no = sc.nextInt();
            sc.nextLine();
            System.out.println("DO YOU WANT TO CONTINUE ? YES OR NO");
            uniqueValues.add(no);

            String choice = sc.nextLine();
            if(choice.equalsIgnoreCase("NO")){
                break;
            }
        }
        int sum = 0;
        for(Integer i:uniqueValues){
            sum = sum + i;
        }
        System.out.println("SUM = "+sum);
    }
}

// QB268
/*Write a program that reads in a list of words from the user and
stores them in a HashSet. The program should then prompt the
user for a prefix and output all the words in the set that start with
that prefix.
 */

import java.util.*;

public class QB268 {
    public static void main(String[] args) {
        HashSet<String> words = new HashSet<>();
        Scanner sc = new Scanner(System.in);

        // Read words from user and stores them in to HashSet

        System.out.println("---------------------------------");
        while (true) {
            System.out.print("Enter Word ");
            String w = sc.nextLine();
            words.add(w);
            System.out.println("DO YOU WANT TO CONTINUE ? YES OR NO");
            
            String choice = sc.nextLine();
            if(choice.equalsIgnoreCase("NO")){
                break;
            }
        }   
        
        System.out.println("Enter prefix for the word that you want to search from HashSet ? ");
        String prefix = sc.next();
        for(String s : words){
            if(s.startsWith(prefix)){
                System.out.println("---> "+s );
            }
        }
    }    
}

/* QB269
 * Write a program that reads in a list of names and corresponding
phone numbers from the user, and stores them in a HashMap.
The program should then prompt the user for a name and output
the corresponding phone number, or a message indicating that
the name is not in the map.

 */
import java.util.HashMap;
import java.util.*;

public class QB269 {
    public static void main(String[] args) {
        HashMap<String,String> phonebook = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        String name, number; 
        while(true){
            System.out.println("Enter Name");
            name = sc.nextLine();
            
            System.out.println("Enter Number");
            number = sc.nextLine();
            phonebook.put(name, number);
            System.out.println("Do you want to add Data ? Enter Yes or No");
            String choice = sc.nextLine();
            if(choice.equalsIgnoreCase("NO")){
                break;
            }
        }

        System.out.println("Enter the name you want to search from phonebook ?");
        String searchName = sc.nextLine();
        String searchNumber = phonebook.get(searchName);
        if(searchNumber!=null){
            System.out.println("Phone Number of "+searchName+" = "+searchNumber);
        }
        else{
            System.out.println("Name is not there in phonebook");
        }
    }
    
}


/* QB270
 * Write a program that reads in a list of stock prices for a company
and stores them in a HashMap, where the key is the date and the
value is the price. The program should then compute the average
price for the entire period and output it to the user.

 */
import java.util.*;


public class QB270 {
    public static void main(String[] args) {
        HashMap<String, Double> stockPrices = new HashMap<>();
        Scanner sc = new Scanner(System.in);

        // Read stock prices and dates from the user and store them in the HashMap
        System.out.println("Enter stock prices and dates");
        while (true) {
            System.out.print("Date (format: YYYY-MM-DD): ");
            String date = sc.nextLine();
            System.out.print("Price: ");

            double price =sc.nextDouble();
            sc.nextLine();

            stockPrices.put(date, price);
            
            System.out.println("DO YOU WANT TO CONTINUE ? YES OR NO");
            
            String choice = sc.nextLine();
            if(choice.equalsIgnoreCase("NO")){
                break;
            }
                       
        }

        // Calculate the average stock price for the entire period
        double total = 0.0;
        for (double price : stockPrices.values()) {
            total = total + price;
        }
        double average = total / stockPrices.size();

        // Output the result
        System.out.println("Average stock price for the entire period: " + average);

        
    }    
}

/* QB271
 * Write a program that reads in a list of words from a file and
stores them in a HashMap, along with their frequency (i.e. how
many times they appear in the file). The program should then
prompt the user for a word and output the corresponding
frequency, or a message indicating that the word is not in the
map.
 */

 import java.util.*;
 import java.io.*;
public class QB271 {
    public static void main(String[] args) throws Exception {
        FileReader fr = new FileReader("inputData.txt");
        BufferedReader br =  new BufferedReader(fr);
        HashMap<String,Integer> wordFrequencyCount = new HashMap<>();
        String line="";
        while((line = br.readLine())!=null){
            String words[] = line.split(" ");
            for(String w:words){
                if(wordFrequencyCount.get(w)==null){
                    wordFrequencyCount.put(w,1);
                }
                else{
                    wordFrequencyCount.put(w, (wordFrequencyCount.get(w)+1));
                }
                //wordFrequencyCount.put(w, wordFrequencyCount.getOrDefault(w, 0)+1); // this method is not in syllabus so can not use it .
            }
        } 

        for(Map.Entry e: wordFrequencyCount.entrySet()){
            System.out.println("Count for: "+e.getKey() +  "  is = "+ e.getValue());

        }
        br.close();
        fr.close();
    }
    
}

/* QB272	
Write a Java program that takes in a list of student names and their grades from the user and stores them in a HashTable. The program should then prompt the user for a student name and output their grade. If the student name is not found in the HashTable, the program should output an error message.*/

import java.util.Hashtable;
import java.util.Scanner;

public class StudentGradeLookup {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Hashtable<String, Integer> studentGrades = new Hashtable<>();

        // Input student names and grades
        System.out.print("Enter the number of students: ");
        int numStudents = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        for (int i = 0; i < numStudents; i++) {
            System.out.print("Enter student name: ");
            String name = scanner.nextLine();

            System.out.print("Enter student grade: ");
            int grade = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            studentGrades.put(name, grade);
        }

        // Lookup student grade
        System.out.print("Enter student name to lookup grade: ");
        String nameToLookup = scanner.nextLine();

        Integer grade = studentGrades.get(nameToLookup);

        if (grade != null) {
            System.out.println("Grade for " + nameToLookup + ": " + grade);
        } else {
            System.out.println("Error: Student not found.");
        }

        scanner.close();
    }
}

//-------------------------------------------------------------------------------

/* QB273
	Write a Java program that implements a HashTable to store a dictionary of words and their definitions. The program should prompt the user for a word and output its definition. If the word is not found in the HashTable, the program should ask the user if they would like to add the word and its definition to the HashTable.
The program should use the following methods of the HashTable class:
put(K key, V value): Adds a key-value pair to the HashTable. In this program, it should be used to add new words and their definitions.
get(Object key): Retrieves the value associated with the given key from the HashTable. In this program, it should be used to retrieve the definition of a word.
containsKey(Object key): Returns true if the HashTable contains a mapping for the specified key. In this program, it should be used to check if a word is already in the HashTable.
keySet(): Returns a Set of all keys in the HashTable. In this program, it should be used to print out a list of all words in the dictionary.*/

import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;

public class DictionaryApp {
    public static void main(String[] args) {
        Hashtable<String, String> dictionary = new Hashtable<>();
        Scanner scanner = new Scanner(System.in);

        // Populate the dictionary with some initial words and definitions
        dictionary.put("apple", "A fruit that is typically red or green.");
        dictionary.put("banana", "A long, curved fruit with a yellow skin.");
        dictionary.put("cat", "A small domesticated carnivorous mammal.");

        while (true) {
            System.out.print("Enter a word to get its definition (or type 'exit' to quit): ");
            String inputWord = scanner.nextLine();

            if (inputWord.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (dictionary.containsKey(inputWord)) {
                String definition = dictionary.get(inputWord);
                System.out.println("Definition: " + definition);
            } else {
                System.out.println("Word not found in the dictionary.");

                System.out.print("Would you like to add it? (yes/no): ");
                String addWord = scanner.nextLine();

                if (addWord.equalsIgnoreCase("yes")) {
                    System.out.print("Enter the definition of the word: ");
                    String definition = scanner.nextLine();
                    dictionary.put(inputWord, definition);
                    System.out.println("Word added to the dictionary.");
                } else {
                    System.out.println("Word was not added to the dictionary.");
                }
            }
        }

        // Print out the list of all words in the dictionary
        Set<String> words = dictionary.keySet();
        System.out.println("Words in the dictionary:");
        for (String word : words) {
            System.out.println(word);
        }

        scanner.close();
    }
}

//-------------------------------------------------------------------------------

/*QB274	
Write a Java program that creates a HashTable to store information about books in a library. The HashTable should use the book titles as keys and store information about each book, such as the author, publisher, and year of publication. The program should provide the following functionality:
Add a book: The program should prompt the user for a book title, author, publisher, and year of publication, and add the book to the HashTable.
Remove a book: The program should prompt the user for a book title and remove the corresponding entry from the HashTable.
Search for a book: The program should prompt the user for a book title and output the corresponding information stored in the HashTable, such as the author, publisher, and year of publication. If the book title is not found in the HashTable, the program should o
List all books: The program should list all books in the HashTable, along with their corresponding information.
To accomplish these tasks, you can use the following HashTable methods:
put(key, value): Inserts a key-value pair into the HashTable.
remove(key): Removes a key-value pair from the HashTable.
get(key): Returns the value corresponding to a given key in the HashTable.
containsKey(key): Returns true if the HashTable contains a given key.
keySet(): Returns a Set of all the keys in the HashTable."*/

import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;

public class LibraryManagement {
    private Hashtable<String, Book> library;

    public LibraryManagement() {
        library = new Hashtable<>();
    }

    private class Book {
        String title;
        String author;
        String publisher;
        int year;

        public Book(String title, String author, String publisher, int year) {
            this.title = title;
            this.author = author;
            this.publisher = publisher;
            this.year = year;
        }
    }

    public void addBook() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        System.out.print("Enter publisher: ");
        String publisher = scanner.nextLine();
        System.out.print("Enter year of publication: ");
        int year = scanner.nextInt();

        Book book = new Book(title, author, publisher, year);
        library.put(title, book);

        System.out.println("Book added successfully!");
    }

    public void removeBook() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter book title to remove: ");
        String title = scanner.nextLine();

        if (library.containsKey(title)) {
            library.remove(title);
            System.out.println("Book removed successfully!");
        } else {
            System.out.println("Book not found in the library.");
        }
    }

    public void searchBook() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter book title to search: ");
        String title = scanner.nextLine();

        if (library.containsKey(title)) {
            Book book = library.get(title);
            System.out.println("Title: " + book.title);
            System.out.println("Author: " + book.author);
            System.out.println("Publisher: " + book.publisher);
            System.out.println("Year of Publication: " + book.year);
        } else {
            System.out.println("Book not found in the library.");
        }
    }

    public void listAllBooks() {
        if (library.isEmpty()) {
            System.out.println("The library is empty.");
        } else {
            System.out.println("Listing all books in the library:");
            Set<String> titles = library.keySet();
            for (String title : titles) {
                Book book = library.get(title);
                System.out.println("Title: " + book.title);
                System.out.println("Author: " + book.author);
                System.out.println("Publisher: " + book.publisher);
                System.out.println("Year of Publication: " + book.year);
                System.out.println("--------------------------");
            }
        }
    }

    public static void main(String[] args) {
        LibraryManagement libraryManagement = new LibraryManagement();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nLibrary Management System");
            System.out.println("1. Add a book");
            System.out.println("2. Remove a book");
            System.out.println("3. Search for a book");
            System.out.println("4. List all books");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    libraryManagement.addBook();
                    break;
                case 2:
                    libraryManagement.removeBook();
                    break;
                case 3:
                    libraryManagement.searchBook();
                    break;
                case 4:
                    libraryManagement.listAllBooks();
                    break;
                case 5:
                    System.out.println("Exiting the program.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
