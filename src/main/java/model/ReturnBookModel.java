package model;

import com.library.Util.DBConnection;
import dto.ReturnDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnBookModel {


    public static class BorrowedBookInfo {
        private int issueId;
        private String isbn;
        private String title;
        private String memberName;
        private String memberId;
        private LocalDate issueDate;
        private LocalDate dueDate;

        public BorrowedBookInfo(int issueId, String isbn, String title, String memberName) {
            this.issueId = issueId;
            this.isbn = isbn;
            this.title = title;
            this.memberName = memberName;
        }

        public int getIssueId() { return issueId; }
        public String getIsbn() { return isbn; }
        public String getTitle() { return title; }
        public String getMemberName() { return memberName; }
        public String getMemberId() { return memberId; }
        public LocalDate getIssueDate() { return issueDate; }
        public LocalDate getDueDate() { return dueDate; }
        public void setMemberId(String memberId) { this.memberId = memberId; }
        public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
        public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    }

    public List<BorrowedBookInfo> getAllBorrowedBooksForCombo() {
        List<BorrowedBookInfo> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT ib.issue_id, ib.isbn, b.title, m.full_name FROM issued_books ib " +
                            "JOIN book b ON ib.isbn = b.isbn JOIN member m ON ib.member_id = m.member_id WHERE ib.status = 'ISSUED'"
            );
            while (rs.next()) {
                list.add(new BorrowedBookInfo(rs.getInt("issue_id"), rs.getString("isbn"), rs.getString("title"), rs.getString("full_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public BorrowedBookInfo getBorrowedDetailsById(int issueId) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement pst = conn.prepareStatement(
                    "SELECT ib.issue_date, ib.due_date, m.member_id, m.full_name, b.isbn, b.title FROM issued_books ib " +
                            "JOIN member m ON ib.member_id = m.member_id JOIN book b ON ib.isbn = b.isbn WHERE ib.issue_id = ?"
            );
            pst.setInt(1, issueId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                BorrowedBookInfo info = new BorrowedBookInfo(issueId, rs.getString("isbn"), rs.getString("title"), rs.getString("full_name"));
                info.setMemberId(rs.getString("member_id"));
                info.setIssueDate(rs.getDate("issue_date").toLocalDate());
                info.setDueDate(rs.getDate("due_date").toLocalDate());
                return info;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean returnBook(ReturnDTO dto) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement pst = conn.prepareStatement("UPDATE issued_books SET return_date = ?, status = ? WHERE issue_id = ?");
            pst.setDate(1, java.sql.Date.valueOf(dto.getReturnDate()));
            pst.setString(2, dto.getStatus());
            pst.setInt(3, dto.getIssueId());
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean increaseBookQuantity(String isbn) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement pst = conn.prepareStatement("UPDATE book SET quantity = quantity + 1 WHERE isbn = ?");
            pst.setString(1, isbn);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}