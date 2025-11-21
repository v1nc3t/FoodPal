package client.scenes;

import client.utils.ServerUtils;
import commons.Ingredient;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddRecipeCtrl {

    @FXML private Label nameLabel;
    @FXML private Label ingredientsLabel;
    @FXML private Label preparationLabel;
    @FXML private Label servingSizeLabel;

    @FXML private TextField nameField;
    @FXML private ComboBox<Ingredient> ingredientsComboBox;
    @FXML private TextField preparationField;
    @FXML private TextField servingSizeField;

    @FXML private Button doneButton;
    @FXML private Button cancelButton;

    private final ServerUtils server;
    // main controller attribute

    @Inject
    public AddRecipeCtrl(ServerUtils server) {
        this.server = server;
        // inject main controller
    }
}
