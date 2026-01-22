package client.scenes;

import client.services.LocaleManager;
import client.services.RecipeManager;
import client.services.RecipeTextFormatter;
import client.services.ShoppingListManager;
import client.services.TextFileExporter;
import client.services.WebSocketService;
import com.google.inject.Inject;
import commons.*;
import javafx.application.Platform;
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
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

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

    private final StringProperty nutritionalValueProperty = new SimpleStringProperty();
    private final StringProperty nutritionalValueKcalProperty = new SimpleStringProperty();
    private final StringProperty proteinProperty = new SimpleStringProperty();
    private final StringProperty proteinValueProperty = new SimpleStringProperty();
    private final StringProperty fatProperty = new SimpleStringProperty();
    private final StringProperty fatValueProperty = new SimpleStringProperty();
    private final StringProperty carbsProperty = new SimpleStringProperty();
    private final StringProperty carbsValueProperty = new SimpleStringProperty();
    @FXML
    private Label nutritionalValueLabel;

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

    private final StringProperty servingSizeLabelProperty = new SimpleStringProperty();
    private final StringProperty servingSizeValueProperty = new SimpleStringProperty();
    private Consumer<Recipe> onRecipeEdit;
    private final LocaleManager localeManager;
    private final RecipeManager recipeManager;

    private final ShoppingListManager shoppingListManager;
    private final MainApplicationCtrl mainCtrl;
    private final WebSocketService webSocketService;

    @Inject
    public RecipeViewerCtrl(MainApplicationCtrl mainCtrl, LocaleManager localeManager,
            RecipeManager recipeManager, ShoppingListManager shoppingListManager,
            WebSocketService webSocketService) {
        this.mainCtrl = mainCtrl;
        this.localeManager = localeManager;
        this.recipeManager = recipeManager;
        this.shoppingListManager = shoppingListManager;
        this.webSocketService = webSocketService;

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
        nutritionalValueLabel.textProperty().bind(nutritionalValueProperty.concat(" ")
                .concat(nutritionalValueKcalProperty)
                .concat(" kcal, ")
                .concat(proteinProperty)
                .concat(" ")
                .concat(proteinValueProperty)
                .concat(" g, ")
                .concat(carbsProperty)
                .concat(" ")
                .concat(carbsValueProperty)
                .concat(" g, ")
                .concat(fatProperty)
                .concat(" ")
                .concat(fatValueProperty)
                .concat(" g"));
        preparationLabel.textProperty().bind(preparationProperty);
        editButton.textProperty().bind(editProperty);
        printButton.textProperty().bind(printProperty);
        addToShoppingListButton.textProperty().bind(addToShoppingListProperty);
        servingSizeLabel.textProperty().bind(servingSizeLabelProperty.concat(" ")
                .concat(servingSizeValueProperty));
        caloriesLabel.textProperty().bind(caloriesLabelProperty.concat(" ")
                .concat(caloriesProperty));

    }

    @Override
    public void setLocale(Locale locale) {
        var resourceBundle = ResourceBundle.getBundle(localeManager.getBundleName(), locale);
        if (currentRecipe != null) {
            languageSuffixProperty.set(currentRecipe.getLanguage().proper());
            titleProperty.set(currentRecipe.getTitle());
        } else {
            languageSuffixProperty.set("");
            titleProperty.set(resourceBundle.getString("txt.recipe_name"));
        }
        languageProperty.set(resourceBundle.getString("txt.recipe_language") + ":");
        portionsProperty.set(resourceBundle.getString("txt.portions") + ":");
        resetProperty.set(resourceBundle.getString("txt.reset"));
        ingredientsProperty.set(resourceBundle.getString("txt.ingredients"));
        preparationProperty.set(resourceBundle.getString("txt.preparation"));
        editProperty.set(resourceBundle.getString("txt.edit"));
        printProperty.set(resourceBundle.getString("txt.print"));
        addToShoppingListProperty.set(resourceBundle.getString("txt.add_to_shopping_list"));
        servingSizeProperty.set(resourceBundle.getString("txt.serving_size") + ":");
        servingSizeLabelProperty.set(resourceBundle.getString("txt.serving_size") + ":");
        caloriesLabelProperty.set(resourceBundle.getString("txt.recipe_kcal_100g"));
        nutritionalValueProperty.set(resourceBundle.getString("txt.nutritional_values") + ":");
        proteinProperty.set(resourceBundle.getString("txt.protein").toLowerCase() + ":");
        fatProperty.set(resourceBundle.getString("txt.fat").toLowerCase() + ":");
        carbsProperty.set(resourceBundle.getString("txt.carbs").toLowerCase() + ":");
    }

    /**
     * This sets the values inside the `RecipeViewer`
     * 
     * @param recipe the recipe
     */
    public void setRecipe(Recipe recipe) {
        if (recipe == null) {
            titleProperty.set("");
            preparationList.getItems().clear();
            return;
        }

        if (currentRecipe != null && !currentRecipe.getId().equals(recipe.getId())) {
            webSocketService.unsubscribe("recipe", currentRecipe.getId());
        }

        if (currentRecipe == null || !currentRecipe.getId().equals(recipe.getId())) {
            webSocketService.subscribe("recipe", recipe.getId(), response -> {
                Platform.runLater(() -> {
                    if (response.type() == WebSocketTypes.UPDATE) {
                        Recipe updated = webSocketService.convertData(response.data(), Recipe.class);
                        setRecipe(updated);
                    } else if (response.type() == WebSocketTypes.DELETE) {
                        mainCtrl.showMainScreen();
                    }
                });
            });
        }

        this.currentRecipe = recipe;
        Function<UUID, Ingredient> ingredientFactory = recipeManager::getIngredient;
        DecimalFormat df = new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.ROOT));

        languageSuffixProperty.set(recipe.getLanguage().proper());
        titleProperty.set(recipe.getTitle());
        setPreparationList(recipe);

        double kcalPer100g = recipe.calcKcalPer100g(ingredientFactory);
        double kcalPerPortion = recipe.calcCaloriesPerPortion(ingredientFactory);
        double protein = recipe.calcTotalProtein(ingredientFactory);
        double carbs = recipe.calcTotalCarbs(ingredientFactory);
        double fat = recipe.calcTotalFat(ingredientFactory);

        caloriesProperty.set(df.format(kcalPer100g) + " kcal / 100g");
        servingSizeValueProperty.set(df.format(kcalPerPortion) + " kcal");

        boolean scaled = recipeManager.isScaled(recipe.getId());
        if (scaled && calculateScaledPortions() < 1) {
            recipeManager.removeScaledRecipe(recipe.getId());
            scaled = false;
        }

        resetScaleButton.setDisable(!scaled);
        scaleDownButton.setDisable(calculateScaledPortions() < 2);

        nutritionalValueKcalProperty.set(df.format(kcalPerPortion * calculateScaledPortions()));
        if (scaled) {
            portionsValueProperty.set("~" + calculateScaledPortions());
            double scaleFactor = calculateScaledPortions() / (double) recipe.getPortions();
            setIngredientsList(recipe, scaleFactor);
            proteinValueProperty.set(df.format(protein * scaleFactor));
            carbsValueProperty.set(df.format(carbs * scaleFactor));
            fatValueProperty.set(df.format(fat * scaleFactor));
        } else {
            portionsValueProperty.set(String.valueOf(calculateScaledPortions()));
            setIngredientsList(recipe);
            proteinValueProperty.set(df.format(protein));
            carbsValueProperty.set(df.format(carbs));
            fatValueProperty.set(df.format(fat));
        }
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

        String text = RecipeTextFormatter.toText(
                currentRecipe,
                id -> recipeManager
                        .getIngredient(new RecipeIngredient(id, null))
                        .getName(),
                ResourceBundle.getBundle(
                        localeManager.getBundleName(), localeManager.getCurrentLocale()));

        TextFileExporter.save(
                text,
                titleLabel.getScene().getWindow());
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