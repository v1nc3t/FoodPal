package client.scenes;

import client.MyFXML;
import client.utils.ServerUtils;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeCtrl {

    @FXML private Label nameLabel;
    @FXML private TextField nameField;

    @FXML private Label ingredientsLabel;
    @FXML private ComboBox<Ingredient> ingredientsComboBox;
    @FXML private Button addIngredientButton;
    @FXML private ScrollPane ingredientsScrollPane;
    @FXML private VBox ingredientsList;

    @FXML private Label preparationLabel;
    @FXML private TextField preparationField;
    @FXML private Button addPreparationButton;
    @FXML private ScrollPane preparationScrollPane;
    @FXML private VBox preparationList;

    @FXML private Label servingSizeLabel;
    @FXML private TextField servingSizeField;

    @FXML private Button doneButton;
    @FXML private Button cancelButton;

    private final ServerUtils server;
    private final MainApplicationCtrl mainCtrl;
    private final MyFXML fxml;

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
        // when user entered a prep step, clicking enter will add it to the list
        preparationField.setOnAction(e -> {
            if(!preparationField.getText().isBlank()) {
                addPreparationButton.fire();
            }
        });
    }

    /**
     * When clicked all fields are cleared
     * No recipe is added
     * Main app stops showing add recipe panel
     */
    public void clickCancel() {
        clearFields();
        mainCtrl.showMainScreen();
    }

    /**
     * Try adding a recipe to server
     * Update the main app's recipe list with new recipe name
     * Add recipe no work then error
     * And after clear all fields and main app stops showing add recipe panel
     */
    public void clickDone() {
        try {
            server.addRecipe(getRecipe());
            // update the main controller's recipe list with new name
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        clearFields();
        mainCtrl.showMainScreen();
    }

    /**
     * Clearing all fields
     */
    private void clearFields() {
        nameField.clear();
        servingSizeField.clear();
        preparationField.clear();

        ingredientsComboBox.getSelectionModel().clearSelection();
        ingredientsComboBox.getItems().clear();

        preparationList.getChildren().clear();
    }

    /**
     * Get all inputs from fields
     * @return a new Recipe with user input
     */
    private Recipe getRecipe() {
        return new Recipe(
                getName(),
                getIngredients(),
                getPreparations(),
                getServingSize()
        );
    }

    /**
     * Gets name from user
     * @return string of recipe name
     */
    private String getName() {
        return nameField.getCharacters().toString();
    }

    /**
     * Gets ingredients used in recipe from user
     * @return list of RecipeIngredient
     */
    private List<RecipeIngredient> getIngredients() {
        return new ArrayList<>(); // from input of user
    }

    /**
     * Gets preparation steps of recipe from user
     * Go through the vertical box and extract the text from each horiontal box
     * @return list of string - steps
     */
    private List<String> getPreparations() {
        return preparationList.getChildren().stream()
                .map(b -> (HBox) b)
                .map(h -> ((Label) h.getChildren().getFirst()).getText())
                .toList();
    }

    /**
     * Gets how many servings is the recipe from user
     * @return an int of serving size
     */
    private int getServingSize() {
        return Integer.parseInt(servingSizeField.getCharacters().toString());
    }

    /**
     * When click on add button next to ingredient
     * Open pop up window for adding a new Ingredient
     */
    public void clickAddIngredient() {
        Pair<AddIngredientCtrl, Parent> addIngredientPair = fxml.load(AddIngredientCtrl.class,
                "client", "scenes", "AddIngredient.fxml");

        AddIngredientCtrl addIngredientCtrl = addIngredientPair.getKey();
        Parent addIngredientRoot = addIngredientPair.getValue();

        Stage addIngredientStage = new Stage();
        addIngredientStage.setTitle("Add Ingredient");
        addIngredientStage.initModality(Modality.APPLICATION_MODAL);
        addIngredientStage.setScene(new Scene(addIngredientRoot));
        addIngredientStage.setResizable(false);

        addIngredientStage.showAndWait();

        // add ingredient to combobox
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
     * A new item wich consist of a text and three buttons:
     * move up, move down, remove
     * @param text user input of preparation step
     * @return a horizontal box with the text and three buttons
     */
    private HBox createPreparationItem(String text) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(text);
        label.setWrapText(true);

        label.prefWidthProperty().bind(item.widthProperty().subtract(80));

        Button up = new Button("↑");
        Button down = new Button("↓");
        Button delete = new Button("-");

        HBox buttonGroup = new HBox(5, up, down, delete);
        buttonGroup.setAlignment(Pos.CENTER_RIGHT);

        item.getChildren().addAll(label, buttonGroup);

        up.setOnAction(e -> moveUp(item));
        down.setOnAction(e -> moveDown(item));
        delete.setOnAction(e -> preparationList.getChildren().remove(item));

        return item;
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
