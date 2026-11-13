package Controller.Login;

import dto.BookDTO;
import model.BookModel;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddBookController implements Initializable {

    @FXML
    private TextField txtBookId;

    @FXML
    private TextField txtTitle;

    @FXML
    private TextField txtAuthor;

    @FXML
    private ComboBox<String> cmbCategory;

    @FXML
    private TextField txtYear;

    @FXML
    private TextField txtQuantity;

    @FXML
    private Label lblMsg;

    private final BookModel bookModel = new BookModel();



    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cmbCategory.setItems(
                FXCollections.observableArrayList(
                        "Children",
                        "Novel",
                        "Educational",
                        "History",
                        "Science",
                        "Other"
                )
        );

        lblMsg.setText("");
    }



    @FXML
    private void handleAddBook() {

        System.out.println("ADD BOOK BUTTON CLICKED!");

        String isbn = txtBookId.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();

        String category = cmbCategory.getValue();

        String yearText = txtYear.getText().trim();
        String quantityText = txtQuantity.getText().trim();



        if (isbn.isEmpty()
                || title.isEmpty()
                || author.isEmpty()
                || category == null
                || yearText.isEmpty()
                || quantityText.isEmpty()) {

            showMessage(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Please fill all required fields!"
            );

            return;
        }



        int year;
        int quantity;

        try {

            year = Integer.parseInt(yearText);
            quantity = Integer.parseInt(quantityText);

            if (year <= 0 || year > 9999) {

                showMessage(
                        Alert.AlertType.WARNING,
                        "Invalid Year",
                        "Please enter a valid published year."
                );

                return;
            }

            if (quantity <= 0) {

                showMessage(
                        Alert.AlertType.WARNING,
                        "Invalid Quantity",
                        "Quantity must be greater than zero."
                );

                return;
            }

        } catch (NumberFormatException e) {

            showMessage(
                    Alert.AlertType.WARNING,
                    "Invalid Input",
                    "Year and Quantity must contain numbers only."
            );

            return;
        }



        BookDTO dto = new BookDTO(
                isbn,
                title,
                author,
                category,
                String.valueOf(year),
                quantity
        );



        try {

            boolean saved = bookModel.saveBook(dto);

            if (saved) {

                showMessage(
                        Alert.AlertType.INFORMATION,
                        "Success",
                        "Book added successfully!"
                );

                handleClear();

            } else {

                showMessage(
                        Alert.AlertType.WARNING,
                        "Save Failed",
                        "This Book ISBN already exists or could not be saved."
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "Unable to save book:\n" + e.getMessage()
            );
        }
    }



    @FXML
    private void handleClear() {

        txtBookId.clear();
        txtTitle.clear();
        txtAuthor.clear();
        txtYear.clear();
        txtQuantity.clear();

        cmbCategory.setValue(null);

        lblMsg.setText("");
    }



    private void showMessage(
            Alert.AlertType type,
            String title,
            String message
    ) {

        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

}