package dto;

import java.time.LocalDate;

public class IssueDTO {

    private int issueId;
    private String memberId;
    private String isbn;
    private String title;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;

    public IssueDTO() {
    }

    // Constructor for issuing a book
    public IssueDTO(String memberId, String isbn,
                    LocalDate issueDate,
                    LocalDate dueDate,
                    String status) {

        this.memberId = memberId;
        this.isbn = isbn;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    // Constructor without book title
    public IssueDTO(int issueId, String memberId,
                    String isbn, LocalDate issueDate,
                    LocalDate dueDate, LocalDate returnDate,
                    String status) {

        this.issueId = issueId;
        this.memberId = memberId;
        this.isbn = isbn;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Constructor with book title
    public IssueDTO(int issueId, String memberId,
                    String isbn, String title,
                    LocalDate issueDate,
                    LocalDate dueDate,
                    LocalDate returnDate,
                    String status) {

        this.issueId = issueId;
        this.memberId = memberId;
        this.isbn = isbn;
        this.title = title;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}