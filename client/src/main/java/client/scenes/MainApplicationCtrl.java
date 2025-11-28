package client.scenes;

import client.MyFXML;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import commons.Recipe;
import client.scenes.RecipeListCtrl;
import javafx.scene.control.ListView;

import java.util.Locale;
import java.util.ResourceBundle;

import static client.Main.BUNDLE_NAME;
import static client.Main.DEFAULT_LOCALE;

public class MainApplicationCtrl {

    /**
     *   This is the right pane(This pane will load different screens)
     */
    @FXML
    private Pane contentPane;

    private final StringProperty addProperty = new SimpleStringProperty();
    @FXML
    private Button addButton;

    private final StringProperty removeProperty = new SimpleStringProperty();
    @FXML
    private Button removeButton;

    private final StringProperty refreshProperty = new SimpleStringProperty();
    @FXML
    private Button refreshButton;

    @FXML
    private ListView<Recipe> recipeListView;

    private RecipeListCtrl recipeListCtrl;

    private MyFXML fxml;

    @Inject
    public MainApplicationCtrl(MyFXML fxml){
        this.fxml =fxml;
    }

    /**
     * Binds the elements of the UI to StringProperties,
     * allowing dynamic updates, i.e. instant propagation,
     * of the language of buttons, labels, etc.
     * Should only be called once when initializing the controller.
     */
    private void bindElementsProperties() {
        addButton.textProperty().bind(addProperty);
        removeButton.textProperty().bind(removeProperty);
        refreshButton.textProperty().bind(refreshProperty);
    }

    /**
     * Dynamically updates properties of UI elements to the language
     * of a corresponding locale
     * @param locale provided locale/language for UI elements
     */
    private void setLocale(Locale locale) {
        var resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        addProperty.set(resourceBundle.getString("txt.add"));
        removeProperty.set(resourceBundle.getString("txt.remove"));
        refreshProperty.set(resourceBundle.getString("txt.refresh"));
    }

    /**
     *   Loads Recipe panel
     */
    @FXML
    private void addRecipe() {
        var bundle = ResourceBundle.getBundle(BUNDLE_NAME, DEFAULT_LOCALE);
        Pair<AddRecipeCtrl, Parent> pair = fxml.load(AddRecipeCtrl.class, bundle,
            "client", "scenes", "AddRecipePanel.fxml");

        /*
        Injects the main ctrl into the add recipe ctrl
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
     *     Binds the existing FXML {@code ListView} to the controller
     *     so recipe titles can be displayed.
     *     Configures the Remove button so that clicking it puts the list into "remove mode",
     *         meaning the next click on a recipe name will remove that specific recipe.
     * Only listing and remove-on-click behavior are implemented at this stage.
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
