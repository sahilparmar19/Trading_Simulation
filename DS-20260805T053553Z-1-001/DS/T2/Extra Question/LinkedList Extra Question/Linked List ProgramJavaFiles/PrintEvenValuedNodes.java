//Write a Java Program to Print Node's data which has only even value//
import java.util.Scanner;

class SingyLinkedList
{
    Scanner sc = new Scanner(System.in);
    class Node
    {
        int data;
        Node next;
        Node()
        {
            System.out.println("Insert the data part of a new node - ");
            data = sc.nextInt();
            next = null;
        }

    }

    Node head = null;

    void insertatLast()
    {
        Node newNode = new Node();
        if(head == null)
        {
            head = newNode;
        }   
        else
        {
            Node tail = head;
            while(tail.next != null)
            {
                tail = tail.next;
            }
            tail.next = newNode;
        }
    }

    void displayEven()
    {
        if(head == null)
        {
            System.out.println("LL is Empty");
        }
        else
        {
            Node temp = head;
            while(temp != null)
            {
                if((temp.data)%2 == 0)
                {
                    System.out.print(temp.data + "-->");
                }
                temp = temp.next;
            }
            System.out.println("null");
            
        }
    }

    
    void display()
    {
        if(head == null)
        {
            System.out.println("LL is Empty");
        }
        else
        {
            Node temp = head;
            while(temp != null)
            {
                System.out.print(temp.data + "-->");
                temp = temp.next;
            }
            System.out.println("null");
            
        }
    }
}


class PrintEvenValuedNodes
{
    public static void main(String args[]) 
    {
        SingyLinkedList  s = new SingyLinkedList();
        s.insertatLast();
        s.insertatLast();
        s.insertatLast();
        s.insertatLast();
        s.insertatLast();
        s.insertatLast();
        System.out.println("SinglyLinkedList is as below -");
        s.display();   
        System.out.println("SinglyLinkedList with even values is as below -");
        s.displayEven();   
    }
}