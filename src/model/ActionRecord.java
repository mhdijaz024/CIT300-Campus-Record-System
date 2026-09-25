package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * One entry of the action history that is pushed onto the undo stack.
 *
 * The record keeps enough information to reverse the operation:
 *   ADD    -> snapshot of the student that was added   (undo = delete it)
 *   DELETE -> snapshot of the student that was removed (undo = add it back)
 *   UPDATE -> snapshot of the student BEFORE the change (undo = restore it)
 *
 * Responsibility: Member 2 - Z. Isham (23DA2-0677)
 */
public class ActionRecord {

    public enum Type { ADD, UPDATE, DELETE }

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Type type;
    private final String description;
    private final Student snapshot;      // may be null for non-undoable log entries
    private final LocalDateTime time;

    public ActionRecord(Type type, String description, Student snapshot) {
        this.type = type;
        this.description = description;
        this.snapshot = snapshot;
        this.time = LocalDateTime.now();
    }

    public Type getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Student getSnapshot() {
        return snapshot;
    }

    public boolean isUndoable() {
        return snapshot != null;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-6s : %s", time.format(FMT), type, description);
    }
}
