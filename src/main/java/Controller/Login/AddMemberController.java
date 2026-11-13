package Controller.Login;

import dto.MemberDTO;
import model.MemberModel;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddMemberController {

    @FXML
    private TextField memberIdField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextArea addressArea;

    @FXML
    private Label messageLabel;

    private final MemberModel memberModel = new MemberModel();

    @FXML
    private void handleRegisterMember() {

        try {

            String id = memberIdField.getText().trim();
            String name = fullNameField.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {

                messageLabel.setText("ID and Name required!");

                new Alert(
                        Alert.AlertType.WARNING,
                        "ID and Name required!"
                ).show();

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

                messageLabel.setText("Member Added Successfully!");

                new Alert(
                        Alert.AlertType.INFORMATION,
                        "Member Added Successfully!"
                ).show();

                handleClear();

            } else {

                messageLabel.setText("Failed to add member!");

                new Alert(
                        Alert.AlertType.ERROR,
                        "Failed to add member!"
                ).show();
            }

        } catch (Exception e) {

            e.printStackTrace();

            messageLabel.setText(e.getMessage());
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