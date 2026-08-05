//Write a Java Method to Find the Product of all the elements the given Linked List//
import java.util.*;
void product(Node First)
{
	Node temp=First;
	int product=1;
	while(temp!=null)
	{
		product=product*temp.data;
		temp=temp.next;
	}
	System.out.println("Product of All elements is"+product);
}
	