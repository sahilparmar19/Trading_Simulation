//Write a Java Method to Concate the given Two Linked List//
import java.util.*;
void concat(Node First1,Node First2)
{
	Node temp1,temp2;
	temp1=First1;
	temp2=First2;
	while(temp1.next!=null)
	{
		temp1=temp1.next;
	}
	temp1.next=temp2;
	System.out.println("The Concated List is");
	temp1=First1;
	while(temp1!=null)
	{
		System.out.println(temp1.data+"--");
		temp1=temp1.next;
	}
}