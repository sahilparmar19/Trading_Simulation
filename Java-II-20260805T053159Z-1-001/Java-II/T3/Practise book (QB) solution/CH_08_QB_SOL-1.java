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