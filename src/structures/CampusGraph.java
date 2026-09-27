package structures;

/**
 * Undirected weighted graph of campus locations, stored as an ADJACENCY LIST.
 *
 *   Vertex = a campus location (Main Gate, Library, Hostel A, ...)
 *   Edge   = a road / walking path between two locations, with a distance in metres
 *
 * The adjacency list is built from hand-written linked structures, and the
 * BFS queue / DFS stack are also hand-written, so no java.util collection is
 * used anywhere in this class.
 *
 * Responsibility: Member 4 - M.N.M Nafeel (23DA2-0678)
 */
public class CampusGraph {

    /** One neighbour reference inside a vertex's edge list. */
    private static class Edge {
        Vertex target;
        int distance;
        Edge next;

        Edge(Vertex target, int distance, Edge next) {
            this.target = target;
            this.distance = distance;
            this.next = next;
        }
    }

    /** One campus location. Vertices themselves are held in a linked list. */
    private static class Vertex {
        String name;
        Edge edges;
        Vertex next;
        boolean visited;

        Vertex(String name) {
            this.name = name;
        }
    }

    private Vertex head;
    private int vertexCount;
    private int edgeCount;

    // --------------------------------------------------------------- lookup

    private Vertex findVertex(String name) {
        Vertex cursor = head;
        while (cursor != null) {
            if (cursor.name.equalsIgnoreCase(name)) {
                return cursor;
            }
            cursor = cursor.next;
        }
        return null;
    }

    public boolean hasLocation(String name) {
        return findVertex(name) != null;
    }

    // ------------------------------------------------------------- vertices

    /** Adds a campus location. Returns false when the location already exists. */
    public boolean addLocation(String name) {
        if (name == null || name.trim().isEmpty() || findVertex(name) != null) {
            return false;
        }
        Vertex fresh = new Vertex(name.trim());
        fresh.next = head;
        head = fresh;
        vertexCount++;
        return true;
    }

    /**
     * Removes a location and every road touching it, so no dangling edge is
     * left behind. Returns false when the location does not exist.
     */
    public boolean removeLocation(String name) {
        Vertex target = findVertex(name);
        if (target == null) {
            return false;
        }

        // 1. drop every edge that points INTO the target
        Vertex cursor = head;
        while (cursor != null) {
            if (cursor != target) {
                cursor.edges = unlinkEdge(cursor.edges, target);
            }
            cursor = cursor.next;
        }

        // 2. count and drop the target's own outgoing edges
        Edge e = target.edges;
        while (e != null) {
            edgeCount--;
            e = e.next;
        }

        // 3. unlink the vertex itself
        if (head == target) {
            head = head.next;
        } else {
            Vertex previous = head;
            while (previous.next != target) {
                previous = previous.next;
            }
            previous.next = target.next;
        }
        vertexCount--;
        return true;
    }

    private Edge unlinkEdge(Edge list, Vertex target) {
        while (list != null && list.target == target) {
            list = list.next;
        }
        if (list == null) {
            return null;
        }
        Edge cursor = list;
        while (cursor.next != null) {
            if (cursor.next.target == target) {
                cursor.next = cursor.next.next;
            } else {
                cursor = cursor.next;
            }
        }
        return list;
    }

    // ---------------------------------------------------------------- edges

    /**
     * Adds an undirected road between two existing locations.
     * Result codes: 0 = added, 1 = a location is missing, 2 = road already exists,
     *               3 = both ends are the same location.
     */
    public int addConnection(String from, String to, int distance) {
        Vertex a = findVertex(from);
        Vertex b = findVertex(to);
        if (a == null || b == null) {
            return 1;
        }
        if (a == b) {
            return 3;
        }
        if (hasEdge(a, b)) {
            return 2;
        }
        a.edges = new Edge(b, distance, a.edges);
        b.edges = new Edge(a, distance, b.edges);   // undirected: link both ways
        edgeCount++;
        return 0;
    }

    private boolean hasEdge(Vertex a, Vertex b) {
        Edge cursor = a.edges;
        while (cursor != null) {
            if (cursor.target == b) {
                return true;
            }
            cursor = cursor.next;
        }
        return false;
    }

    public boolean hasConnection(String from, String to) {
        Vertex a = findVertex(from);
        Vertex b = findVertex(to);
        return a != null && b != null && hasEdge(a, b);
    }

    /**
     * Removes the road between two locations, in both directions.
     * Result codes: 0 = removed, 1 = a location is missing, 2 = no such road.
     */
    public int removeConnection(String from, String to) {
        Vertex a = findVertex(from);
        Vertex b = findVertex(to);
        if (a == null || b == null) {
            return 1;
        }
        if (!hasEdge(a, b)) {
            return 2;
        }
        a.edges = unlinkEdge(a.edges, b);
        b.edges = unlinkEdge(b.edges, a);
        edgeCount--;
        return 0;
    }

    // -------------------------------------------------------------- display

    /** Prints the whole adjacency list, one line per location. */
    public String adjacencyListView() {
        if (head == null) {
            return "   (no campus locations recorded yet)\n";
        }
        StringBuilder sb = new StringBuilder();
        Vertex cursor = head;
        while (cursor != null) {
            sb.append(String.format("   %-18s -> ", cursor.name));
            if (cursor.edges == null) {
                sb.append("(no connections)");
            } else {
                Edge e = cursor.edges;
                while (e != null) {
                    sb.append(e.target.name).append(" [").append(e.distance).append("m]");
                    e = e.next;
                    if (e != null) {
                        sb.append(", ");
                    }
                }
            }
            sb.append('\n');
            cursor = cursor.next;
        }
        return sb.toString();
    }

    /** Square adjacency matrix view - the alternative representation. */
    public String adjacencyMatrixView() {
        if (head == null) {
            return "   (no campus locations recorded yet)\n";
        }
        String[] names = locationNames();
        StringBuilder sb = new StringBuilder();
        sb.append("        ");
        for (int i = 0; i < names.length; i++) {
            sb.append(String.format("%-6s", shortLabel(names[i], i)));
        }
        sb.append('\n');
        for (int r = 0; r < names.length; r++) {
            sb.append(String.format("   %-5s", shortLabel(names[r], r)));
            for (int c = 0; c < names.length; c++) {
                sb.append(String.format("%-6d", hasConnection(names[r], names[c]) ? 1 : 0));
            }
            sb.append("  ").append(names[r]).append('\n');
        }
        return sb.toString();
    }

    private String shortLabel(String name, int index) {
        return "L" + index;
    }

    /** Every location name, in insertion-independent list order. */
    public String[] locationNames() {
        String[] out = new String[vertexCount];
        Vertex cursor = head;
        int i = 0;
        while (cursor != null) {
            out[i++] = cursor.name;
            cursor = cursor.next;
        }
        return out;
    }

    /** Names of the locations directly reachable from the given one. */
    public String[] neighbours(String name) {
        Vertex v = findVertex(name);
        if (v == null) {
            return new String[0];
        }
        int count = 0;
        Edge cursor = v.edges;
        while (cursor != null) {
            count++;
            cursor = cursor.next;
        }
        String[] out = new String[count];
        cursor = v.edges;
        int i = 0;
        while (cursor != null) {
            out[i++] = cursor.target.name;
            cursor = cursor.next;
        }
        return out;
    }

    // ------------------------------------------------------------ traversal

    private void clearVisited() {
        Vertex cursor = head;
        while (cursor != null) {
            cursor.visited = false;
            cursor = cursor.next;
        }
    }

    /**
     * Breadth-First Search from a start location, using a hand-written queue.
     * Returns the visit order, or null when the start location does not exist.
     */
    public String[] bfs(String start) {
        Vertex origin = findVertex(start);
        if (origin == null) {
            return null;
        }
        clearVisited();

        String[] order = new String[vertexCount];
        int filled = 0;

        Vertex[] queue = new Vertex[vertexCount];
        int front = 0;
        int rear = 0;

        origin.visited = true;
        queue[rear++] = origin;

        while (front < rear) {
            Vertex current = queue[front++];
            order[filled++] = current.name;
            Edge e = current.edges;
            while (e != null) {
                if (!e.target.visited) {
                    e.target.visited = true;
                    queue[rear++] = e.target;
                }
                e = e.next;
            }
        }
        return trim(order, filled);
    }

    /**
     * Iterative Depth-First Search from a start location, using a hand-written
     * stack. Returns the visit order, or null when the start does not exist.
     */
    public String[] dfs(String start) {
        Vertex origin = findVertex(start);
        if (origin == null) {
            return null;
        }
        clearVisited();

        String[] order = new String[vertexCount];
        int filled = 0;

        // A vertex can be pushed once per incoming edge before it is marked
        // visited, so the stack is sized for the worst case, not for vertexCount.
        Vertex[] stack = new Vertex[vertexCount + 2 * edgeCount + 1];
        int top = 0;
        stack[top++] = origin;

        while (top > 0) {
            Vertex current = stack[--top];
            if (current.visited) {
                continue;
            }
            current.visited = true;
            order[filled++] = current.name;
            Edge e = current.edges;
            while (e != null) {
                if (!e.target.visited) {
                    stack[top++] = e.target;
                }
                e = e.next;
            }
        }
        return trim(order, filled);
    }

    /**
     * Shortest hop path between two locations, found with a BFS that records
     * each vertex's parent. Returns null when either end is unknown or the
     * two locations are not connected at all.
     */
    public String[] shortestPath(String from, String to) {
        Vertex source = findVertex(from);
        Vertex destination = findVertex(to);
        if (source == null || destination == null) {
            return null;
        }
        clearVisited();

        Vertex[] queue = new Vertex[vertexCount];
        Vertex[] order = new Vertex[vertexCount];
        Vertex[] parent = new Vertex[vertexCount];
        int front = 0;
        int rear = 0;

        source.visited = true;
        queue[rear] = source;
        order[rear] = source;
        parent[rear] = null;
        rear++;

        boolean found = source == destination;
        while (front < rear && !found) {
            Vertex current = queue[front++];
            Edge e = current.edges;
            while (e != null) {
                if (!e.target.visited) {
                    e.target.visited = true;
                    parent[rear] = current;
                    order[rear] = e.target;
                    queue[rear] = e.target;
                    rear++;
                    if (e.target == destination) {
                        found = true;
                        break;
                    }
                }
                e = e.next;
            }
        }
        if (!found) {
            return null;
        }

        // walk the parent chain backwards from the destination
        String[] reversed = new String[vertexCount];
        int count = 0;
        Vertex step = destination;
        while (step != null) {
            reversed[count++] = step.name;
            int idx = indexOf(order, rear, step);
            step = (idx < 0) ? null : parent[idx];
        }
        String[] path = new String[count];
        for (int i = 0; i < count; i++) {
            path[i] = reversed[count - 1 - i];
        }
        return path;
    }

    private int indexOf(Vertex[] arr, int length, Vertex target) {
        for (int i = 0; i < length; i++) {
            if (arr[i] == target) {
                return i;
            }
        }
        return -1;
    }

    private String[] trim(String[] source, int length) {
        String[] out = new String[length];
        System.arraycopy(source, 0, out, 0, length);
        return out;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public boolean isEmpty() {
        return head == null;
    }
}
// BFS uses a hand-written queue, DFS uses a hand-written stack - Member 4 (M.N.M Nafeel)
