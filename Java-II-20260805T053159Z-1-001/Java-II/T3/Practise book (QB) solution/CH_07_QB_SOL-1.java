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

//----------------------------------------------------------------

