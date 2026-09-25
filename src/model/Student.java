package model;

/**
 * Represents a single university student record.
 *
 * Responsibility: Member 1 - M.H.M Ijas (23DA2-0675)
 */
public class Student {

    private final String studentId;   // Immutable key used by the AVL tree and hash table
    private String name;
    private String programme;
    private double marks;

    public Student(String studentId, String name, String programme, double marks) {
        this.studentId = studentId;
        this.name = name;
        this.programme = programme;
        this.marks = marks;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    /** Simple grade derivation, used when records are displayed. */
    public String getGrade() {
        if (marks >= 75) return "A";
        if (marks >= 65) return "B";
        if (marks >= 55) return "C";
        if (marks >= 45) return "D";
        return "F";
    }

    /** One row of the console table. */
    public String toRow() {
        return String.format("| %-12s | %-22s | %-18s | %6.2f | %-5s |",
                studentId, name, programme, marks, getGrade());
    }

    @Override
    public String toString() {
        return studentId + " - " + name + " (" + programme + ", " + marks + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Student)) return false;
        return studentId.equalsIgnoreCase(((Student) other).studentId);
    }

    @Override
    public int hashCode() {
        return studentId.toUpperCase().hashCode();
    }
}
