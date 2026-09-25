# University Student Record and Campus Route Management System

**Module:** CIT300 – Data Structures and Algorithms
**Assessment:** Graded Practical Assignment 1 (Week 10) – 10% of the final module grade
**Institution:** SLTC Research University
**Language:** Java (console application, no external libraries)

A menu-driven Java console application that manages university student records and models the
campus road network. It demonstrates the practical use of **linked lists, stacks, queues, AVL
trees, hashing and graphs** — all implemented from scratch, with no `java.util` collection used
for any of the required structures.

---

## 1. Group Members and Responsibilities

| # | Name | Student ID | Assigned Responsibility | Individual Contribution |
|---|------|-----------|--------------------------|--------------------------|
| 1 | **M.H.M Ijas** *(Group Leader)* | 23DA2-0675 | Linked list implementation and student-record management | Built `StudentLinkedList` (singly linked list with insert at tail, linear search, node unlinking, array export). Designed the `Student` model class including grade derivation and the table row formatting. Implemented menu options 1–4 (Add, Update, Delete, Display All). Coordinated the group, merged the branches and maintained the README. |
| 2 | **Z. Isham** | 23DA2-0677 | Stack and queue implementation and related operations | Built `ActionStack` (linked LIFO stack, push/pop/peek/toArray) and `RequestQueue` (FIFO queue with front and rear pointers for O(1) enqueue and dequeue). Designed the `ServiceRequest` and `ActionRecord` models including auto-generated ticket numbers and timestamps. Implemented menu options 5, 6 and 7, and the **Undo** feature that reverses the last Add / Update / Delete from the stack. |
| 3 | **M.I.M Arshad** | 23DA2-0634 | BST/AVL tree implementation and hashing/search functionality | Built `AVLTree` — a self-balancing BST with all four rotation cases (LL, LR, RR, RL), insert, delete (including the two-child in-order-successor case), search, in-order traversal and the sideways tree-shape view. Built `StudentHashTable` — separate chaining with a base-31 polynomial hash, automatic resizing at a 0.75 load factor, collision counting and probe statistics. Implemented menu options 8 and 9, including the hash-vs-tree search comparison. |
| 4 | **M.N.M Nafeel** | 23DA2-0678 | Graph implementation, campus locations, connections and BFS/DFS traversal | Built `CampusGraph` — an undirected weighted graph on an **adjacency list**, with add/remove location, add/remove road (cleaning up every attached edge), an adjacency-matrix view, neighbour listing, **BFS** (hand-written queue), iterative **DFS** (hand-written stack) and a BFS-based shortest-route finder. Implemented menu options 10–15. |
| — | **All Members** | — | Integration, validation, testing, debugging, documentation, GitHub collaboration | Jointly built `RecordManager` (the integration layer that keeps all three student structures in step), `InputValidator`, `MenuUI` and the `StructureTest` suite. Jointly produced this README, the test plan, the Git workflow and the demonstration video. |

> Every member can explain and demonstrate their own component. The video segments in
> `docs/DEMO_VIDEO_SCRIPT.md` are split along exactly these lines.

---

## 2. How to Compile and Run

### Requirements
Java JDK 8 or newer (`javac` and `java` on the PATH). The project was compiled and tested on
JDK 21. No build tool and no external library are required.

### Windows
```bat
run.bat
```

### Linux / macOS
```bash
chmod +x run.sh
./run.sh
```

### Manual compile and run
```bash
javac -d bin src/model/*.java src/structures/*.java src/service/*.java src/ui/*.java src/app/*.java
java -cp bin app.Main
```

On start the program asks whether to load the sample data set (6 students, 9 campus locations,
10 roads and 2 pending service requests). Answer **Y** for the demonstration.

### Command-line flags
| Flag | Effect |
|------|--------|
| `--sample` | Load the sample data set without asking |
| `--empty` | Start with an empty system without asking |
| `--no-pause` | Skip the "Press Enter" pauses (used for automated testing) |

### Running the automated test suite
```bash
java -cp bin app.StructureTest
```
This exercises every data structure directly and prints a PASS/FAIL line per check.
Current result: **99 passed, 0 failed.**

---

## 3. Project Structure

```
CampusRecordSystem/
├── README.md                       <- this file (group details and contributions)
├── run.bat                         <- one-click compile + run on Windows
├── run.sh                          <- one-click compile + run on Linux/macOS
├── .gitignore
├── src/
│   ├── model/
│   │   ├── Student.java            <- student record (ID, name, programme, marks, grade)
│   │   ├── ServiceRequest.java     <- one queued counter request with a ticket number
│   │   └── ActionRecord.java       <- one undoable entry of the action history
│   ├── structures/
│   │   ├── StudentLinkedList.java  <- Member 1 : singly linked list
│   │   ├── ActionStack.java        <- Member 2 : LIFO stack
│   │   ├── RequestQueue.java       <- Member 2 : FIFO queue
│   │   ├── AVLTree.java            <- Member 3 : self-balancing BST
│   │   ├── StudentHashTable.java   <- Member 3 : hash table, separate chaining
│   │   └── CampusGraph.java        <- Member 4 : adjacency-list graph, BFS/DFS
│   ├── service/
│   │   └── RecordManager.java      <- integration layer, undo logic, sample data
│   ├── ui/
│   │   ├── InputValidator.java     <- all console reads and validation rules
│   │   └── MenuUI.java             <- the 16-option menu
│   └── app/
│       ├── Main.java               <- entry point
│       └── StructureTest.java      <- 99-check automated test suite
└── docs/
    ├── TEST_PLAN.md                <- test cases and results
    ├── GIT_WORKFLOW.md             <- branches, commits and pull requests per member
    ├── DEMO_VIDEO_SCRIPT.md        <- 13-minute video script, split per member
    ├── SUBMISSION_CHECKLIST.md     <- final checks before the LMS submission
    └── test_input.txt              <- scripted input used for the end-to-end run
```

---

## 4. Menu

```
 1. Add Student Record                     9. Search Student using Hashing
 2. Update Student Record                 10. Add Campus Location
 3. Delete Student Record                 11. Remove Campus Location
 4. Display All Records using Linked List 12. Add Campus Connection/Road
 5. Add Service Request to Queue          13. Remove Campus Connection/Road
 6. Process Next Service Request          14. Display Campus Connections
 7. Display Recent Actions using Stack    15. Traverse Campus Locations using BFS or DFS
 8. Display Students using BST/AVL        16. Exit
```

The option numbers follow the assignment specification exactly. The extra features are offered
as sub-options inside the screen they belong to, so the main menu stays as specified:

| Extra feature | Where to find it |
|---|---|
| **Undo** the last Add / Update / Delete | Option 7, after the action list is shown |
| **Adjacency matrix** view | Option 14, view 2 or 3 |
| **Shortest route** between two locations | Option 15, choice 4 |
| **Hash table statistics** (buckets, load factor, collisions, chains) | Option 9, after the search |
| **Hash vs AVL search comparison** (chain steps and nanoseconds) | Option 9, shown automatically |

---

## 5. How Each Requirement Is Met

| # | Requirement | Where it is implemented |
|---|-------------|--------------------------|
| 1 | Store Student ID, Name, Programme, Marks | `model/Student.java` |
| 2 | Linked list stores and manages records | `structures/StudentLinkedList.java`, menu 4 |
| 3 | Stack for recent actions / undo | `structures/ActionStack.java`, menu 7 |
| 4 | Queue for service requests in arrival order | `structures/RequestQueue.java`, menu 5 and 6 |
| 5 | BST/AVL organises and searches by Student ID | `structures/AVLTree.java`, menu 8 |
| 6 | Hashing supports efficient ID searching | `structures/StudentHashTable.java`, menu 9 |
| 7 | Graph represents campus locations and connections | `structures/CampusGraph.java`, menu 10–15 |
| 8 | Adjacency list or matrix representation | Adjacency list is the store; matrix view in menu 14 |
| 9 | Add and remove locations and roads | Menu 10, 11, 12, 13 |
| 10 | Display connected locations / the campus network | Menu 14 |
| 11 | At least one traversal: BFS or DFS | **Both** BFS and DFS, menu 15 |
| 12 | Add, update, delete, search, display for records | Menu 1, 2, 3, 9, 4 and 8 |
| 13 | Menu-driven interface with input validation | `ui/MenuUI.java`, `ui/InputValidator.java` |
| 14 | Handle invalid input, duplicates, missing records, invalid marks, unavailable connections | See section 7 |

---

## 6. Why Each Structure Was Chosen

**Singly linked list — primary record store.** Student records are added and removed constantly,
and a linked list does both without shifting elements the way an array does. It also gives the
natural "insertion order" view for option 4.

**Stack — action history and undo.** The last action performed is always the first one a user
wants to reverse, which is exactly LIFO. Each entry keeps a snapshot of the record before the
change, so an Add can be un-added, a Delete can be restored and an Update can be rolled back.

**Queue — service requests.** Students must be served in the order they arrived. The queue keeps
both a front and a rear pointer so enqueue and dequeue are O(1) rather than O(n).

**AVL tree — sorted view and ordered search.** Real student IDs are issued in ascending order
(23DA2-0675, 23DA2-0676, 23DA2-0677 …). Inserting sorted keys into a plain BST degenerates it into
a linked list with O(n) search. The AVL rotations keep the height at O(log n): the demonstration
inserts 15 sequential IDs and the tree height stays at 4 instead of 15.

**Hash table — fast lookup by ID.** Searching by Student ID is the single most frequent operation,
and hashing makes it O(1) on average. Separate chaining was chosen over open addressing because
deletion is simpler and correctness does not depend on tombstones. The hash is a base-31
polynomial over the whole ID rather than a length or character sum, because campus IDs share the
long common prefix `23DA2-` and a weak hash would pile every record into one bucket.

**Graph (adjacency list) — campus map.** A campus is a sparse network: a location connects to a
handful of neighbours, not to all the others. An adjacency list stores only the roads that exist,
using O(V + E) memory, whereas a matrix would always use O(V²). The matrix view is still provided
in option 14 because the specification mentions both representations.

---

## 7. Input Validation and Error Handling

| Situation | System response |
|---|---|
| Menu choice outside 1–16, or not a number | Re-prompts: "Enter a number between 1 and 16." |
| Blank required field | Re-prompts: "This field cannot be left blank." |
| Malformed Student ID | Re-prompts with the accepted format and the example `23DA2-0675` |
| Marks outside 0–100 or not numeric | Re-prompts: "Marks must be between 0 and 100." |
| Duplicate Student ID on Add | Rejected, with a pointer to option 2 (Update) |
| Update / Delete / Search on a missing ID | "No record found for Student ID …" and returns to the menu |
| Update with every field left blank | "Nothing was changed." |
| Delete | Asks for Y/N confirmation and shows the record first |
| Service request for a student who does not exist | Refused — a request cannot be raised for an unknown student |
| Processing an empty queue | "The service queue is empty." |
| Undo with an empty history | "Nothing to undo." |
| Duplicate campus location | "… already exists on the campus map." |
| Road between locations that do not exist | "One or both locations do not exist." |
| Road that already exists | "A road between these two locations already exists." |
| Road from a location to itself | "A location cannot be connected to itself." |
| Removing a road that does not exist | "There is no direct road between these two locations." |
| Removing a location that has roads | Warns how many roads will go with it, asks for confirmation, then removes them cleanly |
| Traversal from an unknown start location | "… is not on the campus map." |
| Traversal that cannot reach every location | Reports how many locations are unreachable (disconnected graph) |
| Shortest route with no path | "There is no route between … and …" |

---

## 8. Time Complexity Summary

| Operation | Structure used | Average | Worst |
|---|---|---|---|
| Add student | Linked list + AVL + hash | O(n) | O(n) |
| Search by ID | Hash table | **O(1)** | O(n) |
| Search by ID | AVL tree | **O(log n)** | O(log n) |
| Delete student | All three | O(n) | O(n) |
| Display all (insertion order) | Linked list | O(n) | O(n) |
| Display all (sorted) | AVL in-order | O(n) | O(n) |
| Push / pop action | Stack | **O(1)** | O(1) |
| Enqueue / dequeue request | Queue | **O(1)** | O(1) |
| Add / remove location | Graph | O(V + E) | O(V + E) |
| Add / remove road | Graph | O(V + deg) | O(V + E) |
| BFS / DFS traversal | Graph | O(V + E) | O(V + E) |

Adding a student is O(n) because the linked list appends at the tail; the AVL insert is O(log n)
and the hash insert is O(1) on average, so the list is the limiting factor. This is the deliberate
trade-off for keeping an insertion-ordered view.

---

## 9. Testing

Two levels of testing were carried out; both are reproducible.

1. **Automated structure tests** — `java -cp bin app.StructureTest` runs 99 checks across all six
   structures plus the integration layer, including boundary cases (empty pop, empty dequeue,
   duplicate keys, two-child AVL delete, dense-graph DFS, disconnected graph). Result: 99/99 pass.
2. **Scripted end-to-end run** — `java -cp bin app.Main --sample --no-pause < docs/test_input.txt`
   drives all 16 menu options including every invalid-input path, and completes with no exception.

Full case-by-case results are in `docs/TEST_PLAN.md`.

---

## 10. Deliverables Checklist

- [x] Complete project implemented, including all data structures and the graph component
- [x] All group members' names and student IDs recorded correctly
- [x] Responsibilities and individual contributions documented (section 1)
- [ ] GitHub repository with commits, branches and pull requests — see `docs/GIT_WORKFLOW.md`
- [ ] Merged demonstration video under 15 minutes, all faces visible — see `docs/DEMO_VIDEO_SCRIPT.md`
- [ ] Complete project uploaded to Google Drive
- [ ] Google Drive link copied into a Notepad (.txt) file
- [ ] Editor access granted to **asanka.r@sltc.ac.lk**
- [ ] Editor access granted to **kaushika.w@sltc.ac.lk**
- [ ] Drive permissions verified before submitting
- [ ] Submitted through the designated LMS link on or before **29 September**

The unticked items are the ones the group must complete outside the code itself.
`docs/SUBMISSION_CHECKLIST.md` walks through each of them step by step.
