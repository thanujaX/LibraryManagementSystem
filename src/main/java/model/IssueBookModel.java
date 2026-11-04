package model;

import com.library.Util.DBConnection;
import dto.IssueDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class IssueBookModel {

    public boolean issueBook(IssueDTO dto) throws Exception {
        Connection conn = DBConnection.getInstance().getConnection();
        String sql = "INSERT INTO issued_books (member_id, isbn, issue_date, due_date, status) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, dto.getMemberId());
        pst.setString(2, dto.getIsbn());
        pst.setDate(3, Date.valueOf(dto.getIssueDate()));
        pst.setDate(4, Date.valueOf(dto.getDueDate()));
        pst.setString(5, "ISSUED");
        return pst.executeUpdate() > 0;
    }

    public List<IssueDTO> getAllIssuedBooks() throws Exception {
        List<IssueDTO> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
        String sql = "SELECT ib.issue_id, ib.member_id, ib.isbn, b.title, ib.issue_date, ib.due_date, ib.return_date, ib.status FROM issued_books ib JOIN books b ON ib.isbn = b.isbn";
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        while (rs.next()) {
            list.add(new IssueDTO(
                    rs.getInt("issue_id"),
                    rs.getString("member_id"),
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getDate("issue_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate(),
                    rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                    rs.getString("status")
            ));
        }
        return list;
    }

    public boolean returnBook(int issueId) throws Exception {
        Connection conn = DBConnection.getInstance().getConnection();
        String sql = "UPDATE issued_books SET return_date = CURDATE(), status = 'RETURNED ON TIME' WHERE issue_id = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, issueId);
        return pst.executeUpdate() > 0;
    }
}