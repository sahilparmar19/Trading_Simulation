import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;

class Candidate {
    String name;
    int age;
    String party;
    int votes;

    public Candidate(String name, int age, String party) {
        this.name = name;
        this.age = age;
        this.party = party;
        this.votes = 0;
    }

    public String getName() {
        return name;
    }

    public int getVotes() {
        return votes;
    }
}

public class Voter extends Thread {
    static ArrayList<Thread> voters = new ArrayList<>();
    static LinkedList<Candidate> candidates = new LinkedList<>();

    @Override
    public void run() {
        int randomIndex = (int) (Math.random() * candidates.size());
        Candidate selectedCandidate = candidates.get(randomIndex);
        synchronized (selectedCandidate) {
            selectedCandidate.votes++;
            System.out.println(getName() + " voted for " + selectedCandidate.name);
        }
    }

    public static void displayCandidates() {
        System.out.println("Candidate Details:");
        for (Candidate candidate : candidates) {
            System.out.println("Name: " + candidate.name + ", Age: " + candidate.age + ", Party: " + candidate.party);
        }
    }

    public static void displayResults() {
        System.out.println("\nVoting Results:");

        candidates.sort(Comparator.comparing(Candidate::getVotes).reversed());
        for (Candidate candidate : candidates) {
            double percentage = (candidate.votes * 100.0) / voters.size();
            System.out.println(candidate.name + " (" + candidate.party + "): " + candidate.votes + " votes (" + percentage + "%)");
        }
        System.out.println("\nWinner: " + candidates.getFirst().name + " (" + candidates.getFirst().party + ")"+ " (" + candidates.getFirst().votes + " Votes)");
    }

    public static void main(String[] args) {
        voters.ensureCapacity(50);

        Candidate candidate1 = new Candidate("CVM", 28, "AJP");
        Candidate candidate2 = new Candidate("DJU", 39, "BJP");
        Candidate candidate3 = new Candidate("HNM", 35, "CJP");
        Candidate candidate4 = new Candidate("HDS", 32, "DJP");
        Candidate candidate5 = new Candidate("PAT", 30, "EJP");

        candidates.add(candidate1);
        candidates.add(candidate2);
        candidates.add(candidate3);
        candidates.add(candidate4);
        candidates.add(candidate5);

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        while (choice != 5) {
            System.out.println("\n--- Online Voting System ---");
            System.out.println("1. Add Candidate");
            System.out.println("2. Remove Candidate");
            System.out.println("3. Update Candidate Details");
            System.out.println("4. Display Candidate Details");
            System.out.println("5. Display Voting Results and then Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter candidate name: ");
                    String name = scanner.next();
                    System.out.print("Enter candidate age: ");
                    int age = scanner.nextInt();
                    System.out.print("Enter candidate party: ");
                    String party = scanner.next();
                    Candidate candidate = new Candidate(name, age, party);
                    candidates.add(candidate);
                    System.out.println("Candidate added successfully.");
                    break;
                case 2:
                    System.out.print("Enter candidate name to remove: ");
                    String removeName = scanner.next();
                    boolean candidateRemoved = false;
                    for (Candidate c : candidates) {
                        if (c.name.equalsIgnoreCase(removeName)) {
                            candidates.remove(c);
                            candidateRemoved = true;
                            break;
                        }
                    }
                    if (candidateRemoved) {
                        System.out.println("Candidate removed successfully.");
                    } else {
                        System.out.println("Candidate not found.");
                    }
                    break;
                case 3:
                    System.out.print("Enter candidate name to update: ");
                    String updateName = scanner.next();
                    boolean candidateUpdated = false;
                    for (Candidate c : candidates) {
                        if (c.name.equalsIgnoreCase(updateName)) {
                            System.out.print("Enter updated age: ");
                            int updatedAge = scanner.nextInt();
                            System.out.print("Enter updated party: ");
                            String updatedParty = scanner.next();
                            c.age = updatedAge;
                            c.party = updatedParty;
                            candidateUpdated = true;
                            break;
                        }
                    }
                    if (candidateUpdated) {
                        System.out.println("Candidate details updated successfully.");
                    } else {
                        System.out.println("Candidate not found.");
                    }
                    break;
                case 4:
                    displayCandidates();
                    break;
                case 5:
                    for (int i = 1; i <= 50; i++) {
                        Voter voter = new Voter();
                        voter.setName("User-" + i);
                        voters.add(voter);
                        voter.start();
                    }

                    for (Thread voter : voters) {
                        try {
                            voter.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    displayResults();
                    break;
                default:  
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}
