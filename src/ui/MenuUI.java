package ui;

import model.ActionRecord;
import model.ServiceRequest;
import model.Student;
import service.RecordManager;
import structures.CampusGraph;

import java.util.Scanner;

/**
 * Menu-driven console interface. Option numbers 1-16 follow the assignment
 * specification exactly; the extra features (undo, adjacency-matrix view,
 * shortest path, hash statistics) are offered as sub-options inside the
 * screens they belong to, so the main menu stays as specified.
 *
 * Responsibility: all four members (integration and user interface).
 */
public class MenuUI {

    private static final String LINE =
            "+--------------+------------------------+--------------------+--------+-------+";
    private static final String HEADER =
            "| Student ID   | Name                   | Programme          | Marks  | Grade |";

    private final RecordManager manager;
    private final InputValidator input;
    private final Scanner scanner;
    private final boolean pauseEnabled;

    public MenuUI(RecordManager manager, Scanner scanner, boolean pauseEnabled) {
        this.manager = manager;
        this.scanner = scanner;
        this.input = new InputValidator(scanner);
        this.pauseEnabled = pauseEnabled;
    }

    // ------------------------------------------------------------- main loop

    public void run() {
        printBanner();
        boolean running = true;
        while (running) {
            printMenu();
            int choice = input.readInt("   Enter your choice (1-16): ", 1, 16);
            System.out.println();
            switch (choice) {
                case 1:  addStudentRecord();      break;
                case 2:  updateStudentRecord();   break;
                case 3:  deleteStudentRecord();   break;
                case 4:  displayAllRecords();     break;
                case 5:  addServiceRequest();     break;
                case 6:  processNextRequest();    break;
                case 7:  displayRecentActions();  break;
                case 8:  displayUsingTree();      break;
                case 9:  searchUsingHashing();    break;
                case 10: addCampusLocation();     break;
                case 11: removeCampusLocation();  break;
                case 12: addCampusConnection();   break;
                case 13: removeCampusConnection();break;
                case 14: displayCampusNetwork();  break;
                case 15: traverseCampus();        break;
                case 16: running = confirmExit(); break;
                default: System.out.println("   Invalid option.");
            }
        }
    }

    private void printBanner() {
        System.out.println();
        System.out.println("===================================================================");
        System.out.println("   UNIVERSITY STUDENT RECORD & CAMPUS ROUTE MANAGEMENT SYSTEM");
        System.out.println("   CIT300 - Data Structures and Algorithms | Assignment 1");
        System.out.println("   SLTC Research University");
        System.out.println("-------------------------------------------------------------------");
        System.out.println("   Group Members:");
        System.out.println("     M.H.M Ijas   - 23DA2-0675 (Group Leader) : Linked List");
        System.out.println("     Z. Isham     - 23DA2-0677               : Stack & Queue");
        System.out.println("     M.I.M Arshad - 23DA2-0634               : AVL Tree & Hashing");
        System.out.println("     M.N.M Nafeel - 23DA2-0678               : Graph & Traversal");
        System.out.println("===================================================================");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("   +---------------------- MAIN MENU ----------------------+");
        System.out.println("   |  1. Add Student Record                                |");
        System.out.println("   |  2. Update Student Record                             |");
        System.out.println("   |  3. Delete Student Record                             |");
        System.out.println("   |  4. Display All Records using Linked List             |");
        System.out.println("   |  5. Add Service Request to Queue                      |");
        System.out.println("   |  6. Process Next Service Request                      |");
        System.out.println("   |  7. Display Recent Actions using Stack  (+ Undo)      |");
        System.out.println("   |  8. Display Students using BST/AVL                    |");
        System.out.println("   |  9. Search Student using Hashing                      |");
        System.out.println("   | 10. Add Campus Location                               |");
        System.out.println("   | 11. Remove Campus Location                            |");
        System.out.println("   | 12. Add Campus Connection/Road                        |");
        System.out.println("   | 13. Remove Campus Connection/Road                     |");
        System.out.println("   | 14. Display Campus Connections                        |");
        System.out.println("   | 15. Traverse Campus Locations using BFS or DFS        |");
        System.out.println("   | 16. Exit                                              |");
        System.out.println("   +-------------------------------------------------------+");
        System.out.printf ("   Records: %d | Queue: %d | Locations: %d | Roads: %d%n",
                manager.getStudentCount(),
                manager.getServiceQueue().size(),
                manager.getCampusGraph().getVertexCount(),
                manager.getCampusGraph().getEdgeCount());
    }

    private void pause() {
        if (pauseEnabled) {
            input.pause();
        }
    }

    private void heading(String title, String owner) {
        System.out.println("   --- " + title + " ---");
        System.out.println("   [Data structure: " + owner + "]");
        System.out.println();
    }

    // ------------------------------------------------- 1. add student record

    private void addStudentRecord() {
        heading("ADD STUDENT RECORD", "Linked List + AVL Tree + Hash Table");
        String id = input.readStudentId("   Student ID   : ");

        if (manager.searchByHash(id) != null) {
            System.out.println("\n   ! A record with Student ID " + id + " already exists.");
            System.out.println("     Duplicate IDs are rejected. Use option 2 to update it instead.");
            pause();
            return;
        }

        String name = input.readName("   Full Name    : ");
        String programme = input.readNonEmpty("   Programme    : ");
        double marks = input.readMarks("   Marks (0-100): ");

        boolean added = manager.addStudent(new Student(id, name, programme, marks));
        if (added) {
            System.out.println("\n   [OK] Student added to the linked list, the AVL tree and the hash table.");
        } else {
            System.out.println("\n   ! The record could not be added.");
        }
        pause();
    }

    // ---------------------------------------------- 2. update student record

    private void updateStudentRecord() {
        heading("UPDATE STUDENT RECORD", "Hash Table lookup, in-place update");
        if (manager.getStudentCount() == 0) {
            System.out.println("   ! There are no student records yet. Add one with option 1.");
            pause();
            return;
        }

        String id = input.readStudentId("   Student ID to update: ");
        Student existing = manager.searchByHash(id);
        if (existing == null) {
            System.out.println("\n   ! No record found for Student ID " + id + ".");
            pause();
            return;
        }

        System.out.println("\n   Current record:");
        System.out.println("   " + LINE);
        System.out.println("   " + HEADER);
        System.out.println("   " + LINE);
        System.out.println("   " + existing.toRow());
        System.out.println("   " + LINE);
        System.out.println("\n   Press Enter on any field to keep its current value.");

        String name = input.readOptional("   New Name      : ");
        String programme = input.readOptional("   New Programme : ");
        Double marks = input.readOptionalMarks("   New Marks     : ");

        if (name.isEmpty() && programme.isEmpty() && marks == null) {
            System.out.println("\n   Nothing was changed.");
            pause();
            return;
        }

        manager.updateStudent(id, name, programme, marks);
        System.out.println("\n   [OK] Record updated. The change is visible in all three structures.");
        System.out.println("   " + manager.searchByHash(id).toRow());
        pause();
    }

    // ---------------------------------------------- 3. delete student record

    private void deleteStudentRecord() {
        heading("DELETE STUDENT RECORD", "Removed from Linked List, AVL Tree and Hash Table");
        if (manager.getStudentCount() == 0) {
            System.out.println("   ! There are no student records to delete.");
            pause();
            return;
        }

        String id = input.readStudentId("   Student ID to delete: ");
        Student target = manager.searchByHash(id);
        if (target == null) {
            System.out.println("\n   ! No record found for Student ID " + id + ".");
            pause();
            return;
        }

        System.out.println("\n   " + target.toRow());
        if (!input.readYesNo("\n   Delete this record? (Y/N): ")) {
            System.out.println("\n   Deletion cancelled.");
            pause();
            return;
        }

        manager.deleteStudent(id);
        System.out.println("\n   [OK] Record deleted and pushed onto the action stack.");
        System.out.println("        It can be restored from option 7 (Undo).");
        pause();
    }

    // --------------------------------------- 4. display all (linked list)

    private void displayAllRecords() {
        heading("ALL STUDENT RECORDS (LINKED LIST ORDER)", "Singly Linked List traversal");
        Student[] students = manager.allStudentsInInsertionOrder();
        printTable(students);
        System.out.println("\n   Traversal: head -> ... -> null, " + students.length + " node(s) visited.");
        pause();
    }

    private void printTable(Student[] students) {
        if (students.length == 0) {
            System.out.println("   (no records to display)");
            return;
        }
        System.out.println("   " + LINE);
        System.out.println("   " + HEADER);
        System.out.println("   " + LINE);
        for (Student s : students) {
            System.out.println("   " + s.toRow());
        }
        System.out.println("   " + LINE);
        System.out.println("   Total records: " + students.length);
    }

    // ----------------------------------------------- 5. add service request

    private void addServiceRequest() {
        heading("ADD SERVICE REQUEST TO QUEUE", "Queue (FIFO, enqueue at rear)");
        if (manager.getStudentCount() == 0) {
            System.out.println("   ! Add a student record first - requests are linked to a student.");
            pause();
            return;
        }

        String id = input.readStudentId("   Student ID : ");
        if (manager.searchByHash(id) == null) {
            System.out.println("\n   ! No student record found for " + id
                    + ". A request cannot be raised for an unknown student.");
            pause();
            return;
        }

        System.out.println("\n   Request types:");
        System.out.println("     1. Transcript request");
        System.out.println("     2. Student ID card replacement");
        System.out.println("     3. Bonafide / confirmation letter");
        System.out.println("     4. Exam re-correction");
        System.out.println("     5. Other");
        int type = input.readInt("   Choose request type (1-5): ", 1, 5);
        String typeName;
        switch (type) {
            case 1: typeName = "Transcript request"; break;
            case 2: typeName = "Student ID card replacement"; break;
            case 3: typeName = "Bonafide / confirmation letter"; break;
            case 4: typeName = "Exam re-correction"; break;
            default: typeName = input.readNonEmpty("   Describe the request: ");
        }

        ServiceRequest request = manager.enqueueRequest(id, typeName);
        System.out.println("\n   [OK] Request enqueued.");
        System.out.println("   " + request);
        System.out.println("   Position in queue: " + manager.getServiceQueue().size());
        pause();
    }

    // ------------------------------------------- 6. process next request

    private void processNextRequest() {
        heading("PROCESS NEXT SERVICE REQUEST", "Queue (FIFO, dequeue from front)");
        if (manager.getServiceQueue().isEmpty()) {
            System.out.println("   ! The service queue is empty - there is nothing to process.");
            pause();
            return;
        }

        System.out.println("   Waiting list before processing:");
        ServiceRequest[] waiting = manager.getServiceQueue().toArray();
        for (int i = 0; i < waiting.length; i++) {
            System.out.println("     " + (i + 1) + ". " + waiting[i]);
        }

        ServiceRequest served = manager.processNextRequest();
        System.out.println("\n   [OK] Now serving: " + served);
        System.out.println("   Requests still waiting: " + manager.getServiceQueue().size());
        pause();
    }

    // ------------------------------------ 7. recent actions (stack) + undo

    private void displayRecentActions() {
        heading("RECENT ACTIONS (MOST RECENT FIRST)", "Stack (LIFO)");
        ActionRecord[] actions = manager.getHistory().toArray();
        if (actions.length == 0) {
            System.out.println("   (no actions recorded yet)");
            pause();
            return;
        }
        int shown = Math.min(actions.length, 15);
        for (int i = 0; i < shown; i++) {
            System.out.printf("   %2d. %s%n", i + 1, actions[i]);
        }
        if (actions.length > shown) {
            System.out.println("   ... and " + (actions.length - shown) + " older action(s).");
        }
        System.out.println("\n   Stack size: " + manager.getHistory().size());

        ActionRecord top = manager.getHistory().peek();
        if (top != null && top.isUndoable()) {
            if (input.readYesNo("\n   Undo the most recent action? (Y/N): ")) {
                System.out.println("\n   " + manager.undoLastAction());
            }
        }
        pause();
    }

    // ---------------------------------------------- 8. display using AVL

    private void displayUsingTree() {
        heading("STUDENTS IN SORTED ORDER", "AVL Tree (self-balancing BST), in-order traversal");
        Student[] sorted = manager.allStudentsSortedById();
        printTable(sorted);
        System.out.println("\n   Tree height : " + manager.getStudentTree().height()
                + "  (a plain BST on sorted IDs would be " + manager.getStudentTree().size() + ")");
        System.out.println("   Tree shape (rotated 90 degrees, root on the left edge):");
        System.out.print(manager.getStudentTree().structureView());
        pause();
    }

    // ------------------------------------------- 9. search using hashing

    private void searchUsingHashing() {
        heading("SEARCH STUDENT USING HASHING", "Hash Table with separate chaining");
        if (manager.getStudentCount() == 0) {
            System.out.println("   ! There are no student records to search.");
            pause();
            return;
        }

        String id = input.readStudentId("   Student ID to search: ");

        long hashStart = System.nanoTime();
        Student byHash = manager.searchByHash(id);
        long hashTime = System.nanoTime() - hashStart;

        long treeStart = System.nanoTime();
        Student byTree = manager.searchByTree(id);
        long treeTime = System.nanoTime() - treeStart;

        System.out.println();
        if (byHash == null) {
            System.out.println("   ! No student found with ID " + id + ".");
        } else {
            System.out.println("   [FOUND]");
            System.out.println("   " + LINE);
            System.out.println("   " + HEADER);
            System.out.println("   " + LINE);
            System.out.println("   " + byHash.toRow());
            System.out.println("   " + LINE);
        }

        System.out.println("\n   Search comparison:");
        System.out.printf("     Hash table : %s, %d chain step(s), %d ns%n",
                byHash != null ? "found" : "not found",
                manager.getStudentIndex().getLastProbeCount(), hashTime);
        System.out.printf("     AVL tree   : %s, %d ns%n",
                byTree != null ? "found" : "not found", treeTime);

        if (input.readYesNo("\n   Show hash table statistics? (Y/N): ")) {
            System.out.println();
            System.out.print(manager.getStudentIndex().statistics());
        }
        pause();
    }

    // --------------------------------------------- 10. add campus location

    private void addCampusLocation() {
        heading("ADD CAMPUS LOCATION", "Graph vertex (adjacency list)");
        String name = input.readName("   Location name: ");
        if (manager.getCampusGraph().addLocation(name)) {
            System.out.println("\n   [OK] Location '" + name + "' added to the campus graph.");
            System.out.println("   Total locations: " + manager.getCampusGraph().getVertexCount());
        } else {
            System.out.println("\n   ! '" + name + "' already exists on the campus map. Duplicates are not allowed.");
        }
        pause();
    }

    // ------------------------------------------ 11. remove campus location

    private void removeCampusLocation() {
        heading("REMOVE CAMPUS LOCATION", "Graph vertex removal + cleanup of every attached road");
        CampusGraph graph = manager.getCampusGraph();
        if (graph.isEmpty()) {
            System.out.println("   ! There are no campus locations to remove.");
            pause();
            return;
        }
        listLocations();

        String name = input.readNonEmpty("\n   Location to remove: ");
        if (!graph.hasLocation(name)) {
            System.out.println("\n   ! '" + name + "' is not on the campus map.");
            pause();
            return;
        }
        String[] attached = graph.neighbours(name);
        if (attached.length > 0) {
            System.out.println("\n   This location has " + attached.length
                    + " road(s) attached; they will be removed as well.");
        }
        if (!input.readYesNo("   Continue? (Y/N): ")) {
            System.out.println("\n   Removal cancelled.");
            pause();
            return;
        }
        graph.removeLocation(name);
        System.out.println("\n   [OK] Location and all its roads removed.");
        pause();
    }

    // -------------------------------------------- 12. add campus connection

    private void addCampusConnection() {
        heading("ADD CAMPUS CONNECTION / ROAD", "Graph edge (undirected, weighted)");
        CampusGraph graph = manager.getCampusGraph();
        if (graph.getVertexCount() < 2) {
            System.out.println("   ! At least two locations are needed before a road can be added.");
            pause();
            return;
        }
        listLocations();

        String from = input.readNonEmpty("\n   From location: ");
        String to = input.readNonEmpty("   To location  : ");
        int distance = input.readInt("   Distance (m) : ", 1, 100000);

        int result = graph.addConnection(from, to, distance);
        System.out.println();
        switch (result) {
            case 0:
                System.out.println("   [OK] Road added: " + from + " <-> " + to + " (" + distance + "m).");
                System.out.println("   Total roads: " + graph.getEdgeCount());
                break;
            case 1:
                System.out.println("   ! One or both locations do not exist. Add them with option 10 first.");
                break;
            case 2:
                System.out.println("   ! A road between these two locations already exists.");
                break;
            default:
                System.out.println("   ! A location cannot be connected to itself.");
        }
        pause();
    }

    // ----------------------------------------- 13. remove campus connection

    private void removeCampusConnection() {
        heading("REMOVE CAMPUS CONNECTION / ROAD", "Graph edge removal (both directions)");
        CampusGraph graph = manager.getCampusGraph();
        if (graph.getEdgeCount() == 0) {
            System.out.println("   ! There are no roads to remove.");
            pause();
            return;
        }
        System.out.print(graph.adjacencyListView());

        String from = input.readNonEmpty("\n   From location: ");
        String to = input.readNonEmpty("   To location  : ");

        int result = graph.removeConnection(from, to);
        System.out.println();
        switch (result) {
            case 0:
                System.out.println("   [OK] Road removed between " + from + " and " + to + ".");
                break;
            case 1:
                System.out.println("   ! One or both locations do not exist.");
                break;
            default:
                System.out.println("   ! There is no direct road between these two locations.");
        }
        pause();
    }

    // --------------------------------------------- 14. display the network

    private void displayCampusNetwork() {
        heading("CAMPUS NETWORK", "Graph representation");
        CampusGraph graph = manager.getCampusGraph();
        if (graph.isEmpty()) {
            System.out.println("   ! No campus locations have been recorded yet.");
            pause();
            return;
        }
        System.out.println("   1. Adjacency list");
        System.out.println("   2. Adjacency matrix");
        System.out.println("   3. Both");
        int view = input.readInt("   Choose a view (1-3): ", 1, 3);

        System.out.println();
        if (view == 1 || view == 3) {
            System.out.println("   ADJACENCY LIST");
            System.out.print(graph.adjacencyListView());
        }
        if (view == 2 || view == 3) {
            System.out.println("\n   ADJACENCY MATRIX (1 = direct road)");
            System.out.print(graph.adjacencyMatrixView());
        }
        System.out.println("\n   Locations: " + graph.getVertexCount()
                + " | Roads: " + graph.getEdgeCount());
        pause();
    }

    // ---------------------------------------------- 15. traverse the campus

    private void traverseCampus() {
        heading("TRAVERSE CAMPUS LOCATIONS", "Graph traversal: BFS (queue) / DFS (stack)");
        CampusGraph graph = manager.getCampusGraph();
        if (graph.isEmpty()) {
            System.out.println("   ! No campus locations have been recorded yet.");
            pause();
            return;
        }
        listLocations();

        System.out.println("\n   1. Breadth-First Search (BFS)");
        System.out.println("   2. Depth-First Search (DFS)");
        System.out.println("   3. Both");
        System.out.println("   4. Shortest route between two locations (BFS based)");
        int mode = input.readInt("   Choose (1-4): ", 1, 4);

        if (mode == 4) {
            shortestRoute(graph);
            return;
        }

        String start = input.readNonEmpty("   Start location: ");
        if (!graph.hasLocation(start)) {
            System.out.println("\n   ! '" + start + "' is not on the campus map.");
            pause();
            return;
        }

        System.out.println();
        if (mode == 1 || mode == 3) {
            printOrder("BFS visit order", graph.bfs(start));
        }
        if (mode == 2 || mode == 3) {
            printOrder("DFS visit order", graph.dfs(start));
        }

        int reachable = graph.bfs(start).length;
        if (reachable < graph.getVertexCount()) {
            System.out.println("\n   Note: " + (graph.getVertexCount() - reachable)
                    + " location(s) are not reachable from " + start
                    + " - the campus graph is not fully connected.");
        }
        pause();
    }

    private void shortestRoute(CampusGraph graph) {
        String from = input.readNonEmpty("   From location: ");
        String to = input.readNonEmpty("   To location  : ");

        if (!graph.hasLocation(from) || !graph.hasLocation(to)) {
            System.out.println("\n   ! One or both locations are not on the campus map.");
            pause();
            return;
        }

        String[] path = graph.shortestPath(from, to);
        System.out.println();
        if (path == null) {
            System.out.println("   ! There is no route between " + from + " and " + to + ".");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < path.length; i++) {
                sb.append(path[i]);
                if (i < path.length - 1) {
                    sb.append(" -> ");
                }
            }
            System.out.println("   Shortest route (" + (path.length - 1) + " road(s)):");
            System.out.println("   " + sb);
        }
        pause();
    }

    private void printOrder(String title, String[] order) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < order.length; i++) {
            sb.append(order[i]);
            if (i < order.length - 1) {
                sb.append(" -> ");
            }
        }
        System.out.println("   " + title + " (" + order.length + " location(s)):");
        System.out.println("   " + sb);
        System.out.println();
    }

    private void listLocations() {
        String[] names = manager.getCampusGraph().locationNames();
        System.out.println("   Known locations:");
        StringBuilder sb = new StringBuilder("     ");
        for (int i = 0; i < names.length; i++) {
            sb.append(names[i]);
            if (i < names.length - 1) {
                sb.append(" | ");
            }
            if ((i + 1) % 4 == 0 && i < names.length - 1) {
                sb.append("\n     ");
            }
        }
        System.out.println(sb);
    }

    // ----------------------------------------------------------- 16. exit

    private boolean confirmExit() {
        if (input.readYesNo("   Are you sure you want to exit? (Y/N): ")) {
            System.out.println();
            System.out.println("   Session summary:");
            System.out.println("     Student records   : " + manager.getStudentCount());
            System.out.println("     Actions recorded  : " + manager.getHistory().size());
            System.out.println("     Requests pending  : " + manager.getServiceQueue().size());
            System.out.println("     Campus locations  : " + manager.getCampusGraph().getVertexCount());
            System.out.println("     Campus roads      : " + manager.getCampusGraph().getEdgeCount());
            System.out.println();
            System.out.println("   Thank you for using the Campus Record System. Goodbye!");
            System.out.println("===================================================================");
            return false;
        }
        return true;
    }
}
// Menu options 8 and 9 - AVL display and hash search - Member 3 (M.I.M Arshad)
// Menu options 10 to 15 - campus locations and traversal - Member 4 (M.N.M Nafeel)
