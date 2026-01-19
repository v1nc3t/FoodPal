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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

    @FXML
    private Button scaleUpButton;

    @FXML
    private Button scaleDownButton;

    private final StringProperty resetProperty = new SimpleStringProperty();
    @FXML
    private Button resetScaleButton;

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
    private final StringProperty caloriesLabelProperty = new SimpleStringProperty();


    private final StringProperty servingSizeProperty = new SimpleStringProperty();
    @FXML
    private Label servingSizeLabel;

    private Consumer<Recipe> onRecipeEdit;
    private final LocaleManager localeManager;
    private final RecipeManager recipeManager;

    private final ShoppingListManager shoppingListManager;
    private final MainApplicationCtrl mainCtrl;

    @Inject
    public RecipeViewerCtrl(MainApplicationCtrl mainCtrl, LocaleManager localeManager,
            RecipeManager recipeManager, ShoppingListManager shoppingListManager) {
        this.mainCtrl = mainCtrl;
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
        resetScaleButton.textProperty().bind(resetProperty);
        ingredientsLabel.textProperty().bind(ingredientsProperty);
        preparationLabel.textProperty().bind(preparationProperty);
        editButton.textProperty().bind(editProperty);
        printButton.textProperty().bind(printProperty);
        addToShoppingListButton.textProperty().bind(addToShoppingListProperty);
        servingSizeLabel.textProperty().bind(servingSizeProperty);
        caloriesLabel.textProperty().bind(
                caloriesLabelProperty.concat(" ").concat(caloriesProperty)
        );

    }

    @Override
    public void setLocale(Locale locale) {
        var resourceBundle = ResourceBundle.getBundle(localeManager.getBundleName(), locale);
        if (currentRecipe != null) {
            languageSuffixProperty.set(currentRecipe.getLanguage().proper());
        }
        titleProperty.set(resourceBundle.getString("txt.recipe_name"));
        languageProperty.set(resourceBundle.getString("txt.recipe_language") + ":");
        portionsProperty.set(resourceBundle.getString("txt.portions") + ":");
        resetProperty.set(resourceBundle.getString("txt.reset"));
        ingredientsProperty.set(resourceBundle.getString("txt.ingredients"));
        preparationProperty.set(resourceBundle.getString("txt.preparation"));
        editProperty.set(resourceBundle.getString("txt.edit"));
        printProperty.set(resourceBundle.getString("txt.print"));
        addToShoppingListProperty.set(resourceBundle.getString("txt.add_to_shopping_list"));
        servingSizeProperty.set(resourceBundle.getString("txt.serving_size") + ":");
        caloriesLabelProperty.set(resourceBundle.getString("txt.recipe_kcal_100g"));

    }

    /**
     * This sets the values inside the `RecipeViewer`
     * 
     * @param recipe the recipe
     */
    public void setRecipe(Recipe recipe) {
        if (recipe == null) {
            titleProperty.set("");
            ingredientsList.getItems().clear();
            preparationList.getItems().clear();
            return;
        }
        this.currentRecipe = recipe;

        languageSuffixProperty.set(recipe.getLanguage().proper());
        titleProperty.set(recipe.getTitle());
        setPreparationList(recipe);
        double kcalPer100g = recipe.getKcalPer100g(
                id -> recipeManager.getIngredient(new RecipeIngredient(id, null))
        );
        double kcalPerPortion = recipe.getCaloriesPerPortion(
                id -> recipeManager.getIngredient(new RecipeIngredient(id, null))
        );


        DecimalFormat df = new DecimalFormat("0.#",
                DecimalFormatSymbols.getInstance(Locale.ROOT));

        caloriesProperty.set(df.format(kcalPer100g) + " kcal / 100g");


        boolean scaled = recipeManager.isScaled(recipe.getId());

        if (scaled && calculateScaledPortions() < 1) {
            recipeManager.removeScaledRecipe(recipe.getId());
            scaled = false;
        }

        resetScaleButton.setDisable(!scaled);
        scaleDownButton.setDisable(calculateScaledPortions() < 2);

        double scaleFactor = 0;
        if (scaled) {
            portionsValueProperty.set("~" + calculateScaledPortions());
            scaleFactor = calculateScaledPortions() / (double) recipe.getPortions();
            setIngredientsList(recipe, scaleFactor);
        } else {
            portionsValueProperty.set(String.valueOf(calculateScaledPortions()));
            setIngredientsList(recipe);
        }
        int portions = calculateScaledPortions();

        DecimalFormat df1 = new DecimalFormat("0.#",
                DecimalFormatSymbols.getInstance(Locale.ROOT));

        servingSizeProperty.set(
                localeManager.getCurrentBundle().getString("txt.serving_size")
                        + ": "
                        + portions
                        + " ("
                        + df1.format(kcalPerPortion)
                        + " kcal / portion)"
        );

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
     * This sets the ingredients with a scaling factor.
     *
     * @param recipe      the recipe
     * @param scaleFactor the scaling factor
     */
    private void setIngredientsList(Recipe recipe, double scaleFactor) {
        ingredientsList.getItems().clear();
        var ingredients = recipe.getIngredients();
        if (ingredients != null) {
            for (var recipeIngredient : ingredients) {
                Ingredient ingredient = recipeManager.getIngredient(recipeIngredient);
                ingredientsList.getItems().add(ingredient.getName() + " | " +
                        recipeIngredient.getAmount().scale(scaleFactor).toNormalizedString());
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
            var items = shoppingListManager.getRecipeItems(currentRecipe);
            mainCtrl.showIngredientOverview(items);
        }
    }

    /**
     * Scales up the recipe by 1 portion
     */
    @FXML
    private void scaleUpRecipe() {
        if (recipeManager.isScaled(currentRecipe.getId())) {
            int newScaledPortions = recipeManager.getRecipeScale(currentRecipe.getId()) + 1;
            recipeManager.setScaledRecipe(currentRecipe.getId(), newScaledPortions);
        } else {
            recipeManager.setScaledRecipe(currentRecipe.getId(), 1);
        }

        setRecipe(currentRecipe);
    }

    /**
     * Scales down the recipe by 1 portion, if possible
     */
    @FXML
    private void scaleDownRecipe() {
        if (calculateScaledPortions() >= 2) {
            if (recipeManager.isScaled(currentRecipe.getId())) {
                int newScaledPortions = recipeManager.getRecipeScale(currentRecipe.getId()) - 1;
                recipeManager.setScaledRecipe(currentRecipe.getId(), newScaledPortions);
            } else {
                recipeManager.setScaledRecipe(currentRecipe.getId(), -1);
            }
        }

        setRecipe(currentRecipe);
    }

    /**
     * Resets the scaled portions of the current recipe
     */
    @FXML
    private void resetScale() {
        recipeManager.removeScaledRecipe(currentRecipe.getId());

        setRecipe(currentRecipe);
    }

    /**
     * Calculates the scaled portions of the current recipe
     *
     * @return scaled portions
     */
    private int calculateScaledPortions() {
        if (!recipeManager.isScaled(currentRecipe.getId()))
            return currentRecipe.getPortions();
        return currentRecipe.getPortions() + recipeManager.getRecipeScale(currentRecipe.getId());
    }

}