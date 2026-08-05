import java.util.*;

class Participant implements Runnable 
{
    String name;
    int age;
    String contactNumber;
    List<Character> answers;
    
    public Participant(String name, int age, String contactNumber, Object lock) 
    {
        this.name = name;
        this.age = age;
        this.contactNumber = contactNumber;
        this.answers = new ArrayList<>();
    }

    public String getName() 
    {
        return name;
    }

    public int getAge() 
    {
        return age;
    }

    public String getContactNumber() 
    {
        return contactNumber;
    }

    public List<Character> getAnswers() 
    {
        return answers;
    }

    public void run() 
    {
        System.out.println("Participant: " + name + ", Age: " + age + ", Contact Number: " + contactNumber);
        Scanner scanner = new Scanner(System.in);

        synchronized (this) 
        {
            System.out.println();
            System.out.println();
            System.out.println("Hello "+name+" Here are questions for Quiz");
            System.out.println("Q.1. National bird of INDIA..(A) Peacock (B) Sparrow (C) Duck (D) Owl ");
            System.out.println("Q.2  Independence year of INDIA..(A) 1955 (B) 1947 (C) 1999 (D) 1929 ");
            System.out.println("Q.3  Gandhi Jayanti is on......(A) 2nd Oct (B) 5th Oct (C) 9th Oct (D) 7th Oct ");
            System.out.println("Q.4  Count of states in INDIA..(A) 17 (B) 21 (C) 25 (D) 28 ");
            System.out.println("Q.5  how many continents are there in the world..(A) 5 (B) 6 (C) 7 (D) 8 ");

            for (int i = 1; i <= 5; i++) 
            {
                System.out.print("Enter your answer for Question " + i + ": ");
                char answer = scanner.nextLine().charAt(0);
                answers.add(answer);
            }

        }
    }
}

class QuizGame 
{
    public static void main(String[] args) throws InterruptedException 
    {
        Scanner scanner = new Scanner(System.in);
        List<Participant> participants = new ArrayList<>();
        Stack<Character> correctAnswers = new Stack<>();
        correctAnswers.push('A');
        correctAnswers.push('B');
        correctAnswers.push('A');
        correctAnswers.push('D');
        correctAnswers.push('C');

        for (int i = 1; i <= 3; i++) 
        {
            System.out.print("Enter participant name: ");
            String name = scanner.nextLine();

            System.out.print("Enter participant age: ");
            int age = scanner.nextInt();
            scanner.nextLine();

            String contactNumber;
            while (true) 
            {
                System.out.print("Enter 10-digit contact number for " + name + ": ");
                contactNumber = scanner.nextLine();
                if (contactNumber.length() == 10 && contactNumber.startsWith("9")) 
                {
                    break;
                } 
                else 
                {
                    System.out.println("Please enter a valid 10-digit contact number starting with '9'.");
                }
            }

            participants.add(new Participant(name, age, contactNumber, new Object()));
        }

        Collections.sort(participants,Comparator.comparing(Participant::getAge));

        List<Thread> threads = new ArrayList<>();
        
        for (Participant participant : participants) 
        {
            Thread thread = new Thread(participant);
            threads.add(thread);
            thread.start();
            thread.join();
        }

        for (Thread thread : threads) 
        {
            thread.join();
        }

        int highestScore = 0;
        String winner = "";

        System.out.println("----------------Results:------------------");
        for (Participant participant : participants) {
            int score = calculateScore(participant.getAnswers(), correctAnswers);
            System.out.println("Participant: " + participant.getName() + ", Age: " + participant.getAge() + ", Contact Number: " + participant.getContactNumber() + ", Score: " + score);

            if (score > highestScore) 
            {
                highestScore = score;
                winner = participant.getName();
            }
        }

        System.out.println("Winner: " + winner);
    }

    private static int calculateScore(List<Character> participantAnswers, Stack<Character> correctAnswers) 
    {
        int score = 0;
        for (int i = 0; i < participantAnswers.size(); i++) 
        {
            if (participantAnswers.get(i) == correctAnswers.get(i)) 
            {
                score++;
            }
        }
        return score;
    }
}


