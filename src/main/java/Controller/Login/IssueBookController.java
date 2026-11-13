package Controller.Login;

import Util.HibernateUtil;
import dto.IssueDTO;
import entity.Book;
import entity.Member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import model.IssueBookModel;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueBookController {

    @FXML private ComboBox<String> memberComboBox;
    @FXML private ComboBox<String> bookComboBox;
    @FXML private DatePicker issueDatePicker;
    @FXML private DatePicker dueDatePicker;
    @FXML private Label messageLabel;

    private Map<String, String> memberMap = new HashMap<>();
    private Map<String, String> bookMap = new HashMap<>();

    private final IssueBookModel issueBookModel = new IssueBookModel();

    @FXML
    public void initialize() {
        issueDatePicker.setValue(LocalDate.now());
        dueDatePicker.setValue(LocalDate.now().plusDays(14));
        loadMembers();
        loadBooks();
    }

    private void loadMembers() {
        ObservableList<String> list = FXCollections.observableArrayList();
        memberMap.clear();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Member> members = session.createQuery("FROM Member", Member.class).list();
            for (Member m : members) {
                String display = m.getMemberId() + " - " + m.getFullName();
                list.add(display);
                memberMap.put(display, m.getMemberId());
            }
            memberComboBox.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading members: " + e.getMessage());
        }
    }

    private void loadBooks() {
        ObservableList<String> list = FXCollections.observableArrayList();
        bookMap.clear();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Book> books = session.createQuery("FROM Book WHERE availableQuantity > 0", Book.class).list();
            for (Book b : books) {
                String display = b.getId() + " - " + b.getTitle();
                list.add(display);
                bookMap.put(display, b.getId());
            }
            bookComboBox.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading books: " + e.getMessage());
        }
    }

    @FXML
    private void handleIssueBook() {
        String memberDisplay = memberComboBox.getValue();
        String bookDisplay = bookComboBox.getValue();
        LocalDate issueDate = issueDatePicker.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        if (memberDisplay == null || bookDisplay == null || issueDate == null || dueDate == null) {
            messageLabel.setText("Please fill all fields!");
            return;
        }
        if (dueDate.isBefore(issueDate)) {
            messageLabel.setText("Due date must be after issue date!");
            return;
        }

        try {
            String memberId = memberMap.get(memberDisplay);
            String isbn = bookMap.get(bookDisplay);
            IssueDTO dto = new IssueDTO(memberId, isbn, issueDate, dueDate, "BORROWED");
            boolean isIssued = issueBookModel.issueBook(dto);
            if (isIssued) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Book Issued Successfully!");
                alert.setContentText("Member: " + memberDisplay + "\nBook: " + bookDisplay);
                alert.showAndWait();
                handleClear();
            } else {
                messageLabel.setText("Issue Failed! Book not available or already borrowed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        memberComboBox.setValue(null);
        bookComboBox.setValue(null);
        issueDatePicker.setValue(LocalDate.now());
        dueDatePicker.setValue(LocalDate.now().plusDays(14));
        messageLabel.setText("");
        loadBooks();
    }
}