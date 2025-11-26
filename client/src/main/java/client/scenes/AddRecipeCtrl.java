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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        preparationScrollPane.setFitToWidth(true);

        preparationList.setSpacing(5);

        ingredientsScrollPane.setFitToWidth(true);

        ingredientsList.setSpacing(5);
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
        try {
            server.addRecipe(getRecipe());
            //TODO update the main controller's recipe list
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        closeView();
    }

    private void closeView() {
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
        String name = TextFieldUtils.getStringFromField(nameField,nameLabel);
        List <RecipeIngredient> ingredients = new ArrayList<>();
        List<String> preparations = getPreparations();
        int servingSize = TextFieldUtils.getIntFromField(servingSizeField,servingSizeLabel);

        return new Recipe(name, ingredients, preparations, servingSize);
    }

    /**
     * Gets ingredients used in recipe from user
     * @return list of RecipeIngredient
     */
    private List<RecipeIngredient> getIngredients() {
        return new ArrayList<>(); //TODO get ingredients from list
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
        Pair<AddIngredientCtrl, Parent> addIngredientPair = fxml.load(AddIngredientCtrl.class,
                "client", "scenes", "AddIngredient.fxml");

        AddIngredientCtrl addIngredientCtrl = addIngredientPair.getKey();
        Parent addIngredientRoot = addIngredientPair.getValue();

        // waits for new ingredient to be made in popup
        addIngredientCtrl.setIngredientAdded(newIngredient -> {
            Platform.runLater(() -> {
                ingredientsList.getChildren().add(createIngredientItem(newIngredient));
            });
        });

        Stage addIngredientStage = new Stage();
        addIngredientStage.setTitle("Add Ingredient");
        addIngredientStage.initModality(Modality.APPLICATION_MODAL);
        addIngredientStage.setScene(new Scene(addIngredientRoot));
        addIngredientStage.setResizable(false);
        addIngredientStage.showAndWait();
    }

    /**
     * A new item which consist of the name of an ingredient and a delete button:
     * @param newRecipeIngredient user input of ingredient
     * @return a horizontal box with the ingredient name and delete button
     */
    private HBox createIngredientItem(RecipeIngredient newRecipeIngredient) {
        UUID id = newRecipeIngredient.getIngredientRef();

        //TODO find newIngredient based on id

        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setMaxWidth(Double.MAX_VALUE);

        TextFlow textFlow = new TextFlow(new Text("test"));

        textFlow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textFlow, Priority.ALWAYS);

        Button delete = new Button("-");

        HBox buttonGroup = new HBox(5, delete);
        buttonGroup.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(buttonGroup, Priority.NEVER);

        item.getChildren().addAll(textFlow, buttonGroup);

        delete.setOnAction(e -> ingredientsList.getChildren().remove(item));

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
