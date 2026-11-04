package model;
import com.library.Util.DBConnection;
import dto.BorrowingHistoryDTO;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BorrowingHistoryModel {

    public List<BorrowingHistoryDTO> getAllHistory() {
        List<BorrowingHistoryDTO> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT ib.issue_id, ib.member_id, m.full_name, ib.isbn, b.title, ib.issue_date, ib.due_date, ib.return_date, ib.status " +
                    "FROM issued_books ib JOIN member m ON ib.member_id = m.member_id JOIN book b ON ib.isbn = b.isbn ORDER BY ib.issue_id DESC";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new BorrowingHistoryDTO(
                        rs.getInt("issue_id"),
                        rs.getString("member_id"),
                        rs.getString("full_name"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getDate("issue_date") != null ? rs.getDate("issue_date").toLocalDate() : null,
                        rs.getDate("due_date") != null ? rs.getDate("due_date").toLocalDate() : null,
                        rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                        rs.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}