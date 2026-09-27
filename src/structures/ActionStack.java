package structures;

import model.ActionRecord;

/**
 * LIFO stack of recent actions, backed by a linked structure so it never
 * runs out of capacity. Drives both the "Recent Actions" view and Undo.
 *
 * Responsibility: Member 2 - Z. Isham (23DA2-0677)
 */
public class ActionStack {

    private static class Node {
        ActionRecord data;
        Node below;

        Node(ActionRecord data, Node below) {
            this.data = data;
            this.below = below;
        }
    }

    private Node top;
    private int size;

    /** Pushes a new action on top. O(1). */
    public void push(ActionRecord record) {
        if (record == null) {
            return;
        }
        top = new Node(record, top);
        size++;
    }

    /** Removes and returns the most recent action, or null when empty. */
    public ActionRecord pop() {
        if (isEmpty()) {
            return null;
        }
        ActionRecord data = top.data;
        top = top.below;
        size--;
        return data;
    }

    /** Looks at the most recent action without removing it. */
    public ActionRecord peek() {
        return isEmpty() ? null : top.data;
    }

    /** Most-recent-first snapshot used by the display menu. */
    public ActionRecord[] toArray() {
        ActionRecord[] out = new ActionRecord[size];
        Node cursor = top;
        int i = 0;
        while (cursor != null) {
            out[i++] = cursor.data;
            cursor = cursor.below;
        }
        return out;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }
}
