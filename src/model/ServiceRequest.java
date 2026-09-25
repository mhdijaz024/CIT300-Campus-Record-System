package model;

/**
 * A student service request (transcript, ID card, letter, etc.) that waits
 * in the FIFO service queue until a counter officer processes it.
 *
 * Responsibility: Member 2 - Z. Isham (23DA2-0677)
 */
public class ServiceRequest {

    private static int counter = 1000;

    private final int ticketNo;
    private final String studentId;
    private final String studentName;
    private final String requestType;

    public ServiceRequest(String studentId, String studentName, String requestType) {
        this.ticketNo = ++counter;
        this.studentId = studentId;
        this.studentName = studentName;
        this.requestType = requestType;
    }

    public int getTicketNo() {
        return ticketNo;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getRequestType() {
        return requestType;
    }

    @Override
    public String toString() {
        return String.format("Ticket #%d | %s (%s) | %s",
                ticketNo, studentId, studentName, requestType);
    }
}
