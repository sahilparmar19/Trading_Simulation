//JAVA Program to reverse the queue without using any additional storage.

import java.util.concurrent.DelayQueue;

class Queue{
    int size;
    int front, rear;
    int a[];

    Queue(int size)
    {
        this.size = size;
        a = new int[size];
        front = -1;
        rear = -1;
    }

    void enqueue(int data)
    {
        if(rear == size-1)
        {
            System.out.println("Overflow");
        }
        else
        {
            if(front == -1)
            {
                front =0;
            }
            rear = (rear + 1)%size;
            a[rear] = data;
        }
        
    }

    void reverse(int front)
    {
        if(front != -1)
        {
            int x = dequeue();
            reverse(this.front);
            enqueue(x);
        }
        else
        return;
    }

    void display()
    {
        for(int i = front; i<=rear; i++)
        {
            System.out.print(a[i] + " " );
        }
        System.out.println();
    }

    int dequeue()
    {
        if(front == -1)
        {
            System.out.println("Underflow"); return -1;
        }
        else
        {
            int y = a[front];
            if(front == rear)
            {
                front = -1;
                rear = -1;
            }
            else
            {
                front = (front +1)%size;
            }
            return y;
        }
    }
}

class ReverseQueue
{
    public static void main(String args[])
    {
        Queue  q= new Queue(5);
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.display();
        q.reverse(q.front);
        q.display();
    }
}