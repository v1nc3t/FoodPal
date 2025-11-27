package client.scenes;

import client.utils.ServerUtils;
import commons.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

import static client.Main.BUNDLE_NAME;
import static client.Main.DEFAULT_LOCALE;

public class AddIngredientCtrl {

    private final StringProperty nameProperty = new SimpleStringProperty();
    @FXML private Label nameLabel;

    private final StringProperty enterIngredientNameProperty =  new SimpleStringProperty();
    @FXML private TextField nameField;

    private final StringProperty amountProperty = new SimpleStringProperty();
    @FXML private Label amountLabel;

    private final StringProperty amountIngredientProperty = new SimpleStringProperty();
    @FXML private TextField amountField;

    private final StringProperty proteinProperty = new SimpleStringProperty();
    @FXML private Label proteinLabel;

    private final StringProperty gramsProteinProperty = new SimpleStringProperty();
    @FXML private TextField proteinField;

    private final StringProperty fatProperty = new SimpleStringProperty();
    @FXML private Label fatLabel;

    private final StringProperty gramsFatProperty = new SimpleStringProperty();
    @FXML private TextField fatField;

    private final StringProperty carbsProperty = new SimpleStringProperty();
    @FXML private Label carbsLabel;

    private final StringProperty gramsCarbsProperty = new SimpleStringProperty();
    @FXML private TextField carbsField;

    private final StringProperty selectUnitProperty = new SimpleStringProperty();
    @FXML private ComboBox<String> unitComboBox;

    private final StringProperty doneProperty = new SimpleStringProperty();
    @FXML private Button doneButton;

    private final StringProperty cancelProperty = new SimpleStringProperty();
    @FXML private Button cancelButton;

    private final ServerUtils server;
    private final AddRecipeCtrl ctrl;

    @FXML
    private void initialize() {
        bindElementsProperties();

        /* For UI testing purposes, since we don't have a button
         for language selection just yet, change this line
         if you want to visualize language changes.
         Parameter choices:
         EN: DEFAULT_LOCALE
         DE: Locale.GERMAN
         NL: Locale.forLanguageTag("nl-NL")
        */
        setLocale(DEFAULT_LOCALE);

        for (Unit unit : Unit.values()) {
            unitComboBox.getItems().add(unit.name().toLowerCase());
        }

        // ignore case ?? for amount??
    }

    /**
     * Binds the elements of the UI to StringProperties,
     * allowing dynamic updates, i.e. instant propagation,
     * of the language of buttons, labels, etc.
     * Should only be called once when initializing the controller.
     */
    private void bindElementsProperties() {
        nameLabel.textProperty().bind(nameProperty);
        nameField.promptTextProperty().bind(enterIngredientNameProperty);
        amountLabel.textProperty().bind(amountProperty);
        amountField.promptTextProperty().bind(amountIngredientProperty);
        proteinLabel.textProperty().bind(proteinProperty);
        proteinField.promptTextProperty().bind(gramsProteinProperty);
        fatLabel.textProperty().bind(fatProperty);
        fatField.promptTextProperty().bind(gramsFatProperty);
        carbsLabel.textProperty().bind(carbsProperty);
        carbsField.promptTextProperty().bind(gramsCarbsProperty);
        unitComboBox.promptTextProperty().bind(selectUnitProperty);
        doneButton.textProperty().bind(doneProperty);
        cancelButton.textProperty().bind(cancelProperty);
    }

    /**
     * Dynamically updates properties of UI elements to the language
     * of a corresponding locale
     * @param locale provided locale/language for UI elements
     */
    private void setLocale(Locale locale) {
        var resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        nameProperty.set(resourceBundle.getString("txt.name"));
        enterIngredientNameProperty.set(resourceBundle.getString("txt.enter_ingredient_name"));
        amountProperty.set(resourceBundle.getString("txt.amount"));
        amountIngredientProperty.set(resourceBundle.getString("txt.amount_of_ingredient"));
        proteinProperty.set(resourceBundle.getString("txt.protein"));
        gramsProteinProperty.set(resourceBundle.getString("txt.grams_of_protein"));
        fatProperty.set(resourceBundle.getString("txt.fat"));
        gramsFatProperty.set(resourceBundle.getString("txt.grams_of_fat"));
        carbsProperty.set(resourceBundle.getString("txt.carbs"));
        gramsCarbsProperty.set(resourceBundle.getString("txt.grams_of_carbohydrates"));
        selectUnitProperty.set(resourceBundle.getString("txt.select_unit"));
        doneProperty.set(resourceBundle.getString("txt.done"));
        cancelProperty.set(resourceBundle.getString("txt.cancel"));
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
