package com.library.controller;

import com.library.Util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class BorrowingHistoryController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<BorrowingRecord> historyTable;
    @FXML
    private TableColumn<BorrowingRecord, String> memberIdColumn;
    @FXML
    private TableColumn<BorrowingRecord, String> bookTitleColumn;
    @FXML
    private TableColumn<BorrowingRecord, LocalDate> issueDateColumn;
    @FXML
    private TableColumn<BorrowingRecord, LocalDate> dueDateColumn;
    @FXML
    private TableColumn<BorrowingRecord, LocalDate> returnDateColumn;
    @FXML
    private TableColumn<BorrowingRecord, String> statusColumn;
    @FXML
    private Label historyCountLabel;

    private ObservableList<BorrowingRecord> historyList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        issueDateColumn.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadFromDB();
    }

    private void loadFromDB() {
        historyList.clear();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT ib.member_id, b.title, ib.issue_date, ib.due_date, ib.return_date, ib.status FROM issued_books ib JOIN book b ON ib.isbn = b.isbn ORDER BY ib.issue_date DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String memberId = rs.getString("member_id");
                String bookTitle = rs.getString("title");
                LocalDate issueDate = rs.getDate("issue_date") != null ? rs.getDate("issue_date").toLocalDate() : null;
                LocalDate dueDate = rs.getDate("due_date") != null ? rs.getDate("due_date").toLocalDate() : null;
                LocalDate returnDate = rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null;
                String status = rs.getString("status");

                if (status == null || status.isEmpty()) {
                    if (returnDate != null) status = "Returned";
                    else if (dueDate != null && dueDate.isBefore(LocalDate.now())) status = "Overdue";
                    else status = "Borrowed";
                }

                historyList.add(new BorrowingRecord(memberId, bookTitle, issueDate, dueDate, returnDate, status));
            }
            historyTable.setItems(historyList);
            updateHistoryCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            historyTable.setItems(historyList);
            updateHistoryCount();
            return;
        }
        ObservableList<BorrowingRecord> searchResults = FXCollections.observableArrayList();
        for (BorrowingRecord record : historyList) {
            if (record.getMemberId().toLowerCase().contains(searchText)
                    || record.getBookTitle().toLowerCase().contains(searchText)
                    || record.getStatus().toLowerCase().contains(searchText)) {
                searchResults.add(record);
            }
        }
        historyTable.setItems(searchResults);
        historyCountLabel.setText("Records: " + searchResults.size());
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        historyTable.setItems(historyList);
        updateHistoryCount();
    }

    private void updateHistoryCount() {
        historyCountLabel.setText("Records: " + historyList.size());
    }

    public static class BorrowingRecord {
        private final String memberId;
        private final String bookTitle;
        private final LocalDate issueDate;
        private final LocalDate dueDate;
        private final LocalDate returnDate;
        private final String status;

        public BorrowingRecord(String memberId, String bookTitle, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, String status) {
            this.memberId = memberId;
            this.bookTitle = bookTitle;
            this.issueDate = issueDate;
            this.dueDate = dueDate;
            this.returnDate = returnDate;
            this.status = status;
        }
        public String getMemberId() { return memberId; }
        public String getBookTitle() { return bookTitle; }
        public LocalDate getIssueDate() { return issueDate; }
        public LocalDate getDueDate() { return dueDate; }
        public LocalDate getReturnDate() { return returnDate; }
        public String getStatus() { return status; }
    }
}