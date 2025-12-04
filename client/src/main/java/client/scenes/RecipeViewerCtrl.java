package client.scenes;

import client.services.RecipeManager;
import commons.Ingredient;
import commons.Recipe;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class RecipeViewerCtrl {

    @FXML
    private Label titleLabel;
    @FXML
    private ListView<String> ingredientsList;
    @FXML
    private ListView<String> preparationList;
    @FXML
    private Button editButton;

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

  /**
   * This sets the values inside of the Recipeviewer
   * @param recipe the recipe
   */
    public void setRecipe(Recipe recipe){
        this.currentRecipe = recipe;

        if(recipe == null){
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
   * @param recipe the recipe
   */
    private void setIngredientsList(Recipe recipe) {
        ingredientsList.getItems().clear();
        var ingredients = recipe.getIngredients();
        if (ingredients != null) {
            for (var recipeIngredient : ingredients) {
                Ingredient ingredient = RecipeManager.getInstance().getIngredient(recipeIngredient);
                ingredientsList.getItems().add(ingredient.getName() + " | " + recipeIngredient.getAmount().toPrettyString());
            }
        }
    }

  /**
   * This sets the steps
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
   * Opens the AddRecipe panel pre filled with the currently viewed recipe.
   */
    @FXML
    private void editRecipe() {
        if (mainCtrl != null && currentRecipe != null) {
            mainCtrl.editRecipe(currentRecipe);
        }
    }
}
