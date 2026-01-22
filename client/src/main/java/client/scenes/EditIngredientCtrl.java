package client.scenes;

import client.services.LocaleManager;
import client.services.RecipeManager;
import client.services.WebSocketService;
import client.utils.TextFieldUtils;
import commons.Ingredient;
import commons.NutritionValues;
import commons.WebSocketTypes;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class EditIngredientCtrl implements Internationalizable {
    private final RecipeManager recipeManager;
    private final LocaleManager localeManager;
    private final WebSocketService webSocketService;
    private final MainApplicationCtrl mainCtrl;

    private final StringProperty nameProperty = new SimpleStringProperty();
    @FXML
    private Label nameLabel;

    private final StringProperty enterIngredientNameProperty = new SimpleStringProperty();
    @FXML
    private TextField nameField;

    private final StringProperty proteinProperty = new SimpleStringProperty();
    @FXML
    private Label proteinLabel;

    private final StringProperty gramsProteinProperty = new SimpleStringProperty();
    @FXML
    private TextField proteinField;

    private final StringProperty fatProperty = new SimpleStringProperty();
    @FXML
    private Label fatLabel;

    private final StringProperty gramsFatProperty = new SimpleStringProperty();
    @FXML
    private TextField fatField;

    private final StringProperty carbsProperty = new SimpleStringProperty();
    @FXML
    private Label carbsLabel;

    private final StringProperty gramsCarbsProperty = new SimpleStringProperty();
    @FXML
    private TextField carbsField;

    private final StringProperty doneProperty = new SimpleStringProperty();
    @FXML
    private Button doneButton;

    private final StringProperty cancelProperty = new SimpleStringProperty();
    @FXML
    private Button cancelButton;

    private final StringProperty emptyFieldProperty = new SimpleStringProperty();
    private final StringProperty positiveDoubleFieldProperty = new SimpleStringProperty();

    private Ingredient ingredient = new Ingredient("", new NutritionValues(0, 0, 0));

    private Consumer<Ingredient> onShowIngredient;

    public void setOnShowIngredient(Consumer<Ingredient> cb) {
        onShowIngredient = cb;
    }

    @Inject
    public EditIngredientCtrl(
            RecipeManager recipeManager,
            LocaleManager localeManager,
            WebSocketService webSocketService,
            MainApplicationCtrl mainCtrl) {
        this.recipeManager = recipeManager;
        this.localeManager = localeManager;
        this.webSocketService = webSocketService;
        this.mainCtrl = mainCtrl;
        localeManager.register(this);
    }

    @FXML
    private void initialize() {
        bindElementsProperties();
        setLocale(localeManager.getCurrentLocale());
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
        proteinLabel.textProperty().bind(proteinProperty);
        proteinField.promptTextProperty().bind(gramsProteinProperty);
        fatLabel.textProperty().bind(fatProperty);
        fatField.promptTextProperty().bind(gramsFatProperty);
        carbsLabel.textProperty().bind(carbsProperty);
        carbsField.promptTextProperty().bind(gramsCarbsProperty);
        doneButton.textProperty().bind(doneProperty);
        cancelButton.textProperty().bind(cancelProperty);
    }

    /**
     * Dynamically updates properties of UI elements to the language
     * of a corresponding locale
     * 
     * @param locale provided locale/language for UI elements
     */
    @Override
    public void setLocale(Locale locale) {
        var resourceBundle = ResourceBundle.getBundle(localeManager.getBundleName(), locale);
        nameProperty.set(resourceBundle.getString("txt.name"));
        enterIngredientNameProperty.set(resourceBundle.getString("txt.enter_ingredient_name"));
        proteinProperty.set(resourceBundle.getString("txt.protein"));
        gramsProteinProperty.set(resourceBundle.getString("txt.grams_of_protein"));
        fatProperty.set(resourceBundle.getString("txt.fat"));
        gramsFatProperty.set(resourceBundle.getString("txt.grams_of_fat"));
        carbsProperty.set(resourceBundle.getString("txt.carbs"));
        gramsCarbsProperty.set(resourceBundle.getString("txt.grams_of_carbohydrates"));
        doneProperty.set(resourceBundle.getString("txt.done"));
        cancelProperty.set(resourceBundle.getString("txt.cancel"));
        emptyFieldProperty.set(resourceBundle.getString("txt.empty_field_error"));
        positiveDoubleFieldProperty.set(resourceBundle.getString("txt.positive_double_field_error"));
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        nameField.setText(ingredient.getName());
        proteinField.setText(Double.toString(ingredient.nutritionValues.protein()));
        fatField.setText(Double.toString(ingredient.nutritionValues.fat()));
        carbsField.setText(Double.toString(ingredient.nutritionValues.carbs()));

        webSocketService.subscribe("ingredient", ingredient.getId(), response -> {
            if (response.type() == WebSocketTypes.DELETE) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ingredient Deleted");
                    alert.setHeaderText(null);
                    alert.setContentText("The ingredient you are editing was deleted by another user.");
                    alert.showAndWait();
                    mainCtrl.showMainScreen();
                });
            }
        });
    }

    public void clickCancel() {
        if (ingredient != null) {
            webSocketService.unsubscribe("ingredient", ingredient.getId());
        }
        if (onShowIngredient != null) {
            onShowIngredient.accept(ingredient);
        }
    }

    public void clickDone() {
        if (ingredient != null) {
            webSocketService.unsubscribe("ingredient", ingredient.getId());
        }
        ingredient.name = TextFieldUtils.getStringFromField(nameField, nameLabel, emptyFieldProperty);
        double protein = TextFieldUtils.getPositiveDoubleFromField(proteinField, proteinLabel,
                positiveDoubleFieldProperty);
        double fat = TextFieldUtils.getPositiveDoubleFromField(fatField, fatLabel, positiveDoubleFieldProperty);
        double carbs = TextFieldUtils.getPositiveDoubleFromField(carbsField, carbsLabel, positiveDoubleFieldProperty);
        ingredient.nutritionValues = new NutritionValues(protein, fat, carbs);
        recipeManager.setIngredient(ingredient);
        if (onShowIngredient != null) {
            onShowIngredient.accept(ingredient);
        }
    }
}
