package structures;

import model.ServiceRequest;

/**
 * FIFO queue of student service requests, implemented with front/rear
 * pointers so enqueue and dequeue are both O(1).
 *
 * Responsibility: Member 2 - Z. Isham (23DA2-0677)
 */
public class RequestQueue {

    private static class Node {
        ServiceRequest data;
        Node next;

        Node(ServiceRequest data) {
            this.data = data;
        }
    }

    private Node front;
    private Node rear;
    private int size;

    /** Adds a request at the rear of the queue. O(1). */
    public void enqueue(ServiceRequest request) {
        if (request == null) {
            return;
        }
        Node fresh = new Node(request);
        if (rear == null) {
            front = fresh;
            rear = fresh;
        } else {
            rear.next = fresh;
            rear = fresh;
        }
        size++;
    }

    /** Serves the request that arrived first. Returns null when the queue is empty. */
    public ServiceRequest dequeue() {
        if (isEmpty()) {
            return null;
        }
        ServiceRequest data = front.data;
        front = front.next;
        if (front == null) {
            rear = null;                 // queue drained, reset the rear pointer too
        }
        size--;
        return data;
    }

    public ServiceRequest peek() {
        return isEmpty() ? null : front.data;
    }

    /** Front-to-rear snapshot used when the waiting list is displayed. */
    public ServiceRequest[] toArray() {
        ServiceRequest[] out = new ServiceRequest[size];
        Node cursor = front;
        int i = 0;
        while (cursor != null) {
            out[i++] = cursor.data;
            cursor = cursor.next;
        }
        return out;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }
}
