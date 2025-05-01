import java.util.List;
import java.util.ArrayList;

public class TransaksiList {
    // Node class representing each element in the linked list
    private static class Node {
        TransaksiHandler data;
        Node next;

        Node(TransaksiHandler data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;  // head of the list
    private int size;   // number of nodes in the list

    public TransaksiList() {
        this.head = null;
        this.size = 0;
    }

    // Add a new transaction to the end of the list
    public void add(TransaksiHandler t) {
        Node newNode = new Node(t);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    // Return the number of transactions in the list
    public int size() {
        return size;
    }

    // Optional: method to get transaction at index (0-based)
    public TransaksiHandler get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    // Convert the linked list to a List of TransaksiHandler
    public List<TransaksiHandler> toList() {
        List<TransaksiHandler> list = new ArrayList<>();
        Node current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }
}
