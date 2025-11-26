package client.scenes;

import client.utils.ServerUtils;
import client.utils.TextFieldUtils;
import commons.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

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
    private Consumer<RecipeIngredient> ingredientAdded;

    @FXML
    private void initialize() {
        for (Unit unit : Unit.values()) {
            unitComboBox.getItems().add(unit.name().toLowerCase());
        }
    }

    @Inject
    public AddIngredientCtrl(ServerUtils server, AddRecipeCtrl ctrl) {
        this.server = server;
        this.ctrl = ctrl;
    }

    /**
     * Setting the callback
     * @param callback new ingredient made will be sent
     */
    public void setIngredientAdded(Consumer<RecipeIngredient> callback) {
        this.ingredientAdded = callback;
    }

    /**
     * When cancel button clicked no ingredient is added
     * Add ingredient pop up is closed
     */
    public void clickCancel() {
        closeWindow();
    }

    /**
     * Tries to create a new RecipeIngredient from user input
     * new ingredient is sent to server ///TODO
     * @throws WebApplicationException then show alert and stop
     */
    public void clickDone() {
        RecipeIngredient newIngredient = null;
        try {
            newIngredient = getRecipeIngredient();
            // server.addIngredient(getIngredient());
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        if(ingredientAdded != null && newIngredient != null) {
            ingredientAdded.accept(newIngredient);
        }

        closeWindow();
    }

    /**
     * Clears all fields and closes the popup
     */
    private void closeWindow() {
        clearFields();

        Stage addIngredientStage = (Stage) cancelButton.getScene().getWindow();
        addIngredientStage.close();
    }

    /**
     * Clears all fields
     */
    private void clearFields() {
        nameField.clear();
        amountField.clear();
        proteinField.clear();
        fatField.clear();
        carbsField.clear();

        unitComboBox.getEditor().clear();
    }

    /**
     * Creates a new RecipeIngredient with user input
     */
    private RecipeIngredient getRecipeIngredient() {
        String name = TextFieldUtils.getStringFromField(nameField, nameLabel);
        Amount newAmount = getAmount();
        double protein = TextFieldUtils.getDoubleFromField(proteinField, proteinLabel);
        double fat = TextFieldUtils.getDoubleFromField(fatField, fatLabel);
        double carbs = TextFieldUtils.getDoubleFromField(carbsField, carbsLabel);

        NutritionValues newValues = new NutritionValues(protein, fat, carbs);

        // put the new ingredient in the database
        Ingredient newIngredient = new Ingredient(name, newValues);


        return new RecipeIngredient(newIngredient.getId(), newAmount);
    }

    /**
     * Extracts the unit of measuring from the selecting field
     * @return a new Amount based on input
     */
    private Amount getAmount() {
        double quantity = TextFieldUtils.getDoubleFromField(amountField, amountLabel);
        String input = unitComboBox.getEditor().getText().trim();

        if (input.isEmpty()) {
            return null;
            //return new InformalAmount(quantity, "");
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
            return null;
            // return new InformalAmount(quantity, input);
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
