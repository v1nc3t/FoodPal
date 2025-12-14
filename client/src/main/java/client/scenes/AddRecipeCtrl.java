package client.scenes;

import client.MyFXML;
import client.utils.ServerUtils;

import client.utils.TextFieldUtils;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.*;

import static client.Main.BUNDLE_NAME;
import static client.Main.DEFAULT_LOCALE;
import client.services.RecipeManager;

public class AddRecipeCtrl {

    private final StringProperty nameProperty = new SimpleStringProperty();
    @FXML private Label nameLabel;

    private final StringProperty recipeNameFieldProperty = new SimpleStringProperty();
    @FXML private TextField nameField;

    private final StringProperty ingredientsProperty = new SimpleStringProperty();
    @FXML private Label ingredientsLabel;

    private final StringProperty selectIngredientProperty = new SimpleStringProperty();
    @FXML private ComboBox<Ingredient> ingredientsComboBox;

    @FXML private Button addIngredientButton;
    @FXML private ScrollPane ingredientsScrollPane;
    @FXML private VBox ingredientsList;

    private final StringProperty preparationProperty = new SimpleStringProperty();
    @FXML private Label preparationLabel;

    private final StringProperty addPreparationStepProperty = new SimpleStringProperty();
    @FXML private TextArea preparationField;

    @FXML private Button addPreparationButton;
    @FXML private ScrollPane preparationScrollPane;
    @FXML private VBox preparationList;

    private final StringProperty servingSizeProperty = new SimpleStringProperty();
    @FXML private Label servingSizeLabel;

    private final StringProperty portionsProperty = new SimpleStringProperty();
    @FXML private TextField servingSizeField;

    private final StringProperty doneProperty = new SimpleStringProperty();
    @FXML private Button doneButton;

    private final StringProperty cancelProperty = new SimpleStringProperty();
    @FXML private Button cancelButton;

    private Recipe editingRecipe;

    private ArrayList<RecipeIngredient> ingredients = new ArrayList<>();
    private final ServerUtils server;
    private final MainApplicationCtrl mainCtrl;
    private final MyFXML fxml;

    // callback so MainApplicationCtrl can be notified when a recipe is added
    private java.util.function.Consumer<Recipe> onRecipeAdded;
    public void setOnRecipeAdded(java.util.function.Consumer<Recipe> onRecipeAdded) {
        this.onRecipeAdded = onRecipeAdded;
    }

    @Inject
    public AddRecipeCtrl(ServerUtils server, MainApplicationCtrl mainCtrl, MyFXML fxml) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.fxml = fxml;
    }

    /**
     * Initializes UI functionality
     */
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

        /*
        // when user entered a prep step, clicking enter will add it to the list
        preparationField.setOnAction(e -> {
            if(!preparationField.getText().isBlank()) {
                addPreparationButton.fire();
            }
        });
         */
        

        preparationScrollPane.setFitToWidth(true);

        preparationList.setSpacing(5);

        ingredientsScrollPane.setFitToWidth(true);

        ingredientsList.setSpacing(5);
    }

    /**
     * Binds the elements of the UI to StringProperties,
     * allowing dynamic updates, i.e. instant propagation,
     * of the language of buttons, labels, etc.
     * Should only be called once when initializing the controller.
     */
    private void bindElementsProperties() {
        nameLabel.textProperty().bind(nameProperty);
        nameField.promptTextProperty().bind(recipeNameFieldProperty);
        ingredientsLabel.textProperty().bind(ingredientsProperty);
        ingredientsComboBox.promptTextProperty().bind(selectIngredientProperty);
        preparationLabel.textProperty().bind(preparationProperty);
        preparationField.promptTextProperty().bind(addPreparationStepProperty);
        servingSizeLabel.textProperty().bind(servingSizeProperty);
        servingSizeField.promptTextProperty().bind(portionsProperty);
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
        recipeNameFieldProperty.set(resourceBundle.getString("txt.recipe_name"));
        ingredientsProperty.set(resourceBundle.getString("txt.ingredients"));
        selectIngredientProperty.set(resourceBundle.getString("txt.select_ingredient"));
        preparationProperty.set(resourceBundle.getString("txt.preparation"));
        addPreparationStepProperty.set(resourceBundle.getString("txt.add_preparation_step"));
        servingSizeProperty.set(resourceBundle.getString("txt.serving_size"));
        portionsProperty.set(resourceBundle.getString("txt.portions"));
        doneProperty.set(resourceBundle.getString("txt.done"));
        cancelProperty.set(resourceBundle.getString("txt.cancel"));
    }

    /**
     * When cancel clicked no recipe is added
     * Main ctrl stop showing add recipe panel
     */
    public void clickCancel() {
        closeView();
    }

    /**
     * Try adding a recipe to server
     * Update the main app's recipe list with new recipe name
     * Add recipe no work then error
     * And after clear all fields and main app stops showing add recipe panel
     */
    public void clickDone() {
        Recipe r;
        try {
            r = getRecipe();
            RecipeManager mgr = RecipeManager.getInstance();

            if (editingRecipe == null) {
                mgr.addRecipeOptimistic(r);

                if (onRecipeAdded != null) onRecipeAdded.accept(r);

                try {
                    server.addRecipe(r); // current project signature is void;
                } catch (Exception e) {
                    // Server not available / failed: keep optimistic copy.
                }
            } else {
                if (!mgr.setRecipe(r)) {
                    System.out.println("Failed to add recipe " + r);
                }
            }

        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
        editingRecipe = null;
        mainCtrl.showRecipeViewer(r);
    }

    private void closeView() {
        Recipe justSaved = editingRecipe;
        clearFields();

        if (justSaved != null) {
            mainCtrl.editRecipe(justSaved);
        } else {
            mainCtrl.showMainScreen();
        }
    }

    /**
     * Clearing all fields
     */
    private void clearFields() {
        nameField.clear();
        servingSizeField.clear();
        preparationField.clear();
        ingredients = new ArrayList<>();

        ingredientsComboBox.getSelectionModel().clearSelection();
        ingredientsComboBox.getItems().clear();

        preparationList.getChildren().clear();
        editingRecipe = null;
    }

    /**
     * Get all inputs from fields
     * @return a new Recipe with user input
     */
    private Recipe getRecipe() {
        String name = TextFieldUtils.getStringFromField(nameField,nameLabel);
        List<String> preparations = getPreparations();
        int servingSize = TextFieldUtils.getIntFromField(servingSizeField,servingSizeLabel);
        if(editingRecipe == null){
            // new recipe
            return new Recipe(name, ingredients, preparations, servingSize);
        } else {
            // Editing one
            return new Recipe(editingRecipe.getId(), name, ingredients, preparations, servingSize);
        }
    }

    /**
     * Gets preparation steps of recipe from user
     * Go through the vertical box
     * From each horizontal box we extract the string from the textflow
     * @return list of string - steps
     */
    private List<String> getPreparations() {
        return preparationList.getChildren().stream()
                .map(b -> (HBox) b)
                .map(h -> (TextFlow) h.getChildren().getFirst())
                .map(tf -> ((Text) tf.getChildren().getFirst()).getText())
                .toList();
    }

    /**
     * When click on add button next to ingredient
     * Open pop up window for adding a new Ingredient
     */
    public void clickAddIngredient() {
        var bundle = ResourceBundle.getBundle(BUNDLE_NAME, DEFAULT_LOCALE);
        Pair<AddIngredientCtrl, Parent> addIngredientPair = fxml.load(AddIngredientCtrl.class,
                bundle,"client", "scenes", "AddIngredient.fxml");

        AddIngredientCtrl addIngredientCtrl = addIngredientPair.getKey();
        Parent addIngredientRoot = addIngredientPair.getValue();

        // waits for new ingredient to be made in popup
        addIngredientCtrl.setIngredientAddedCb(newIngredient -> {
            Platform.runLater(() -> {
                ingredients.add(newIngredient);
                refreshIngredientsList();
            });
        });
        var scene = new Scene(addIngredientRoot);
        scene.setOnKeyPressed(addIngredientCtrl::keyPressed);

        Stage addIngredientStage = new Stage();
        addIngredientStage.setTitle("Add Ingredient");
        addIngredientStage.initModality(Modality.APPLICATION_MODAL);
        addIngredientStage.setScene(scene);
        addIngredientStage.setResizable(false);
        addIngredientStage.showAndWait();
    }

    /**
     * When click on edit button next to ingredient
     * Open pop up window for editing the existing Ingredient
     */
    public void clickEditIngredient(RecipeIngredient recipeIngredient) {
        var bundle = ResourceBundle.getBundle(BUNDLE_NAME, DEFAULT_LOCALE);
        Pair<AddIngredientCtrl, Parent> addIngredientPair = fxml.load(AddIngredientCtrl.class,
                bundle,"client", "scenes", "AddIngredient.fxml");

        AddIngredientCtrl addIngredientCtrl = addIngredientPair.getKey();
        addIngredientCtrl.setIngredient(recipeIngredient);
        Parent addIngredientRoot = addIngredientPair.getValue();

        // waits for new ingredient to be made in popup
        addIngredientCtrl.setIngredientAddedCb(newIngredient -> {
            Platform.runLater(() -> {
                ingredients.set(ingredients.indexOf(recipeIngredient), newIngredient);
                refreshIngredientsList();
            });
        });
        var scene = new Scene(addIngredientRoot);
        scene.setOnKeyPressed(addIngredientCtrl::keyPressed);

        Stage addIngredientStage = new Stage();
        addIngredientStage.setTitle("Edit Ingredient");
        addIngredientStage.initModality(Modality.APPLICATION_MODAL);
        addIngredientStage.setScene(scene);
        addIngredientStage.setResizable(false);
        addIngredientStage.showAndWait();
    }

    /**
     * A new item which consist of the name of an ingredient and a delete button:
     * @param recipeIngredient user input of ingredient
     * @return a horizontal box with the ingredient name and delete button
     */
    private HBox createIngredientItem(RecipeIngredient recipeIngredient) {
        Ingredient ingredient = RecipeManager.getInstance().getIngredient(recipeIngredient);

        //TODO find newIngredient based on id

        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setMaxWidth(Double.MAX_VALUE);

        TextFlow textFlow = new TextFlow(new Text(ingredient.name + " | " + recipeIngredient.amount.toPrettyString()));

        textFlow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textFlow, Priority.ALWAYS);

        Button delete = new Button("-");
        Button edit =  new Button("Edit");

        HBox buttonGroup = new HBox(5, delete, edit);
        buttonGroup.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(buttonGroup, Priority.NEVER);

        item.getChildren().addAll(textFlow, buttonGroup);

        delete.setOnAction(e -> ingredientsList.getChildren().remove(item));
        edit.setOnAction(e -> clickEditIngredient(recipeIngredient));

        return item;
    }

    /**
     * When click on add next to preparation
     * The inputted text will be added to the list, if not empty
     * Field is cleared for next step
     */
    public void clickAddPreparation() {
        String step =  preparationField.getText().trim();
        if (step.isEmpty()) {
            return;
        }

        HBox newItem = createPreparationItem(step);
        preparationList.getChildren().add(newItem);

        preparationField.clear();
    }

    /**
     * A new item which consist of a text and three buttons:
     * move up, move down, remove
     * @param text user input of preparation step
     * @return a horizontal box with the text and three buttons
     */
    private HBox createPreparationItem(String text) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setMaxWidth(Double.MAX_VALUE);

        TextFlow textFlow = new TextFlow(new Text(text));
        textFlow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textFlow, Priority.ALWAYS);

        Button up = new Button("↑");
        Button down = new Button("↓");
        Button delete = new Button("-");

        HBox buttonGroup = new HBox(5, up, down, delete);
        buttonGroup.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(buttonGroup, Priority.NEVER);

        item.getChildren().addAll(textFlow, buttonGroup);

        up.setOnAction(e -> moveUp(item));
        down.setOnAction(e -> moveDown(item));
        delete.setOnAction(e -> preparationList.getChildren().remove(item));

        return item;
    }

    /** Remakes the `ingredientsList` from `ingredients` */
    public void refreshIngredientsList() {
        ingredientsList.getChildren().clear();
        for (RecipeIngredient ri : ingredients) {
            ingredientsList.getChildren().add(createIngredientItem(ri));
        }
    }

    /**
     * Loads an existing recipe into the Add Recipe form for editing.
     * @param recipe the recipe to edit
     */
    public void loadRecipe(Recipe recipe) {
        if (recipe == null) return;

        this.editingRecipe = recipe;

        nameField.setText(recipe.getTitle());
        servingSizeField.setText(String.valueOf(recipe.getServingSize()));

        //needs to be changed once server side is done.
        ingredients.clear();
        if (editingRecipe != null)
            ingredients.addAll(recipe.getIngredients());
        refreshIngredientsList();

        preparationList.getChildren().clear();
        for (String step : recipe.getSteps()) {
            HBox item = createPreparationItem(step);
            preparationList.getChildren().add(item);
        }
    }

    /**
     * Moves the item up the list
     * @param item preparation step
     */
    private void moveUp(HBox item) {
        int index = preparationList.getChildren().indexOf(item);
        if (index > 0) {
            preparationList.getChildren().remove(index);
            preparationList.getChildren().add(index - 1, item);
        }
    }

    /**
     * Moves the item down the list
     * @param item preparation step
     */
    private void moveDown(HBox item) {
        int index = preparationList.getChildren().indexOf(item);
        if (index < preparationList.getChildren().size() - 1) {
            preparationList.getChildren().remove(index);
            preparationList.getChildren().add(index + 1, item);
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
