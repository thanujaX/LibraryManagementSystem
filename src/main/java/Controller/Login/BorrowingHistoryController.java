package com.library.controller;

import dto.BorrowingHistoryDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.BorrowingHistoryModel;
import java.time.LocalDate;

public class BorrowingHistoryController {

    @FXML private TextField searchField;
    @FXML private TableView<BorrowingHistoryDTO> historyTable;
    @FXML private TableColumn<BorrowingHistoryDTO, String> memberIdColumn;
    @FXML private TableColumn<BorrowingHistoryDTO, String> bookTitleColumn;
    @FXML private TableColumn<BorrowingHistoryDTO, LocalDate> issueDateColumn;
    @FXML private TableColumn<BorrowingHistoryDTO, LocalDate> dueDateColumn;
    @FXML private TableColumn<BorrowingHistoryDTO, LocalDate> returnDateColumn;
    @FXML private TableColumn<BorrowingHistoryDTO, String> statusColumn;
    @FXML private Label historyCountLabel;

    private ObservableList<BorrowingHistoryDTO> historyList = FXCollections.observableArrayList();
    private final BorrowingHistoryModel historyModel = new BorrowingHistoryModel();

    @FXML
    public void initialize() {
        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        issueDateColumn.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadFromModel();
    }

    private void loadFromModel() {
        historyList.clear();
        var data = historyModel.getAllHistory();
        for (var dto : data) {
            String status = dto.getStatus();
            if (status == null || status.isEmpty()) {
                if (dto.getReturnDate() != null) status = "Returned";
                else if (dto.getDueDate() != null && dto.getDueDate().isBefore(LocalDate.now())) status = "Overdue";
                else status = "Borrowed";
            }
            historyList.add(new BorrowingHistoryDTO(
                    dto.getIssueId(),
                    dto.getMemberId(),
                    dto.getMemberName(),
                    dto.getIsbn(),
                    dto.getBookTitle(),
                    dto.getIssueDate(),
                    dto.getDueDate(),
                    dto.getReturnDate(),
                    status
            ));
        }
        historyTable.setItems(historyList);
        updateHistoryCount();
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            historyTable.setItems(historyList);
            updateHistoryCount();
            return;
        }
        ObservableList<BorrowingHistoryDTO> searchResults = FXCollections.observableArrayList();
        for (var record : historyList) {
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
}