package client.scenes;

import client.utils.ServerUtils;
import commons.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
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

    /**
     * When cancel button clicked no ingredient is added
     * Add ingredient pop up is closed
     */
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

    private RecipeIngredient getRecipeIngredient() {
        NutritionValues newValues = new NutritionValues(
                getDoubleFromField(proteinField),
                getDoubleFromField(fatField),
                getDoubleFromField(carbsField)
        );

        Ingredient newIngredient = new Ingredient(getName(), newValues);

        Amount newAmount = getAmount();

        return new RecipeIngredient(newIngredient.getId(), newAmount);
    }


    private String getName() {
        return nameField.getText();
    }

    private Amount getAmount() {
        double quantity = getDoubleFromField(amountField);
        String input = unitComboBox.getEditor().getText().trim();

        if (input.isEmpty()) {
            return new InformalAmount("");
        }

        Unit unit = null;

        try {
            unit = Unit.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            // unit remains null
        }

        if (unit != null) {
            return new FormalAmount(quantity, unit);
        } else {
            return new InformalAmount(input);
        }
    }

    private double getDoubleFromField(TextField textField) {
        String text = textField.getText();

        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(textField + " must be an number, with or without decimal point");
            alert.showAndWait();

            textField.clear();

            return -1;
        }
    }

    /**
     * Certain key presses can do things
     * @param e specific key presses
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER -> clickDone();
            case ESCAPE -> clickCancel();
            default -> {}
        }
    }
}
