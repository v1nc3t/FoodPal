package client.scenes;

import client.services.LocaleManager;
import client.services.RecipeManager;
import client.services.ShoppingListManager;
import com.google.inject.Inject;
import commons.Ingredient;
import client.services.RecipePrinter;
import commons.Recipe;
import commons.RecipeIngredient;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class RecipeViewerCtrl implements Internationalizable {

    private final StringProperty titleProperty = new SimpleStringProperty();
    @FXML
    private Label titleLabel;

    private final StringProperty languageProperty = new SimpleStringProperty();
    @FXML
    private Label languageLabel;

    private final StringProperty languageSuffixProperty = new SimpleStringProperty();
    @FXML
    private Label languageSuffixLabel;

    private final StringProperty portionsProperty = new SimpleStringProperty();
    @FXML
    private Label portionsLabel;

    private final StringProperty portionsValueProperty = new SimpleStringProperty();
    @FXML
    private Label portionsValueLabel;

    private final StringProperty ingredientsProperty = new SimpleStringProperty();
    @FXML
    private Label ingredientsLabel;

    @FXML
    private ListView<String> ingredientsList;

    private final StringProperty preparationProperty = new SimpleStringProperty();
    @FXML
    private Label preparationLabel;

    @FXML
    private ListView<String> preparationList;

    private final StringProperty editProperty = new SimpleStringProperty();
    @FXML
    private Button editButton;

    private final StringProperty printProperty = new SimpleStringProperty();
    @FXML
    private Button printButton;
    private final StringProperty addToShoppingListProperty = new SimpleStringProperty();
    @FXML
    private Button addToShoppingListButton;

    private Recipe currentRecipe;

    private final StringProperty caloriesProperty = new SimpleStringProperty();

    @FXML
    private Label caloriesLabel;

    private Consumer<Recipe> onRecipeEdit;
    private final LocaleManager localeManager;
    private final RecipeManager recipeManager;

    private final ShoppingListManager shoppingListManager;

    @Inject
    public RecipeViewerCtrl(LocaleManager localeManager,
                            RecipeManager recipeManager, ShoppingListManager shoppingListManager) {
        this.localeManager = localeManager;
        this.recipeManager = recipeManager;
        this.shoppingListManager = shoppingListManager;

        localeManager.register(this);
    }

    public void setOnRecipeEdit(Consumer<Recipe> cb) {
        onRecipeEdit = cb;
    }

    @FXML
    private void initialize() {
        bindElementsProperties();

        setLocale(localeManager.getCurrentLocale());
    }

    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    private void bindElementsProperties() {
        titleLabel.textProperty().bind(titleProperty);
        languageLabel.textProperty().bind(languageProperty);
        languageSuffixLabel.textProperty().bind(languageSuffixProperty);
        portionsLabel.textProperty().bind(portionsProperty);
        portionsValueLabel.textProperty().bind(portionsValueProperty);
        ingredientsLabel.textProperty().bind(ingredientsProperty);
        preparationLabel.textProperty().bind(preparationProperty);
        editButton.textProperty().bind(editProperty);
        printButton.textProperty().bind(printProperty);
        addToShoppingListButton.textProperty().bind(addToShoppingListProperty);
        caloriesLabel.textProperty().bind(caloriesProperty);
    }

    @Override
    public void setLocale(Locale locale) {
        var resourceBundle = ResourceBundle.getBundle(localeManager.getBundleName(), locale);
        if (currentRecipe != null) {
            languageSuffixProperty.set(currentRecipe.getLanguage().proper());
        }
        titleProperty.set(resourceBundle.getString("txt.recipe_name"));
        languageProperty.set(resourceBundle.getString("txt.recipe_language") + ": ");
        portionsProperty.set(resourceBundle.getString("txt.portions") + ": ");
        ingredientsProperty.set(resourceBundle.getString("txt.ingredients"));
        preparationProperty.set(resourceBundle.getString("txt.preparation"));
        editProperty.set(resourceBundle.getString("txt.edit"));
        printProperty.set(resourceBundle.getString("txt.print"));
        addToShoppingListProperty.set(resourceBundle.getString("txt.add_to_shopping_list"));
        caloriesProperty.set(resourceBundle.getString("txt.calories_per_portion"));

    }

    /**
     * This sets the values inside the `RecipeViewer`
     * 
     * @param recipe the recipe
     */
    public void setRecipe(Recipe recipe) {
        this.currentRecipe = recipe;

        if (recipe == null) {
            titleProperty.set("");
            ingredientsList.getItems().clear();
            preparationList.getItems().clear();
            return;
        }

        languageSuffixProperty.set(recipe.getLanguage().proper());
        portionsValueProperty.set(String.valueOf(recipe.getPortions()));
        titleProperty.set(recipe.getTitle());
        setIngredientsList(recipe);
        setPreparationList(recipe);
        double kcal = recipe.getCaloriesPerPortion(
                id -> recipeManager.getIngredient(new RecipeIngredient(id, null))
        );

        caloriesProperty.set(String.format("%.1f kcal", kcal));


    }

    /**
     * This sets the ingredients
     * 
     * @param recipe the recipe
     */
    private void setIngredientsList(Recipe recipe) {
        ingredientsList.getItems().clear();
        var ingredients = recipe.getIngredients();
        if (ingredients != null) {
            for (var recipeIngredient : ingredients) {
                Ingredient ingredient = recipeManager.getIngredient(recipeIngredient);
                ingredientsList.getItems().add(ingredient.getName() + " | " +
                        recipeIngredient.getAmount().toPrettyString());
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
        if (currentRecipe != null && onRecipeEdit != null) {
            onRecipeEdit.accept(currentRecipe);
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

    @FXML
    private void addToShoppingList() {
        if (currentRecipe != null) {
            shoppingListManager.addRecipe(currentRecipe);
        }
    }
}