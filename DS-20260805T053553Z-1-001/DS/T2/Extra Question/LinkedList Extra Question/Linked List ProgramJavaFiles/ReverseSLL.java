//Write a Java Method to Reverse the given Linked List//
import java.util.*;
void reverse()
{
	Node temp1,temp2,temp3;
	temp1=First;
	if(temp1==null)
	{
		System.out.println("Singly Linked List is Empty.");
	}
	else
	{
		temp2=null;
		while(temp1!=null)
		{
			temp3=temp2;
			temp2=temp1;
			temp1=temp1.next;
			temp2.next=temp3;
		}
		First=temp2;
	}
	System.out.println("The Linked List is Reversed")
}