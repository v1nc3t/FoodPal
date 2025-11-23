package client.scenes;

import client.utils.ServerUtils;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

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
    // main controller attribute
    private final MainApplicationCtrl mainCtrl;

    @Inject
    public AddRecipeCtrl(ServerUtils server, MainApplicationCtrl mainCtrl) {
        this.server = server;
        // inject main controller
        this.mainCtrl = mainCtrl;
    }

    public void clickCancel() {
        clearFields();
        // show the main view
        mainCtrl.showMainScreen();
    }

    public void clickDone() {
        try {
            server.addRecipe(getRecipe());
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        clearFields();
        // show the main
        mainCtrl.showMainScreen();
    }

    private void clearFields() {
        nameField.clear();
        servingSizeField.clear();
        preparationField.clear();

        ingredientsComboBox.getSelectionModel().clearSelection();
        ingredientsComboBox.getItems().clear();

        preparationList.getChildren().clear();
    }

    private Recipe getRecipe() {
        return new Recipe(
                getName(),
                getIngredients(),
                getPreparations(),
                getServingSize()
        );
    }

    private String getName() {
        return nameField.getCharacters().toString();
    }

    private List<RecipeIngredient> getIngredients() {
        return new ArrayList<>(); // from input of user
    }

    private List<String> getPreparations() {
        return new ArrayList<>(); // from input of user
    }

    private int getServingSize() {
        return Integer.parseInt(servingSizeField.getCharacters().toString());
    }

    public void clickAddPreparation() {
        String step =  preparationField.getText().trim();
        if (step.isEmpty()) {
            return;
        }

        HBox newItem = createPreparationItem(step);
        preparationList.getChildren().add(newItem);

        preparationField.clear();
    }

    private HBox createPreparationItem(String text) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(text);
        label.setWrapText(false);

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

    private void moveUp(HBox item) {
        int index = preparationList.getChildren().indexOf(item);
        if (index > 0) {
            preparationList.getChildren().remove(index);
            preparationList.getChildren().add(index - 1, item);
        }
    }

    private void moveDown(HBox item) {
        int index = preparationList.getChildren().indexOf(item);
        if (index < preparationList.getChildren().size() - 1) {
            preparationList.getChildren().remove(index);
            preparationList.getChildren().add(index + 1, item);
        }
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                clickDone();
                break;
            case ESCAPE:
                clickCancel();
                break;
            default:
                break;
        }
    }
}
