package Controller.Login;

import dto.IssueDTO;
import model.IssueBookModel;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnBookController {

    @FXML
    private ComboBox<String> borrowedBookComboBox;

    @FXML
    private TextField memberField;

    @FXML
    private TextField bookField;

    @FXML
    private DatePicker borrowedDatePicker;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private DatePicker returnDatePicker;

    @FXML
    private Label statusLabel;

    @FXML
    private Label messageLabel;

    private final IssueBookModel issueBookModel =
            new IssueBookModel();

    private final Map<String, IssueDTO> issuedBookMap =
            new HashMap<>();


    // INITIALIZE
    @FXML
    public void initialize() {

        borrowedDatePicker.setDisable(true);
        dueDatePicker.setDisable(true);

        returnDatePicker.setValue(LocalDate.now());

        loadBorrowedBooks();

        borrowedBookComboBox.valueProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (newValue != null) {
                        showBookDetails(issuedBookMap.get(newValue));
                    }
                }
        );

        returnDatePicker.valueProperty().addListener(
                (observable, oldValue, newValue) -> updateStatus()
        );
    }


    // LOAD BORROWED BOOKS
    private void loadBorrowedBooks() {

        try {

            List<IssueDTO> list =
                    issueBookModel.getAllIssuedBooks();

            issuedBookMap.clear();

            for (IssueDTO dto : list) {

                if ("ISSUED".equalsIgnoreCase(dto.getStatus())) {

                    String display =
                            dto.getIssueId() + " - " +
                                    dto.getIsbn() + " - " +
                                    dto.getTitle();

                    issuedBookMap.put(display, dto);
                }
            }

            borrowedBookComboBox.setItems(
                    FXCollections.observableArrayList(
                            issuedBookMap.keySet()
                    )
            );

            if (issuedBookMap.isEmpty()) {
                messageLabel.setText("No issued books available!");
            } else {
                messageLabel.setText("");
            }

        } catch (Exception e) {

            e.printStackTrace();

            messageLabel.setText(
                    "Error loading books: " + e.getMessage()
            );
        }
    }


    // DISPLAY SELECTED BOOK
    private void showBookDetails(IssueDTO dto) {

        if (dto == null) return;

        memberField.setText(dto.getMemberId());

        bookField.setText(
                dto.getIsbn() + " - " + dto.getTitle()
        );

        borrowedDatePicker.setValue(dto.getIssueDate());

        dueDatePicker.setValue(dto.getDueDate());

        returnDatePicker.setValue(LocalDate.now());

        updateStatus();
    }


    // CHECK STATUS
    private void updateStatus() {

        LocalDate dueDate = dueDatePicker.getValue();

        LocalDate returnDate = returnDatePicker.getValue();

        LocalDate borrowedDate = borrowedDatePicker.getValue();

        if (dueDate == null || returnDate == null) {
            statusLabel.setText("Not Checked");
            return;
        }

        if (borrowedDate != null &&
                returnDate.isBefore(borrowedDate)) {

            statusLabel.setText("Invalid Return Date");

        } else if (returnDate.isAfter(dueDate)) {

            statusLabel.setText("RETURNED LATE");

        } else {

            statusLabel.setText("RETURNED ON TIME");
        }
    }


    // RETURN BOOK BUTTON
    @FXML
    private void handleReturnBook() {

        String selectedBook = borrowedBookComboBox.getValue();

        if (selectedBook == null) {
            messageLabel.setText("Please select a book!");
            return;
        }

        IssueDTO dto = issuedBookMap.get(selectedBook);

        if (dto == null) {
            messageLabel.setText("Book details not found!");
            return;
        }

        LocalDate returnDate = returnDatePicker.getValue();

        if (returnDate == null) {
            messageLabel.setText("Please select return date!");
            return;
        }

        if (returnDate.isBefore(dto.getIssueDate())) {
            messageLabel.setText(
                    "Return date cannot be before issue date!"
            );
            return;
        }

        try {

            boolean success = issueBookModel.returnBook(
                    dto.getIssueId(),
                    returnDate
            );

            if (success) {

                messageLabel.setText(
                        "Book returned successfully!"
                );

                loadBorrowedBooks();

                clearFields();

            } else {

                messageLabel.setText(
                        "Book return failed! Please check the book."
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            messageLabel.setText(
                    "Return Error: " + e.getMessage()
            );
        }
    }


    // CLEAR BUTTON
    @FXML
    private void handleClear() {

        clearFields();

        messageLabel.setText("");
    }


    private void clearFields() {

        borrowedBookComboBox.getSelectionModel().clearSelection();

        memberField.clear();

        bookField.clear();

        borrowedDatePicker.setValue(null);

        dueDatePicker.setValue(null);

        returnDatePicker.setValue(LocalDate.now());

        statusLabel.setText("Not Checked");
    }
}