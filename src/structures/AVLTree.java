package structures;

import model.Student;

/**
 * Self-balancing AVL tree keyed on Student ID.
 *
 * Every insert/delete restores the AVL property (|balance factor| <= 1) using
 * the four standard rotation cases, so search stays O(log n) even when IDs
 * are entered in sorted order - which is exactly what happens with real
 * student numbers such as 23DA2-0675, 23DA2-0676, 23DA2-0677 ...
 *
 * Responsibility: Member 3 - M.I.M Arshad (23DA2-0634)
 */
public class AVLTree {

    private static class Node {
        Student data;
        Node left;
        Node right;
        int height;

        Node(Student data) {
            this.data = data;
            this.height = 1;
        }
    }

    private Node root;
    private int size;

    // ---------------------------------------------------------------- helpers

    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private int balanceFactor(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private void refreshHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private int compare(String a, String b) {
        return a.compareToIgnoreCase(b);
    }

    // -------------------------------------------------------------- rotations

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node t2 = x.right;
        x.right = y;
        y.left = t2;
        refreshHeight(y);
        refreshHeight(x);
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node t2 = y.left;
        y.left = x;
        x.right = t2;
        refreshHeight(x);
        refreshHeight(y);
        return y;
    }

    /** Re-balances one node after an insert or delete and returns the new subtree root. */
    private Node rebalance(Node node) {
        refreshHeight(node);
        int balance = balanceFactor(node);

        if (balance > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);   // Left-Right case
            }
            return rotateRight(node);                // Left-Left case
        }
        if (balance < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right); // Right-Left case
            }
            return rotateLeft(node);                  // Right-Right case
        }
        return node;
    }

    // ----------------------------------------------------------------- insert

    /** Inserts a record. Returns false when the ID is already in the tree. */
    public boolean insert(Student student) {
        if (student == null || search(student.getStudentId()) != null) {
            return false;
        }
        root = insert(root, student);
        size++;
        return true;
    }

    private Node insert(Node node, Student student) {
        if (node == null) {
            return new Node(student);
        }
        int cmp = compare(student.getStudentId(), node.data.getStudentId());
        if (cmp < 0) {
            node.left = insert(node.left, student);
        } else {
            node.right = insert(node.right, student);
        }
        return rebalance(node);
    }

    // ----------------------------------------------------------------- delete

    /** Removes the record with the given ID. Returns false when it is not present. */
    public boolean delete(String studentId) {
        if (search(studentId) == null) {
            return false;
        }
        root = delete(root, studentId);
        size--;
        return true;
    }

    private Node delete(Node node, String studentId) {
        if (node == null) {
            return null;
        }
        int cmp = compare(studentId, node.data.getStudentId());
        if (cmp < 0) {
            node.left = delete(node.left, studentId);
        } else if (cmp > 0) {
            node.right = delete(node.right, studentId);
        } else {
            // node found - handle 0, 1 and 2 child cases
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node successor = min(node.right);              // in-order successor
            node.data = successor.data;
            node.right = delete(node.right, successor.data.getStudentId());
        }
        return rebalance(node);
    }

    private Node min(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // ----------------------------------------------------------------- search

    /** O(log n) search by Student ID. */
    public Student search(String studentId) {
        Node cursor = root;
        while (cursor != null) {
            int cmp = compare(studentId, cursor.data.getStudentId());
            if (cmp == 0) {
                return cursor.data;
            }
            cursor = (cmp < 0) ? cursor.left : cursor.right;
        }
        return null;
    }

    // -------------------------------------------------------------- traversal

    /** In-order traversal - returns the records sorted by Student ID. */
    public Student[] inOrder() {
        Student[] out = new Student[size];
        fillInOrder(root, out, new int[]{0});
        return out;
    }

    private void fillInOrder(Node node, Student[] out, int[] index) {
        if (node == null) {
            return;
        }
        fillInOrder(node.left, out, index);
        out[index[0]++] = node.data;
        fillInOrder(node.right, out, index);
    }

    /** Indented sideways view of the tree, used to prove the balancing works. */
    public String structureView() {
        StringBuilder sb = new StringBuilder();
        buildStructure(root, 0, sb);
        return sb.length() == 0 ? "   (tree is empty)\n" : sb.toString();
    }

    private void buildStructure(Node node, int depth, StringBuilder sb) {
        if (node == null) {
            return;
        }
        buildStructure(node.right, depth + 1, sb);
        for (int i = 0; i < depth; i++) {
            sb.append("        ");
        }
        sb.append(node.data.getStudentId())
          .append("  (h=").append(node.height)
          .append(", bf=").append(balanceFactor(node)).append(")\n");
        buildStructure(node.left, depth + 1, sb);
    }

    public int height() {
        return height(root);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return root == null;
    }
}
