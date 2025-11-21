package client.scenes;

import client.utils.ServerUtils;
import commons.Unit;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddIngredientCtrl {

    @FXML private Label nameLabel;
    @FXML private Label amountLabel;
    @FXML private Label proteinLabel;
    @FXML private Label fatLabel;
    @FXML private Label carbsLabel;

    @FXML private TextField nameField;
    @FXML private TextField amountField;
    @FXML private TextField proteinField;
    @FXML private TextField fatField;
    @FXML private TextField carbsField;

    @FXML private ComboBox<Unit> ingredientsComboBox;

    @FXML private Button doneButton;
    @FXML private Button cancelButton;

    private final ServerUtils server;
    // main controller attribute

    public AddIngredientCtrl(ServerUtils server) {
        this.server = server;
        // inject main controller
    }

}
