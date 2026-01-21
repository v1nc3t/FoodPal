package client.scenes;

import client.services.LocaleManager;
import client.services.RecipeManager;
import com.google.inject.Inject;
import commons.Ingredient;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class IngredientViewerCtrl implements Internationalizable {
    @FXML
    private Button editButton;
    private final StringProperty editButtonProperty = new SimpleStringProperty();
    @FXML
    private Label nutritionalValueLabel;
    private final StringProperty nutritionalValueProperty = new SimpleStringProperty();
    @FXML
    private Text proteinValue;
    @FXML
    private Text fatValue;
    @FXML
    private Text carbsValue;
    @FXML
    private Label titleLabel;
    @FXML
    private Label proteinLabel;
    private final StringProperty proteinProperty = new SimpleStringProperty();
    @FXML
    private Label fatLabel;
    private final StringProperty fatProperty = new SimpleStringProperty();
    @FXML
    private Label carbsLabel;
    private final StringProperty carbsProperty = new SimpleStringProperty();
    @FXML
    public Label kcalEstimateLabel;
    private final StringProperty kcalEstimateProperty = new SimpleStringProperty();
    @FXML
    public Text kcalEstimateValue;
    @FXML
    public Label usedInRecipesLabel;
    private final StringProperty usedInRecipesProperty = new SimpleStringProperty();
    @FXML
    public Text usedInRecipesValue;
    @Inject
    LocaleManager localeManager;
    @Inject
    RecipeManager recipeManager;

    private Consumer<Ingredient> onIngredientEdit;
    public void setOnIngredientEdit(Consumer<Ingredient> cb) {
        onIngredientEdit = cb;
    }

    private Ingredient ingredient;

    @FXML
    private void initialize() {
        bindElementsProperties();

        setLocale(localeManager.getCurrentLocale());
        localeManager.register(this);
        updateEstimatedKcal();
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        titleLabel.setText(ingredient.getName());
        var nv = ingredient.getNutritionValues();
        proteinValue.setText(Double.toString(nv.protein()));
        fatValue.setText(Double.toString(nv.fat()));
        carbsValue.setText(Double.toString(nv.carbs()));
        updateEstimatedKcal();
        setLocale(localeManager.getCurrentLocale());
    }

    private void bindElementsProperties() {
        proteinLabel.textProperty().bind(proteinProperty);
        fatLabel.textProperty().bind(fatProperty);
        carbsLabel.textProperty().bind(carbsProperty);
        nutritionalValueLabel.textProperty().bind(nutritionalValueProperty);
        editButton.textProperty().bind(editButtonProperty);
        kcalEstimateLabel.textProperty().bind(kcalEstimateProperty);
        usedInRecipesLabel.textProperty().bind(usedInRecipesProperty);
    }

    @Override
    public void setLocale(Locale locale) {
        var resourceBundle = ResourceBundle.getBundle(localeManager.getBundleName(), locale);
        proteinProperty.set(resourceBundle.getString("txt.protein") + ": ");
        fatProperty.set(resourceBundle.getString("txt.fat") + ": ");
        carbsProperty.set(resourceBundle.getString("txt.carbs") + ": ");
        nutritionalValueProperty.set(resourceBundle.getString("txt.nutritional_values") + " (100g)");
        editButtonProperty.set(resourceBundle.getString("txt.edit"));
        kcalEstimateProperty.set(resourceBundle.getString("txt.calories_per_100g_estimate") + ":");
        usedInRecipesProperty.set(resourceBundle.getString("txt.ingredient_used_in") + ":");
        if (ingredient != null)
            usedInRecipesValue.setText(
                    recipeManager.ingredientUsedIn(ingredient.id) + " " + resourceBundle.getString("txt.recipes")
            );
    }

    void updateEstimatedKcal() {
        if (ingredient == null) return;
        var calories = ingredient.getNutritionValues().calcKcalPer100g();
        DecimalFormat formatter = new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.ROOT));
        kcalEstimateValue.setText(formatter.format(calories) + " kcal");
    }


    public void clickEdit(ActionEvent _action) {
        if (ingredient != null && onIngredientEdit != null) {
            onIngredientEdit.accept(ingredient);
        }
    }

}
