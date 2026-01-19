package client.scenes;

import client.services.LocaleManager;
import client.services.WebSocketService;
import com.google.inject.Inject;
import commons.Ingredient;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

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
    @Inject
    LocaleManager localeManager;
    @Inject
    WebSocketService webSocketService;
    @Inject
    MainApplicationCtrl mainCtrl;

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
    }

    public void setIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            titleLabel.setText("");
            return;
        }

        if (this.ingredient != null && !this.ingredient.getId().equals(ingredient.getId())) {
            webSocketService.unsubscribe("ingredient", this.ingredient.getId());
        }

        if (this.ingredient == null || !this.ingredient.getId().equals(ingredient.getId())) {
            webSocketService.subscribe("ingredient", ingredient.getId(), response -> {
                javafx.application.Platform.runLater(() -> {
                    if (response.type() == commons.WebSocketTypes.UPDATE) {
                        Ingredient updated = webSocketService.convertData(response.data(), Ingredient.class);
                        setIngredient(updated);
                    } else if (response.type() == commons.WebSocketTypes.DELETE) {
                        mainCtrl.showMainScreen();
                    }
                });
            });
        }

        this.ingredient = ingredient;
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
        editButton.textProperty().bind(editButtonProperty);
    }

    @Override
    public void setLocale(Locale locale) {
        var resourceBundle = ResourceBundle.getBundle(localeManager.getBundleName(), locale);
        proteinProperty.set(resourceBundle.getString("txt.protein") + ": ");
        fatProperty.set(resourceBundle.getString("txt.fat") + ": ");
        carbsProperty.set(resourceBundle.getString("txt.carbs") + ": ");
        nutritionalValueProperty.set(resourceBundle.getString("txt.nutritional_values") + " (100g)");
        editButtonProperty.set(resourceBundle.getString("txt.edit"));
    }

    public void clickEdit(ActionEvent _action) {
        if (ingredient != null && onIngredientEdit != null) {
            onIngredientEdit.accept(ingredient);

        }
    }

}
