//Write a Java Method to Swap the elements in Singly Linked List//
import java.util.*;
class Node {
    int data;
    Node next;

    Node(int data) {
        this.data = data;
        this.next = null;
    }
}

public class LinkedList {
    Node head;

    public void swapNodes(int position) {
        if (head == null || head.next == null)
            return;

        if (position == 1) {
            Node prev = null;
            Node current = head;
            Node nextNode = head.next;

            head = nextNode;
            current.next = nextNode.next;
            nextNode.next = current;
        } else {
            int count = 1;
            Node previousNode = null;
            Node currentNode = head;

            while (count < position && currentNode != null) {
                previousNode = currentNode;
                currentNode = currentNode.next;
                count++;
            }

            if (currentNode == null || currentNode.next == null)
                return;

            Node nextNode = currentNode.next;
            previousNode.next = nextNode;
            currentNode.next = nextNode.next;
            nextNode.next = currentNode;
        }
    }

    public void displayList() {
        Node tempNode = head;
        while (tempNode != null) {
            System.out.print(tempNode.data + " ");
            tempNode = tempNode.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        LinkedList linkedList = new LinkedList();

        // Create the linked list
        linkedList.head = new Node(1);
        Node secondNode = new Node(2);
        Node thirdNode = new Node(3);
        Node fourthNode = new Node(4);

        linkedList.head.next = secondNode;
        secondNode.next = thirdNode;
        thirdNode.next = fourthNode;

        System.out.println("Original Linked List:");
        linkedList.displayList();

        int positionToSwap = 2;
        linkedList.swapNodes(positionToSwap);

        System.out.println("Linked List after swapping nodes at position " + positionToSwap + ":");
        linkedList.displayList();
    }
}
