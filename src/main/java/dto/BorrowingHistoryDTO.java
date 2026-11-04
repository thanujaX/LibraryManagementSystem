package dto;
import java.time.LocalDate;

public class BorrowingHistoryDTO {
    private int issueId;
    private String memberId;
    private String memberName;
    private String isbn;
    private String bookTitle;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;

    public BorrowingHistoryDTO(int issueId, String memberId, String memberName, String isbn, String bookTitle, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, String status) {
        this.issueId = issueId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public int getIssueId() { return issueId; }
    public String getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public String getIsbn() { return isbn; }
    public String getBookTitle() { return bookTitle; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getStatus() { return status; }
}