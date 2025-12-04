package client.scenes;

import client.services.RecipePrinter;
import commons.Recipe;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;


import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static client.Main.BUNDLE_NAME;
import static client.Main.DEFAULT_LOCALE;

public class RecipeViewerCtrl {

    @FXML private Label titleLabel;


    private final StringProperty ingredientsProperty = new SimpleStringProperty();
    @FXML private Label ingredientsLabel;

    @FXML private ListView<String> ingredientsList;

    private final StringProperty preparationProperty = new SimpleStringProperty();
    @FXML private Label preparationLabel;

    @FXML private ListView<String> preparationList;

    private final StringProperty editProperty = new SimpleStringProperty();
    @FXML private Button editButton;
    @FXML
    private Button printButton;

    private Recipe currentRecipe;

    private MainApplicationCtrl mainCtrl;

    /**
     * Called by MainApplicationCtrl after loading this FXML.
     * Allows the viewer to call back into the main controller.
     *
     * @param mainCtrl the main application controller
     */
    public void setMainCtrl(MainApplicationCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    @FXML
    private void initialize() {
        bindElementsProperties();

        setLocale(DEFAULT_LOCALE);
    }

    private void bindElementsProperties() {
        ingredientsLabel.textProperty().bind(ingredientsProperty);
        preparationLabel.textProperty().bind(preparationProperty);

    }

    private void setLocale(Locale locale) {
        var resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        ingredientsProperty.set(resourceBundle.getString("txt.ingredients"));
        preparationProperty.set(resourceBundle.getString("txt.preparation"));
    }

  /**
   * This sets the values inside the recipe viewer
   * @param recipe the recipe
   */
    public void setRecipe(Recipe recipe) {
        this.currentRecipe = recipe;

        if (recipe == null) {
            titleLabel.setText("");
            ingredientsList.getItems().clear();
            preparationList.getItems().clear();
            return;
        }

        titleLabel.setText(recipe.getTitle());
        setIngredientsList(recipe);
        setPreparationList(recipe);
    }

    /**
     * This sets the ingredients
     *
     * @param recipe the recipe
     */
    private void setIngredientsList(Recipe recipe) {
        ingredientsList.getItems().clear();
        List<?> ingredients = recipe.getIngredients();
        if (ingredients != null) {
            for (Object ingredient : ingredients) {
                ingredientsList.getItems().add(String.valueOf(ingredient));
            }
        }
    }

    /**
     * This sets the steps
     *
     * @param recipe the recipe
     */
    private void setPreparationList(Recipe recipe) {
        preparationList.getItems().clear();
        List<String> steps = recipe.getSteps();
        if (steps != null) {
            preparationList.getItems().addAll(steps);
        }
    }

    /**
     * Opens the AddRecipe panel pre-filled with the currently viewed recipe.
     */
    @FXML
    private void editRecipe() {
        if (mainCtrl != null && currentRecipe != null) {
            mainCtrl.editRecipe(currentRecipe);
        }
    }

    /**
     * Opens print dialog and prints the current recipe
     */
    @FXML
    private void printRecipe() {
        if (currentRecipe == null) {
            return;
        }

        RecipePrinter.printRecipe(currentRecipe, titleLabel.getScene().getWindow());
    }
}