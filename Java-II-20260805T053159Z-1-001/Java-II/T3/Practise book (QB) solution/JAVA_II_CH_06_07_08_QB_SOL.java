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

/*-------------------------------------------*/

//------CHAP 7 FILE IO QB SOLUTION Q298 TO Q307 -----------//

/*QB 298	
Write a java program which read source file and destination file name and copy the content of source file to destination file using InputeStream class. */
import java.io.*;

public class FileCopyUsingInputStream {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter the source file name: ");
            String sourceFileName = reader.readLine();

            System.out.print("Enter the destination file name: ");
            String destinationFileName = reader.readLine();

            copyFileUsingInputStream(sourceFileName, destinationFileName);

            System.out.println("File copied successfully.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void copyFileUsingInputStream(String sourceFileName, String destinationFileName) throws IOException {
        try (InputStream inputStream = new FileInputStream(sourceFileName);
             OutputStream outputStream = new FileOutputStream(destinationFileName)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}

The program will prompt you to enter the source file name and destination file name. After you provide the names, it will copy the content of the source file to the destination file using the InputStream class.

//----------------------------------------------------------------

/* QB 299	
Write a java program which read numbers from number.txt file and store even number to even.txt and odd number into odd.txt file */


class NumberSorter 
{
    public static void main(String[] args) throws Exception
    {

             BufferedReader reader = new BufferedReader(new FileReader("number.txt"));
             BufferedWriter evenWriter = new BufferedWriter(new FileWriter("even.txt")); 
             BufferedWriter oddWriter = new BufferedWriter(new FileWriter("odd.txt"));

            String line = reader.readLine();
            while (line != null) 
            {
                int number = Integer.parseInt(line);
                if (number % 2 == 0) 
                {
                    evenWriter.write(Integer.toString(number));
                    evenWriter.newLine();
                    line = reader.readLine();
                } else {
                    oddWriter.write(Integer.toString(number));
                    oddWriter.newLine();
                    line = reader.readLine();
                }
            }

            System.out.println("Numbers sorted and saved successfully!");
            evenWriter.close();
            oddWriter.close();
        
    }
}
//----------------------------------------------------------------

/* QB 300	
Read employee salary and calculate the income tax based on 10% of income and store it in tax.txt file for five different employees. */

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.Scanner;

public class IncomeTaxCalculator {
    public static void main(String[] args) {
        try {
            FileWriter fileWriter = new FileWriter("tax.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            Scanner scanner = new Scanner(System.in);

            for (int i = 1; i <= 5; i++) {
                System.out.print("Enter salary for Employee " + i + ": ");
                double salary = scanner.nextDouble();

                double tax = calculateIncomeTax(salary);
                bufferedWriter.println("Employee " + i + ": " + tax);
            }

            bufferedWriter.close();
            System.out.println("Income tax calculated and saved to tax.txt.");
        } catch (IOException e) {
            System.out.println("Error while writing to the file: " + e.getMessage());
        }
    }

    static double calculateIncomeTax(double salary) {
        double taxRate = 0.10; // 10% tax rate
        return salary * taxRate;
    }
}

//----------------------------------------------------------------

/* Q 301
Write a program that counts the no. of words in a text file. The file name is passed as a
command line argument. The program should check whether the file exists or not. The
words in the file are separated by white space characters */
import java.io.*;

class WordCounter {
    public static void main(String[] args) 
    {
        if (args.length != 1) {
            System.out.println("Please provide the file name as a command line argument.");
            return;
        }

            try
            {
                String fileName = args[0];

                BufferedReader reader = new BufferedReader(new FileReader(fileName));
             
                int wordCount = 0;
                String line = reader.readLine();
                while (line != null) 
                {
                    String[ ] words = line.split(" ");
                    wordCount += words.length;
                    line = reader.readLine();
                }
                System.out.println("Number of words in the file: " + wordCount);
            } 
            catch (FileNotFoundException e) 
            {
                System.out.println("The file does not exist.");    
            } 
            catch (IOException e) 
            {
                System.out.println("An error occurred while reading the file.");
            }
    }
}
//----------------------------------------------------------------

/* QB 302	--- OR ---
/* QB 303	
Write a program to check that whether the name given from command line is file or not? If it is a file then print the size of file and if it is directory then it should display the name of all files in it.   */

import java.io.File;

class FileTypeChecker 
{
    public static void main(String[] args) 
    {
        if (args.length != 1)  
        {
            System.out.println("Please provide the file or directory name as a command lineargument.");
            return;
        }

        String path = args[0];
        File file = new File(path);

        if (file.isFile()) 
        {
            System.out.println("The given path is a file.");
            System.out.println("File size: " + file.length() );
        } 
        else if (file.isDirectory()) 
        {
            System.out.println("The given path is a directory.");
            System.out.println("Files in the directory:");

            String files[ ] = file.list();
            
                for (String f : files) 
                { 
                        System.out.println(f);
                }
        } 
        else 
        {
            System.out.println("The given path does not exist or is neither a file nor a directory.");
        }
    }
}


/*QB 304 
Write a program that reads file name from user, through command line argument   and
displays/reads content of the text file on console.*/
 
import java.io.*;
class FileContentReader 
{
    public static void main(String[] args) 
    {
        if (args.length != 1) 
        {
            System.out.println("Please provide the file name as a command line argument.");
            return;
        }

        String fileName = args[0];

        try
        { 
            BufferedReader reader = new BufferedReader(new FileReader(fileName)); 
        
            String line= reader.readLine();
            while ( line != null) 
            {
                System.out.println(line);
                line= reader.readLine();
            }
        } 
        catch (IOException e) 
        {
            System.out.println("An error occurred while reading the file.");
        }
    }
}
 
/*QB 305	
Write a program to create directories (/home/abc/bcd/def/ghi/jkl) in the home directory  /home/abc and list the files and directories showing file/directory,file size. Read-writeexecute permissions. */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DirectoryCreatorAndLister {
    public static void main(String[] args) {
        String baseDir = "/home/abc";
        String nestedDirs = "bcd/def/ghi/jkl";

        String fullPath = baseDir + "/" + nestedDirs;

        // Create directories if they don't exist
        File directory = new File(fullPath);
        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (!success) {
                System.out.println("Failed to create directories.");
                return;
            }
        }

        // List files and directories with permissions
        System.out.println("List of files and directories with permissions:");
        permitioncheck(directory);
    }

    static void permitioncheck(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                String type = file.isFile() ? "File" : "Directory";
                String permissions = "Read: " + file.canRead() + ", Write: " + file.canWrite() + ", Execute: " + file.canExecute();

                System.out.println(name + " - " + file.getAbsolutePath() + " - " + type + " - " + permissions);
            }
        }
    }
}

//----------------------------------------------------------------

/*QB 306	
Write a program to find the sum of all the number in Number.txt file and print the result in console. */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SumFromFile {
    public static void main(String[] args) throws IOException {
        String fileName = "Number.txt";

        int sum = 0;
		FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr); 
		String line;
            while ((line = br.readLine()) != null) {
                int number = Integer.parseInt(line.trim());
                sum += number;
            }
        System.out.println("Sum of numbers in " + fileName + ": " + sum);
    }
}

//----------------------------------------------------------------
 
/*Qb 307	
Write a program to create a new file and write the number into it and sort that file content into different file.*/

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class FileSorter {
    public static void main(String[] args) throws IOException {
        // Create a new file and write numbers into it
        String inputFilePath = "input.txt";

        FileWriter writer = new FileWriter(inputFilePath);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        // Add some numbers to the file
        bufferedWriter.write("10\n5\n8\n1\n3\n");
        bufferedWriter.close();

        // Sort the content of the file and write into another file
        String outputFilePath = "output.txt";

		FileReader reader = new FileReader(inputFilePath);
        BufferedReader bufferedReader = new BufferedReader(reader);

        ArrayList<Integer> numbers = new ArrayList<>();

        // Read numbers from the file and add them to the ArrayList
        String line = bufferedReader.readLine();
        while (line != null) {
            numbers.add(Integer.parseInt(line));
			line = bufferedReader.readLine();
        }
        bufferedReader.close();

        // Sort the numbers in ascending order
        Collections.sort(numbers);

        // Write the sorted numbers into the output file
        FileWriter writer = new FileWriter(outputFilePath);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        
		for (int number : numbers) {
            bufferedWriter.write(String.valueOf(number));
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

        System.out.println("File sorting successful.");
    }
}

//-----------------------------------------------

//------CHAP 8 CHARACTER STREAM QB SOLUTION Q325 TO Q338 -----------//
 
/*QB325	
Write a JAVA program to read student.txt file and display the content. */
 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadStudentFile {

    public static void main(String[] args) throws IOException {
        String filename = "student.txt";

        BufferedReader br = new BufferedReader(new FileReader(filename));

        //loop to display contents line-by-line
		String line = br.readLine(); 
        while (line  != null) {
            System.out.println(line);
			line = br.readLine();
        }
		
		
		/* OR USE THIS LOOP instead:
		String line;                               
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
		*/

        br.close();
    }
}

//----------------------------------------------------------------

/*QB 326	
Write a program to read the content of a file into a character array and write it into another file. Get names of the files from command line. */

import java.io.*;

public class FileCopy {
    public static void main(String[] args) throws IOException {
        // Check if the correct number of command-line arguments are provided
        if (args.length != 2) {
            System.out.println("Usage: java FileCopy <source-file> <destination-file>");
            return;
        }

        String sourceFileName = args[0];
        String destinationFileName = args[1];

        // Open the source file and read its content into a character array
        FileReader fileReader = new FileReader(sourceFileName);
        int fileSize = (int) new File(sourceFileName).length();
        char[] content = new char[fileSize];
        fileReader.read(content);
        fileReader.close();

        // Write the content from the character array to the destination file
        FileWriter fileWriter = new FileWriter(destinationFileName);
        fileWriter.write(content);
        fileWriter.close();

        System.out.println("File copied successfully.");
    }
}

//----------------------------------------------------------------
/*QB 327 AND QB 331 SAME
Write a java program to search the file named the word entered as a filename from command line; if it exists in the system then program should print the content of a file on console. */

import java.io.*;

public class FileSearchAndRead {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java FileSearchAndRead <filename>");
            return;
        }

        String fileName = args[0];
        File file = new File(fileName);

        if (!file.exists()) {
            System.out.println("File not found.");
            return;
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
    }
}

//----------------------------------------------------------------
/*QB 328	
Write a java program that read employee details and store into emp.txt text file using file handling.*/
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class EmployeeDetails {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the number of employees: ");
        int numEmployees = scanner.nextInt();
        scanner.nextLine(); // Clear the newline character from the buffer

        BufferedWriter writer = new BufferedWriter(new FileWriter("emp.txt"));
        for (int i = 0; i < numEmployees; i++) {
            System.out.println("Enter employee name: ");
            String name = scanner.nextLine();

            System.out.println("Enter employee ID: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Clear the newline character from the buffer

            System.out.println("Enter employee designation: ");
            String designation = scanner.nextLine();

            System.out.println("Enter employee salary: ");
            double salary = scanner.nextDouble();
            scanner.nextLine(); // Clear the newline character from the buffer

            // Write employee details to the file
            writer.write("Name: " + name);
            writer.newLine();
            writer.write("ID: " + id);
            writer.newLine();
            writer.write("Designation: " + designation);
            writer.newLine();
            writer.write("Salary: " + salary);
            writer.newLine();
            writer.newLine();
        }
        writer.close();
        System.out.println("Employee details written to emp.txt successfully.");
    }
}

//----------------------------------------------------------------
/*QB 329	
Write a program that counts number of characters, words, and lines in a text file. */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileWordCount {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java FileWordCount <file_path>");
            return;
        }

        String filePath = args[0];
        int charCount = 0;
        int wordCount = 0;
        int lineCount = 0;

        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        String line;
        while ((line = reader.readLine()) != null) {
            lineCount++;
            charCount += line.length();
            String[] words = line.trim().split("\\s+");
            wordCount += words.length;
        }

        reader.close();

        System.out.println("Number of characters: " + charCount);
        System.out.println("Number of words: " + wordCount);
        System.out.println("Number of lines: " + lineCount);
    }
}

//----------------------------------------------------------------
/*QB 330	
Create a class called Student. Write a student manager program to manipulate the student information from files by using the BufferedReader and BufferedWriter.*/
import java.io.*;

class Student {
    private String name;
    private int age;
    private String rollNumber;

    public Student(String name, int age, String rollNumber) {
        this.name = name;
        this.age = age;
        this.rollNumber = rollNumber;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Age: " + age + ", Roll Number: " + rollNumber;
    }
}

public class StudentManager {
    public static void main(String[] args) throws IOException {
        // File paths
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        // Read student information from the input file and store in a list
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;
        List<Student> students = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length == 3) {
                String name = data[0].trim();
                int age = Integer.parseInt(data[1].trim());
                String rollNumber = data[2].trim();
                students.add(new Student(name, age, rollNumber));
            }
        }
        reader.close();

        // Write student information to the output file
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        for (Student student : students) {
            writer.write(student.toString());
            writer.newLine();
        }
        writer.close();

        // Print the student information to the console
        for (Student student : students) {
            System.out.println(student);
        }
    }
}

//----------------------------------------------------------------

/*QB 331	(SAME AS 327)
Write a java program to search the file named the word entered as a filename from commandline: If it exists in the system the program should print the contents of the file on console */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileSearchAndRead {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java FileSearchAndRead <filename>");
            return;
        }

        String filename = args[0];
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        reader.close();
    }
}

//----------------------------------------------------------------
/*QB 332	
Write a Java program to copy content of file1.txt to file2.txt using Java file handling */

import java.io.*;

public class FileCopy {
    public static void main(String[] args) throws IOException {
        // Provide the paths for file1.txt and file2.txt
        String sourcePath = "file1.txt";
        String destinationPath = "file2.txt";

        // Open the input and output streams
        FileInputStream inputStream = new FileInputStream(sourcePath);
        FileOutputStream outputStream = new FileOutputStream(destinationPath);

        // Copy the content from file1 to file2
        int data;
        while ((data = inputStream.read()) != -1) {
            outputStream.write(data);
        }

        // Close the streams
        inputStream.close();
        outputStream.close();

        System.out.println("Content of file1.txt has been copied to file2.txt.");
    }
}

//----------------------------------------------------------------
/*QB 333 
Write an application that reads a file and counts the number of occurrences of digit 5. Supply the file name as a command-line argument*/

import java.io.*;

class CountDigit5Occurrences 
{
       public static void main(String[] args) throws Exception
       {
            if (args.length != 1) 
            {
                System.err.println("Usage: java CountDigit5Occurrences <filename>");
                return;
            }

        String filename = args[0];
        int count = 0;
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
            while (line  != null) 
            {
                char ch[ ] = line.toCharArray();
                for (char c : ch) 
                {
                    if (c == '5') 
                    {
                        count++;
                    }
                }
                line = reader.readLine();
            }
            System.out.println("Number of occurrences of digit 5 in the file: " + count);
        }  
    }

//-----------------------------------------------------------------------

/*QB 334 
Write a program to replace all “word1” by “word2” to a file and
display the no. of replacement.*/
 
import java.io.*;
import java.util.*;
class FileReplace 
{
    public static void main(String[] args) throws IOException
    {
        File f1 = new File("G:\\bb.txt"); // read from this and replace
        File f2 = new File("G:\\bbb.txt"); // write to this after replace
        FileWriter fw1 = new FileWriter(f1);
        fw1.write("Hi baby Hi Hi its too High");
        fw1.close();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Word 1");
        String word1 = sc.next();
        System.out.println("Enter Word 2");
        String word2 = sc.next();
        FileWriter fw2 = new FileWriter(f2);
        FileReader fr1 = new FileReader(f1);
        BufferedReader br1 = new BufferedReader(fr1); //
        String rep = "";
        String s="";
       int count = 0 ;
        while((s=br1.readLine())!=null)
        {
        String s1[] = s.split(" ");
        for(String s2 : s1)
        {
            if(s2.equals(word1))
            {
                count++; 
                s2=word2; 
            }
            rep = rep+s2+" ";
        }
        System.out.println("Count of Replacement = "+count);
        fw2.write(rep+"\n");
        }
        fw2.close();
        }
}
//---------------------------------------------------------------

/*QB 335 
Write a program to count the total no. of chars, words, lines, alphabets,
digits, white spaces in a given file. */
import java.io.*;

class FileAnalyzer {

    public static void main(String[] args) 
    {
        if (args.length != 1) {
            System.err.println("Usage: java FileAnalyzer <filename>");
            return;
        }

        String filename = args[0];

        try {
            File file = new File(filename);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            int charCount = 0;
            int wordCount = 0;
            int lineCount = 0;
            int alphabetCount = 0;
            int digitCount = 0;
            int whitespaceCount = 0; 

            String line;
            while ((line = reader.readLine()) != null) {
                charCount += line.length();
                wordCount += countWords(line);
                alphabetCount += countAlphabets(line);
                digitCount += countDigits(line);
                whitespaceCount += countWhitespaces(line);
                lineCount++;
            }

            reader.close();

            System.out.println("Total characters: " + charCount);
            System.out.println("Total words: " + wordCount);
            System.out.println("Total lines: " + lineCount);
            System.out.println("Total alphabets: " + alphabetCount);
            System.out.println("Total digits: " + digitCount);
            System.out.println("Total white spaces: " + whitespaceCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int countWords(String line) {
        String[] words = line.split(" ");
        return words.length;
    }

    static int countAlphabets(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (Character.isLetter(c)) {
                count++;
            } 
        }
        return count;
    }

    static int countDigits(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (Character.isDigit(c)) {
                count++;
            }
        }
        return count;
    }

    static int countWhitespaces(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (Character.isWhitespace(c)) {
                count++;
            }
        }
        return count;
    }
}

//----------------------------------------------------------

/*QB 336	
Write a program to display the bytes of a file in reverse sequence. Provide the name of the file as a command line argument. */

import java.io.*;

public class ReverseFileBytes {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java ReverseFileBytes <filename>");
            return;
        }

        File file = new File(args[0]);
        if (!file.exists() || !file.isFile()) {
            System.out.println("File not found or is not a regular file.");
            return;
        }

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long fileSize = raf.length();

        for (long position = fileSize - 1; position >= 0; position--) {
            raf.seek(position);
            byte byteValue = raf.readByte();
            System.out.print(byteValue);
        }
        raf.close();
    }
}

//---------------------------------------------------------------

/*QB 337 
Write a program to sort the one file numbers to another.for example
one file contain the unsorted number separated by line and write the another
file with sorted number. */

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

class SortNumbersFromFile 
{
    public static void main(String[] args) { 
        if (args.length != 2) {
            System.err.println("Usage: java SortNumbersFromFile <inputFile><outputFile>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        ArrayList<Integer> numbers = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    int number = Integer.parseInt(line.trim());
                    numbers.add(number);
                } catch (NumberFormatException e) {
                    // Ignore non-integer lines in the input file
                }
            }

            reader.close();

            Collections.sort(numbers);

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            for (int num : numbers) {
                writer.write(String.valueOf(num));
                writer.newLine();
            }
 
            writer.close();

            System.out.println("Numbers sorted and written to " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
//---------------------------------------------------------------- 
/*QB338	
Write a java program which read numbers from number.txt file and store even number to even.txt and odd number into odd.txt file. */

import java.io.*;

public class FileNumberSorter {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("number.txt"));
        BufferedWriter evenWriter = new BufferedWriter(new FileWriter("even.txt"));
        BufferedWriter oddWriter = new BufferedWriter(new FileWriter("odd.txt"));

        String line;
        while ((line = reader.readLine()) != null) {
            int num = Integer.parseInt(line);
            if (num % 2 == 0) {
                evenWriter.write(line);
                evenWriter.newLine();
            } else {
                oddWriter.write(line);
                oddWriter.newLine();
            }
        }

        reader.close();
        evenWriter.close();
        oddWriter.close();
    }
}