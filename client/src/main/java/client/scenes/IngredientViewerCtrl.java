package client.scenes;

import client.services.LocaleManager;
import com.google.inject.Inject;
import commons.Ingredient;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.util.Locale;
import java.util.ResourceBundle;

public class IngredientViewerCtrl implements Internationalizable {
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
    @Inject
    LocaleManager localeManager;

    @FXML
    private void initialize() {
        bindElementsProperties();

        setLocale(localeManager.getCurrentLocale());
        localeManager.register(this);
    }

    public void setIngredient(Ingredient ingredient) {
        titleLabel.setText(ingredient.getName());
        var nv = ingredient.getNutritionValues();
        proteinValue.setText(Double.toString(nv.protein()));
        fatValue.setText(Double.toString(nv.fat()));
        carbsValue.setText(Double.toString(nv.carbs()));
    }

    private void bindElementsProperties() {
        proteinLabel.textProperty().bind(proteinProperty);
        fatLabel.textProperty().bind(fatProperty);
        carbsLabel.textProperty().bind(carbsProperty);
        nutritionalValueLabel.textProperty().bind(nutritionalValueProperty);
    }

    @Override
    public void setLocale(Locale locale) {
        var resourceBundle = ResourceBundle.getBundle(localeManager.getBundleName(), locale);
        proteinProperty.set(resourceBundle.getString("txt.protein") + ": ");
        fatProperty.set(resourceBundle.getString("txt.fat") + ": ");
        carbsProperty.set(resourceBundle.getString("txt.carbs") + ": ");
        nutritionalValueProperty.set(resourceBundle.getString("txt.nutritional_values"));
    }
}
