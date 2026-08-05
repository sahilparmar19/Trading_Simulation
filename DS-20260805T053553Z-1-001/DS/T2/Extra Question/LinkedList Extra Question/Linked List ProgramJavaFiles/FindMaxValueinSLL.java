//Write a Java Method to Find the Maximum Value out of all the elements the given Linked List//
import java.util.*;
void MaxValue(Node First)
{
	Node temp=First;
	int MaxValue;
	MaxValue=temp.data;
	while(temp!=null)
	{
		if(temp.data>MaxValue)
		{
			MaxValue=temp.data;
		}
		temp=temp.next;
	}
	System.out.println("Max Value is"+MaxValue);
}