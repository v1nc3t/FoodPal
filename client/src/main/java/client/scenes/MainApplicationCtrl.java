package client.scenes;

import client.MyFXML;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import commons.Recipe;
import client.scenes.RecipeListCtrl;
import javafx.scene.control.ListView;

public class MainApplicationCtrl {

    /**
     *   This is the right pane(This pane will load different screens)
     */
    @FXML
    private Pane contentPane;

    @FXML
    private Button addButton;

    @FXML
    private ListView<Recipe> recipeListView;

    @FXML
    private Button removeButton;

    private RecipeListCtrl recipeListCtrl;

    private MyFXML fxml;

    @Inject
    public MainApplicationCtrl(MyFXML fxml){
        this.fxml =fxml;
    }

    /**
     *   Loads Recipe panel
     */
    @FXML
    private void addRecipe() {
        Pair<AddRecipeCtrl, Parent> pair = fxml.load(AddRecipeCtrl.class,
                "client", "scenes", "AddRecipePanel.fxml");

        /**
         *  Injects the main ctrl into the add recipe ctrl
         */
        AddRecipeCtrl addRecipeCtrl = pair.getKey();
        Parent addRecipeRoot = pair.getValue();

        contentPane.getChildren().setAll(addRecipeRoot);
    }

    /**
     *  This clears the current screen back to the main(blank for now)
     */
    public void showMainScreen(){
        contentPane.getChildren().clear();
    }
    /**
     * Initializes the main application UI components related to the recipe list.
     * This method is automatically called by the JavaFX runtime after FXML loading.
     * It performs the following:
     *     Creates a new {@link RecipeListCtrl} instance, which manages the list of recipes.
     *     Binds the existing FXML {@code ListView} to the controller so recipe titles can be displayed.
     *     Configures the Remove button so that clicking it puts the list into "remove mode",
     *         meaning the next click on a recipe name will remove that specific recipe.
     * Only listing and remove-on-click behavior are implemented at this stage.
     */
    @FXML
    private void initialize() {
        recipeListCtrl = new RecipeListCtrl();
        if (recipeListView != null) recipeListCtrl.setListView(recipeListView);
        if (removeButton != null) removeButton.setOnAction(e -> recipeListCtrl.enterRemoveMode());
    }

    /**
     * Adds a newly created recipe to the left-hand recipe list.
     * This method is called by {@link AddRecipeCtrl} after the user completes the
     * Add Recipe form and presses the Done button. It ensures that:
     *     The recipe appears immediately in the list displayed in the left rectangle.
     *     If the {@link RecipeListCtrl} was not initialized yet,
     *         it will be created and linked to the FXML {@code ListView}.
     * This method performs an in-memory update only; no server or database
     * persistence is involved at this stage.
     *
     * @param r the newly created {@link Recipe} to add to the UI list
     */
    public void addRecipeToList(Recipe r) {
        if (recipeListCtrl == null) {
            recipeListCtrl = new RecipeListCtrl();
            if (recipeListView != null) recipeListCtrl.setListView(recipeListView);
        }
        recipeListCtrl.addRecipe(r);
    }

}
