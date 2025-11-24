package client.scenes;

import client.utils.ServerUtils;
import commons.Ingredient;
import commons.Unit;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddIngredientCtrl {

    @FXML private Label nameLabel;
    @FXML private TextField nameField;

    @FXML private Label amountLabel;
    @FXML private TextField amountField;

    @FXML private Label proteinLabel;
    @FXML private TextField proteinField;

    @FXML private Label fatLabel;
    @FXML private TextField fatField;

    @FXML private Label carbsLabel;
    @FXML private TextField carbsField;

    @FXML private ComboBox<String> unitComboBox;

    @FXML private Button doneButton;
    @FXML private Button cancelButton;

    private final ServerUtils server;
    private final AddRecipeCtrl ctrl;

    @FXML
    private void initialize() {
        for (Unit unit : Unit.values()) {
            unitComboBox.getItems().add(unit.name().toLowerCase());
        }

        // ignore case ?? for amount??
    }

    @Inject
    public AddIngredientCtrl(ServerUtils server, AddRecipeCtrl ctrl) {
        this.server = server;
        this.ctrl = ctrl;
    }

    public void clickCancel() {
        clearFields();

        Stage addIngredientStage = (Stage) cancelButton.getScene().getWindow();
        addIngredientStage.close();
    }

    public void clickDone() {
        try {
            // server.addIngredient(getIngredient());
            // update the combo box in add recipe ctrl
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        clearFields();

        Stage addIngredientStage = (Stage) cancelButton.getScene().getWindow();
        addIngredientStage.close();
    }

    private void clearFields() {
        nameField.clear();
        amountField.clear();
        proteinField.clear();
        fatField.clear();
        carbsField.clear();

        unitComboBox.getEditor().clear();
    }

    private Ingredient getIngredient() {
        // return new Ingredient();
        return null;
    }
}
