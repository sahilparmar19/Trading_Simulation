//Write a Java Method to Insert a Value After a Particular Node//
import java.util.*;
void insertAfterValue(int data,int value)
	{
		
		int flag = 0;
		if(First==null)
		{
			System.out.println("Linked is empty");
		}
		else
		{
			Node dummy = First;
			while(dummy != null)
			{
				if(dummy.data == value)
				{
				flag = 1;
				}
				dummy = dummy.next;
			}
				if(flag == 0)
				{
					System.out.println("The asked value is not inside the linked list");
				}
				else
				{
					Node n = new Node(data);
					if(First.data == value && First.next== null)
					{
						First.next=n;
					}
					else if(First.data == value)
					{
						n.next=First.next;
						First.next=n;
					}
					else
					{
						Node temp=First;
						while(temp.data!=value)
						{
							temp=temp.next;
						}
						n.next=temp.next;
						temp.next=n;
					}
				}		
		}
	}