import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Collections;
import java.util.Iterator;

class Medicine {
    String name;
    double price;
    int quantity;

    public Medicine(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String toString() {
        return "Medicine [name=" + name + ", price=" + price + ", quantity=" + quantity + "]";
    }
}

class Pharmacy {
    LinkedList<Medicine> medicines;

    public Pharmacy() {
        this.medicines = new LinkedList<>();
    }

    public synchronized void addMedicine(Medicine medicine) {
        medicines.add(medicine);
    }

    public synchronized void removeMedicine(String name) {
        Medicine medicineToRemove = null;
        for (Medicine medicine : medicines) {
            if (medicine.getName().equalsIgnoreCase(name)) {
                medicineToRemove = medicine;
                break;
            }
        }

        if (medicineToRemove != null) {
            medicines.remove(medicineToRemove);
            System.out.println("Medicine removed successfully!");
        } else {
            System.out.println("Medicine not found!");
        }
    }

    public synchronized void updateMedicine(String newName) {
        Medicine medicineToUpdate = null;
        for (Medicine medicine : medicines) {
            if (medicine.getName().equalsIgnoreCase(newName)) {
                System.out.println("Enter New Price");
                Scanner sc = new Scanner(System.in);
                double newPrice = sc.nextDouble();
                medicine.price = newPrice;
                
                System.out.println("Enter New Qty");
                Scanner scc = new Scanner(System.in);
                int newQty = scc.nextInt();
                medicine.quantity = newQty;
                                              
                medicineToUpdate = medicine;
                break;
            }
        }
        if (medicineToUpdate != null) {
             System.out.println("Medicine price and Qty updated successfully!");
        }
        else {
                System.out.println("Medicine not found!");
            }
    }

    public void displayMedicinesByName() {
        if(medicines.isEmpty())
        {
            System.out.println("Medicines List is empty. Sorting is not possible");
        }
        else
        {
        System.out.println("Medicines sorted by name.");
        System.out.println("Medicine Inventory:");
        Collections.sort(medicines, Comparator.comparing(Medicine::getName));
        Iterator itr1=medicines.iterator();
        {
            while(itr1.hasNext())
            {
                System.out.println(itr1.next());
            }
        }
        }
    
    }
    public void displayMedicinesByHighestPrice() {
        if(medicines.isEmpty())
        {
            System.out.println("Medicines List is empty. Sorting is not possible");
        }
        else
        {
        System.out.println("Medicines sorted by Highest Price.");
        System.out.println("Medicine Inventory:");
        Collections.sort(medicines, Comparator.comparing(Medicine::getPrice).reversed());
        Iterator itr1=medicines.iterator();
        {
            while(itr1.hasNext())
            {
                System.out.println(itr1.next());
            }
        }
        }
    
    }
    public void displayMedicinesByHighestQuantity() {
        if(medicines.isEmpty())
        {
            System.out.println("Medicines List is empty. Sorting is not possible");
        }
        else
        {
        System.out.println("Medicines sorted by Highest Quantity.");
        System.out.println("Medicine Inventory:");
        Collections.sort(medicines, Comparator.comparing(Medicine::getQuantity).reversed());
        Iterator itr1=medicines.iterator();
            while(itr1.hasNext())
            {
                System.out.println(itr1.next());
            }
        }
    
    }

    public void sellMedicine(String newName, int quantity) {
    
        if(medicines.isEmpty())
        {
            System.out.println("All medicines Sold out. Come Tommorrow");
        }
         
        else{    
            for (Medicine medicine : medicines) {
                if (medicine.getName().equalsIgnoreCase(newName)) {
                    if (medicine.getQuantity() >= quantity) {
                        medicine.setQuantity(medicine.getQuantity() - quantity);
                        System.out.println("Medicine sold successfully to " + Thread.currentThread().getName());
                        return;
                    } else {
                        System.out.println("Insufficient quantity in stock for " + Thread.currentThread().getName());
                        return;
                    }
                }
            }
            System.out.println("Medicine not found!");
        }
    }
}

public class PMSystem extends Thread {
	static Pharmacy pharmacy;
	// String medName;
	// int sellQty;
	
    public void run(){
        synchronized(this){
        System.out.println("Hi "+ getName());
        Scanner scanner = new Scanner(System.in);
        System.out.println("Medicine Avaliablity:");
            Iterator itr1=pharmacy.medicines.iterator();
            while(itr1.hasNext())
                {
                    System.out.println(itr1.next());
                }
        System.out.print("Enter the name of the medicine: ");
        String medicineName = scanner.next();
        System.out.print("Enter the quantity to sell: ");
        int sellQuantity = scanner.nextInt();        
		pharmacy.sellMedicine(medicineName, sellQuantity);
        }
	}
	
	public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        pharmacy = new Pharmacy();
		
        // Adding sample medicines
        pharmacy.addMedicine(new Medicine("Paracetamol", 50.0, 20));
        pharmacy.addMedicine(new Medicine("Ibuprofen", 80.0, 15));
        pharmacy.addMedicine(new Medicine("Saridone", 120.0, 100));
        pharmacy.addMedicine(new Medicine("Dolo", 150.0, 30));
        pharmacy.addMedicine(new Medicine("Disprine", 200.0, 10));

        int choice = 0;

        ArrayList<Thread> customerThreads = new ArrayList<>(); // ArrayList to store customer threads

        while (choice != 6) {
            System.out.println("Pharmacy Management System");
            System.out.println("1. Display Medicines");
            System.out.println("2. Sell Medicine");
            System.out.println("3. Add Medicine");
            System.out.println("4. Remove Medicine");
            System.out.println("5. Update Price by Medicine name");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("\t Enter Display choice: ");
                    System.out.println("\t 1. Display Medicines By Sorting Name");
                    System.out.println("\t 2. Display Medicines By Sorting Highest Price");
                    System.out.println("\t 3. Display Medicines By Sorting Highest Quantity");
                int c= scanner.nextInt();
                    switch (c)
                    {
                        case 1:
                        pharmacy.displayMedicinesByName();
                        break;
                        case 2:
                        pharmacy.displayMedicinesByHighestPrice();
                        break;
                        case 3:
                        pharmacy.displayMedicinesByHighestQuantity();
                        break;
                        default:
                        System.out.println("Invalid Display choice! Please try again.");
                        break;

                    }
                    break;
                    
                case 2:
                   
                    // Creating and starting customer threads
                    PMSystem sonuThread = new PMSystem();
                    sonuThread.setName("Sonu");
                    customerThreads.add(sonuThread); // Adding Sonu thread to the list
                    sonuThread.start();
                    try { sonuThread.join(); } catch (InterruptedException e) {}
                    
                    PMSystem monuThread = new PMSystem();
                    monuThread.setName("Monu");
                    customerThreads.add(monuThread); // Adding Monu thread to the list
                    monuThread.start();
                    try { monuThread.join(); } catch (InterruptedException e) {}

                    break;
                case 3:
                    System.out.print("Enter the name of the medicine: ");
                    String newMedicineName = scanner.next();
                    System.out.print("Enter the price of the medicine: ");
                    double newMedicinePrice = scanner.nextDouble();
                    System.out.print("Enter the quantity of the medicine: ");
                    int newMedicineQuantity = scanner.nextInt();
                    Medicine newMedicine = new Medicine(newMedicineName, newMedicinePrice, newMedicineQuantity);
                    pharmacy.addMedicine(newMedicine);
                    System.out.println("Medicine added successfully!");
                    break;
                case 4:
                    System.out.print("Enter the name of the medicine to remove: ");
                    String medicineToRemove = scanner.next();
                    
                    pharmacy.removeMedicine(medicineToRemove);
                    break;
                case 5:
                    System.out.print("Enter the name of the medicine to update: ");
                    String medicineToUpdate = scanner.next();
                    pharmacy.updateMedicine(medicineToUpdate);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
                    break;
            }
        }

        // Waiting for all customer threads to complete
        for (Thread thread : customerThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // scanner.close();
    }
}
