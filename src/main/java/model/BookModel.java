package model;

import dto.BookDTO;
import com.library.Util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class BookModel {
    public static boolean saveBook(BookDTO bookDTO) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();

        String sql = "INSERT INTO book (isbn, title, author, category, published_year, quantity, created_at, updated_at) VALUES (?,?,?,?,?,?, NOW(), NOW())";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setString(1, bookDTO.getId());
        stm.setString(2, bookDTO.getTitle());
        stm.setString(3, bookDTO.getAuthor());
        stm.setString(4, bookDTO.getCategory());
        stm.setString(5, bookDTO.getPublishedYear());
        stm.setInt(6, bookDTO.getQuantity());
        return stm.executeUpdate() > 0;
    }
}