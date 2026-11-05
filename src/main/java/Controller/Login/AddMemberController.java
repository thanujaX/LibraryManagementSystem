package Controller.Login;

import dto.MemberDTO;
import model.MemberModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddMemberController {

    @FXML private TextField memberIdField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressArea;
    @FXML private Label messageLabel;

    private final MemberModel memberModel = new MemberModel();

    @FXML
    private void handleRegisterMember() {
        try {
            String id = memberIdField.getText().trim();
            String name = fullNameField.getText().trim();

            if (name.isEmpty() || id.isEmpty()) {
                messageLabel.setText("ID and Name required!");
                return;
            }

            MemberDTO dto = new MemberDTO(
                    id,
                    name,
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    addressArea.getText().trim()
            );

            if (memberModel.saveMember(dto)) {
                new Alert(Alert.AlertType.INFORMATION, "Member Added Successfully!").show();
                handleClear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    private void handleClear() {
        memberIdField.clear();
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressArea.clear();
        messageLabel.setText("");
    }
}