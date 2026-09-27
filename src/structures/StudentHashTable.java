package structures;

import model.Student;

/**
 * Hash table with separate chaining, giving O(1) average-case lookup of a
 * student by ID. The hash function, the buckets and the resizing are all
 * implemented here rather than delegated to java.util.HashMap.
 *
 * Responsibility: Member 3 - M.I.M Arshad (23DA2-0634)
 */
public class StudentHashTable {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR_LIMIT = 0.75;

    private static class Entry {
        String key;
        Student value;
        Entry next;          // chain pointer for collisions

        Entry(String key, Student value) {
            this.key = key;
            this.value = value;
        }
    }

    private Entry[] buckets;
    private int size;
    private int collisions;          // counted for the statistics display
    private long lastProbeCount;     // steps taken by the most recent search

    public StudentHashTable() {
        this.buckets = new Entry[DEFAULT_CAPACITY];
    }

    /**
     * Polynomial rolling hash (base 31) over the upper-cased ID, then folded
     * into the bucket range. Chosen because student IDs share long common
     * prefixes such as "23DA2-", so a simple length or sum hash would pile
     * every record into one bucket.
     */
    private int hash(String key, int capacity) {
        int h = 7;
        String normalised = key.toUpperCase();
        for (int i = 0; i < normalised.length(); i++) {
            h = 31 * h + normalised.charAt(i);
        }
        return Math.abs(h % capacity);
    }

    /** Inserts a record. Returns false when the ID is already stored. */
    public boolean put(Student student) {
        if (student == null) {
            return false;
        }
        String key = student.getStudentId();
        int index = hash(key, buckets.length);

        Entry cursor = buckets[index];
        while (cursor != null) {
            if (cursor.key.equalsIgnoreCase(key)) {
                return false;                     // duplicate ID rejected
            }
            cursor = cursor.next;
        }

        if (buckets[index] != null) {
            collisions++;                         // chain already occupied
        }
        Entry fresh = new Entry(key, student);
        fresh.next = buckets[index];
        buckets[index] = fresh;
        size++;

        if ((double) size / buckets.length > LOAD_FACTOR_LIMIT) {
            resize();
        }
        return true;
    }

    /** Average O(1) lookup. Records how many chain steps the search needed. */
    public Student get(String studentId) {
        if (studentId == null) {
            return null;
        }
        lastProbeCount = 0;
        int index = hash(studentId, buckets.length);
        Entry cursor = buckets[index];
        while (cursor != null) {
            lastProbeCount++;
            if (cursor.key.equalsIgnoreCase(studentId)) {
                return cursor.value;
            }
            cursor = cursor.next;
        }
        return null;
    }

    /** Removes a record by ID. Returns false when it is not present. */
    public boolean remove(String studentId) {
        if (studentId == null) {
            return false;
        }
        int index = hash(studentId, buckets.length);
        Entry cursor = buckets[index];
        Entry previous = null;
        while (cursor != null) {
            if (cursor.key.equalsIgnoreCase(studentId)) {
                if (previous == null) {
                    buckets[index] = cursor.next;
                } else {
                    previous.next = cursor.next;
                }
                size--;
                return true;
            }
            previous = cursor;
            cursor = cursor.next;
        }
        return false;
    }

    public boolean containsKey(String studentId) {
        return get(studentId) != null;
    }

    /** Doubles the bucket array and re-hashes every entry into the new range. */
    private void resize() {
        Entry[] old = buckets;
        Entry[] fresh = new Entry[old.length * 2];
        collisions = 0;
        for (Entry head : old) {
            Entry cursor = head;
            while (cursor != null) {
                Entry next = cursor.next;
                int index = hash(cursor.key, fresh.length);
                if (fresh[index] != null) {
                    collisions++;
                }
                cursor.next = fresh[index];
                fresh[index] = cursor;
                cursor = next;
            }
        }
        buckets = fresh;
    }

    /** Bucket-by-bucket report shown in the search menu, proving chaining works. */
    public String statistics() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("   Records stored : %d%n", size));
        sb.append(String.format("   Bucket count   : %d%n", buckets.length));
        sb.append(String.format("   Load factor    : %.2f%n", (double) size / buckets.length));
        sb.append(String.format("   Collisions     : %d%n", collisions));
        sb.append("   Non-empty buckets:\n");
        boolean any = false;
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] != null) {
                any = true;
                StringBuilder chain = new StringBuilder();
                Entry cursor = buckets[i];
                while (cursor != null) {
                    chain.append(cursor.key);
                    cursor = cursor.next;
                    if (cursor != null) {
                        chain.append(" -> ");
                    }
                }
                sb.append(String.format("     [%2d] %s%n", i, chain));
            }
        }
        if (!any) {
            sb.append("     (none)\n");
        }
        return sb.toString();
    }

    public long getLastProbeCount() {
        return lastProbeCount;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
