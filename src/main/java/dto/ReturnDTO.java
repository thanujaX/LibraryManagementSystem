package dto;
import java.time.LocalDate;

public class ReturnDTO {
    private int issueId;
    private LocalDate returnDate;
    private String status;

    public ReturnDTO(int issueId, LocalDate returnDate, String status) {
        this.issueId = issueId;
        this.returnDate = returnDate;
        this.status = status;
    }
    public int getIssueId() { return issueId; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getStatus() { return status; }
}