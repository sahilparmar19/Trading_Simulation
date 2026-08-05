//Write a Java Method to Count the Nodes of a Singly Linked List//
import java.util.*;
void count()
{
	Node Temp=First;
	int c=0;
	if(Temp==null)
	{
		System.out.println("Singly Linked List is Empty");
	}
	while(Temp!=null)
	{
		c=c+1;
		Temp=Temp.next;
	}
	System.out.println("Total Count of Nodes:"+c);
}