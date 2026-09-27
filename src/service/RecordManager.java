package service;

import model.ActionRecord;
import model.ServiceRequest;
import model.Student;
import structures.ActionStack;
import structures.AVLTree;
import structures.CampusGraph;
import structures.RequestQueue;
import structures.StudentHashTable;

/**
 * Integration layer. Every student record lives in THREE structures at once,
 * and this class is what keeps them consistent:
 *
 *   linked list -> primary ordered store, used for "display all"
 *   AVL tree    -> sorted view and O(log n) ordered search
 *   hash table  -> O(1) average lookup by Student ID
 *
 * It also owns the undo stack, the service-request queue and the campus graph.
 *
 * Responsibility: all four members (integration), coordinated by the group leader.
 */
public class RecordManager {

    private final structures.StudentLinkedList studentList = new structures.StudentLinkedList();
    private final AVLTree studentTree = new AVLTree();
    private final StudentHashTable studentIndex = new StudentHashTable();
    private final ActionStack history = new ActionStack();
    private final RequestQueue serviceQueue = new RequestQueue();
    private final CampusGraph campusGraph = new CampusGraph();

    // ------------------------------------------------------- student records

    /** Adds a record to all three structures. Returns false on a duplicate ID. */
    public boolean addStudent(Student student) {
        if (student == null || studentIndex.containsKey(student.getStudentId())) {
            return false;
        }
        studentList.add(student);
        studentTree.insert(student);
        studentIndex.put(student);
        history.push(new ActionRecord(ActionRecord.Type.ADD,
                "Added student " + student.getStudentId() + " (" + student.getName() + ")",
                copyOf(student)));
        return true;
    }

    /**
     * Updates the mutable fields of an existing record. Because all three
     * structures hold the same object reference, one update is enough.
     * Returns false when the ID does not exist.
     */
    public boolean updateStudent(String studentId, String newName, String newProgramme, Double newMarks) {
        Student existing = studentIndex.get(studentId);
        if (existing == null) {
            return false;
        }
        Student before = copyOf(existing);
        if (newName != null && !newName.trim().isEmpty()) {
            existing.setName(newName.trim());
        }
        if (newProgramme != null && !newProgramme.trim().isEmpty()) {
            existing.setProgramme(newProgramme.trim());
        }
        if (newMarks != null) {
            existing.setMarks(newMarks);
        }
        history.push(new ActionRecord(ActionRecord.Type.UPDATE,
                "Updated student " + studentId + " (was: " + before.getName()
                        + ", " + before.getProgramme() + ", " + before.getMarks() + ")",
                before));
        return true;
    }

    /** Deletes a record from all three structures. Returns the removed record or null. */
    public Student deleteStudent(String studentId) {
        Student removed = studentIndex.get(studentId);
        if (removed == null) {
            return null;
        }
        studentList.remove(studentId);
        studentTree.delete(studentId);
        studentIndex.remove(studentId);
        history.push(new ActionRecord(ActionRecord.Type.DELETE,
                "Deleted student " + removed.getStudentId() + " (" + removed.getName() + ")",
                copyOf(removed)));
        return removed;
    }

    /** Hash-table lookup - the O(1) average search path. */
    public Student searchByHash(String studentId) {
        return studentIndex.get(studentId);
    }

    /** AVL lookup - the O(log n) tree search path, used for comparison. */
    public Student searchByTree(String studentId) {
        return studentTree.search(studentId);
    }

    public Student[] allStudentsInInsertionOrder() {
        return studentList.toArray();
    }

    public Student[] allStudentsSortedById() {
        return studentTree.inOrder();
    }

    // ------------------------------------------------------------------ undo

    /**
     * Reverses the most recent undoable action and returns a human-readable
     * message describing what happened.
     */
    public String undoLastAction() {
        ActionRecord last = history.peek();
        if (last == null) {
            return "Nothing to undo - the action history is empty.";
        }
        if (!last.isUndoable()) {
            return "The most recent action cannot be undone.";
        }
        history.pop();
        Student snapshot = last.getSnapshot();

        switch (last.getType()) {
            case ADD: {
                studentList.remove(snapshot.getStudentId());
                studentTree.delete(snapshot.getStudentId());
                studentIndex.remove(snapshot.getStudentId());
                return "Undone: student " + snapshot.getStudentId() + " has been removed again.";
            }
            case DELETE: {
                Student restored = copyOf(snapshot);
                studentList.add(restored);
                studentTree.insert(restored);
                studentIndex.put(restored);
                return "Undone: student " + snapshot.getStudentId() + " has been restored.";
            }
            case UPDATE: {
                Student current = studentIndex.get(snapshot.getStudentId());
                if (current == null) {
                    return "Undo failed - student " + snapshot.getStudentId() + " no longer exists.";
                }
                current.setName(snapshot.getName());
                current.setProgramme(snapshot.getProgramme());
                current.setMarks(snapshot.getMarks());
                return "Undone: student " + snapshot.getStudentId()
                        + " has been rolled back to its previous values.";
            }
            default:
                return "Unsupported action type.";
        }
    }

    private Student copyOf(Student s) {
        return new Student(s.getStudentId(), s.getName(), s.getProgramme(), s.getMarks());
    }

    // -------------------------------------------------------- service queue

    public ServiceRequest enqueueRequest(String studentId, String requestType) {
        Student student = studentIndex.get(studentId);
        if (student == null) {
            return null;
        }
        ServiceRequest request = new ServiceRequest(studentId, student.getName(), requestType);
        serviceQueue.enqueue(request);
        history.push(new ActionRecord(ActionRecord.Type.ADD,
                "Queued service request " + request.getTicketNo() + " for " + studentId, null));
        return request;
    }

    public ServiceRequest processNextRequest() {
        ServiceRequest served = serviceQueue.dequeue();
        if (served != null) {
            history.push(new ActionRecord(ActionRecord.Type.DELETE,
                    "Processed service request " + served.getTicketNo(), null));
        }
        return served;
    }

    // ------------------------------------------------------------- accessors

    public structures.StudentLinkedList getStudentList() {
        return studentList;
    }

    public AVLTree getStudentTree() {
        return studentTree;
    }

    public StudentHashTable getStudentIndex() {
        return studentIndex;
    }

    public ActionStack getHistory() {
        return history;
    }

    public RequestQueue getServiceQueue() {
        return serviceQueue;
    }

    public CampusGraph getCampusGraph() {
        return campusGraph;
    }

    public int getStudentCount() {
        return studentList.size();
    }

    // ------------------------------------------------------------ demo data

    /** Loads a small, realistic data set so the demonstration video has content. */
    public void loadSampleData() {
        addStudent(new Student("23DA2-0675", "M.H.M Ijas", "BAIT", 82.5));
        addStudent(new Student("23DA2-0677", "Z. Isham", "BAIT", 76.0));
        addStudent(new Student("23DA2-0634", "M.I.M Arshad", "BAIT", 88.0));
        addStudent(new Student("23DA2-0678", "M.N.M Nafeel", "BAIT", 71.5));
        addStudent(new Student("23DA2-0521", "S. Fathima", "BSc CS", 64.0));
        addStudent(new Student("23DA2-0912", "K. Pradeep", "BSc SE", 55.5));

        String[] places = {
            "Main Gate", "Admin Block", "Library", "Lecture Hall A",
            "Lecture Hall B", "Computer Lab", "Canteen", "Hostel A", "Sports Ground"
        };
        for (String place : places) {
            campusGraph.addLocation(place);
        }
        campusGraph.addConnection("Main Gate", "Admin Block", 120);
        campusGraph.addConnection("Main Gate", "Canteen", 200);
        campusGraph.addConnection("Admin Block", "Library", 80);
        campusGraph.addConnection("Library", "Lecture Hall A", 60);
        campusGraph.addConnection("Lecture Hall A", "Lecture Hall B", 40);
        campusGraph.addConnection("Lecture Hall B", "Computer Lab", 90);
        campusGraph.addConnection("Computer Lab", "Library", 150);
        campusGraph.addConnection("Canteen", "Hostel A", 180);
        campusGraph.addConnection("Hostel A", "Sports Ground", 220);
        campusGraph.addConnection("Canteen", "Lecture Hall A", 130);

        enqueueRequest("23DA2-0675", "Transcript request");
        enqueueRequest("23DA2-0634", "Student ID card replacement");
    }
}
