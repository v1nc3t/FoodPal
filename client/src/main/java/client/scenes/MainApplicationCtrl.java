package client.scenes;

import client.MyFXML;
import client.services.LocaleManager;
import client.services.RecipeManager;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import commons.Recipe;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainApplicationCtrl implements Internationalizable {

    /**
     *   This is the right pane(This pane will load different screens)
     */
    @FXML
    private Pane contentPane;

    @FXML
    private ChoiceBox<String> orderBy;

    @FXML private ChoiceBox<String> languageOptions;

    @FXML
    private Button addButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button removeButton;

    private final StringProperty refreshProperty = new SimpleStringProperty();
    @FXML
    private Button refreshButton;

    @FXML
    private Button cloneButton;

    @FXML
    private ListView<String> recipeListView;

    private RecipeListCtrl recipeListCtrl;

    private final MyFXML fxml;
    private final LocaleManager localeManager;

    private Recipe currentlyShownRecipe = null;

    @Inject
    public MainApplicationCtrl(MyFXML fxml, LocaleManager localeManager) {
        this.fxml = fxml;
        this.localeManager = localeManager;

        this.localeManager.register(this);
    }

    /**
     * Binds the elements of the UI to StringProperties,
     * allowing dynamic updates, i.e. instant propagation,
     * of the language of buttons, labels, etc.
     * Should only be called once when initializing the controller.
     */
    private void bindElementsProperties() {
        refreshButton.textProperty().bind(refreshProperty);
        //addButton.textProperty().bind(addProperty);
    }

    /**
     * Dynamically updates properties of UI elements to the language
     * of a corresponding locale
     * @param newLocale provided locale/language for UI elements
     */
    @Override
    public void setLocale(Locale newLocale) {
        var resourceBundle = ResourceBundle.getBundle(localeManager.getBundleName(), newLocale);
        refreshProperty.set(resourceBundle.getString("txt.refresh"));
        //addProperty.set(resourceBundle.getString("txt.add"));
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
        setLocale(localeManager.getCurrentLocale());

        prepareLanguageOptions();

        prepareSortBy();
        recipeListCtrl = new RecipeListCtrl();
        recipeListCtrl.initialize();

        if (recipeListView != null) {
            recipeListCtrl.setListView(recipeListView);

            recipeListCtrl.setOnCloneRequest(originalRecipe -> openClonePopup(originalRecipe));
            // open viewer on double-click, and ignore clicks when in remove mode
            recipeListView.setOnMouseClicked(evt -> {
                if (evt.getClickCount() != 2) return; // require double-click to open

                int index = recipeListView.getSelectionModel().getSelectedIndex();
                Recipe sel = RecipeManager.getInstance().getObservableRecipes().get(index);
                if (sel == null) return;

                // If the list is in remove mode, the click was for deleting — ignore it
                if (recipeListCtrl != null && recipeListCtrl.isInRemoveMode()) {
                    recipeListView.getSelectionModel().clearSelection(); // avoid visual flicker
                    evt.consume();
                    return;
                }

                // Open the viewer
                showRecipe(sel);
            });

            if (cloneButton != null) {
                cloneButton.setOnAction(e -> recipeListCtrl.enterCloneMode());
            }

        }

        if (removeButton != null) {
            removeButton.setOnAction(e -> recipeListCtrl.enterRemoveMode());
        }
        //if the currently shown recipe disappears, close viewer.
        client.services.RecipeManager.getInstance().getObservableRecipes()
                .addListener((javafx.collections.ListChangeListener<Recipe>) change -> {
                    while (change.next()) {
                        if (change.wasRemoved() && currentlyShownRecipe != null) {
                            boolean stillPresent = client.services.RecipeManager.getInstance()
                                    .getObservableRecipes()
                                    .stream()
                                    .anyMatch(r -> java.util.Objects.equals(r.getId(), currentlyShownRecipe.getId()));
                            if (!stillPresent) {
                                javafx.application.Platform.runLater(() -> {
                                    showMainScreen();
                                    currentlyShownRecipe = null;
                                });
                            }
                        }
                    }
                });

        sortUponSelection(recipeListCtrl);
    }

    /**
     * Prepares the sort-by choice box, by default sorting alphabetically.
     * TODO: enable internationalization
     */
    private void prepareSortBy() {
        orderBy.getItems().setAll("Alphabetical", "Most recent");
        orderBy.setValue("Alphabetical");
    }

    private void prepareLanguageOptions() {
        languageOptions.getItems().setAll("English", "German", "Dutch");

        Locale currentLocale = localeManager.getCurrentLocale();
        String currentDisplay;
        if (currentLocale.equals(Locale.GERMAN)) {
            currentDisplay = "German";
        } else if (currentLocale.equals(Locale.forLanguageTag("nl-NL"))) {
            currentDisplay = "Dutch";
        } else {
            currentDisplay = "English";
        }

        languageOptions.setValue(currentDisplay);
    }

    @FXML
    private void changeLanguage() {
        String currentDisplay = languageOptions.getValue();

        Locale newLocale;
        switch (currentDisplay) {
            case "German":
                newLocale = Locale.GERMAN;
                break;
            case "Dutch":
                newLocale = Locale.forLanguageTag("nl-NL");
                break;
            case "English":
                newLocale = Locale.ENGLISH;
                break;
            default:
                return;
        }

        localeManager.setAllLocale(newLocale);
    }

    /**
     * Adds a listener for changes to sort-by choice box,
     * so recipe list viewer can get sorted after a selection.
     * @param recipeListCtrl the recipe list controller which is responsible for the recipe list
     */
    private void sortUponSelection(RecipeListCtrl recipeListCtrl) {
        SingleSelectionModel<String> selectionModel = orderBy.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            recipeListCtrl.setSortMethod(newValue);
        });
        //if the currently shown recipe disappears, close viewer.
        client.services.RecipeManager.getInstance().getObservableRecipes()
                .addListener((javafx.collections.ListChangeListener<Recipe>) change -> {
                    while (change.next()) {
                        if (change.wasRemoved() && currentlyShownRecipe != null) {
                            boolean stillPresent = client.services.RecipeManager.getInstance()
                                    .getObservableRecipes()
                                    .stream()
                                    .anyMatch(r -> java.util.Objects.equals(r.getId(), currentlyShownRecipe.getId()));
                            if (!stillPresent) {
                                javafx.application.Platform.runLater(() -> {
                                    showMainScreen();
                                    currentlyShownRecipe = null;
                                });
                            }
                        }
                    }
                });
    }

    /**
     *   Loads Recipe panel
     */
    @FXML
    private void addRecipe() {
        var bundle = localeManager.getCurrentBundle();
        Pair<AddRecipeCtrl, Parent> pair = fxml.load(AddRecipeCtrl.class,bundle,
                "client", "scenes", "AddRecipePanel.fxml");

        AddRecipeCtrl addRecipeCtrl = pair.getKey();
        Parent addRecipeRoot = pair.getValue();

        contentPane.getChildren().setAll(addRecipeRoot);
    }

    private void showRecipe(Recipe recipe) {
        var bundle = localeManager.getCurrentBundle();
        if (recipe == null) {
            showMainScreen();
            currentlyShownRecipe = null;
            return;
        }

        Pair<RecipeViewerCtrl, Parent> pair = fxml.load(RecipeViewerCtrl.class, bundle,
                "client", "scenes", "RecipeViewer.fxml");

        RecipeViewerCtrl viewerCtrl = pair.getKey();
        Parent viewerRoot = pair.getValue();

        viewerCtrl.setRecipe(recipe);

        contentPane.getChildren().setAll(viewerRoot);
        currentlyShownRecipe = recipe;
    }


    public void showRecipeViewer(Recipe recipe) {
        showRecipe(recipe);   // reuse your existing private method
    }



    /**
     * Search field for users to search up items/recipes from ist
     */
    public void search(){
        String query = searchField.getText();

        // To be implemented once server side is done.
        System.out.println("Searching for '" + query);
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
            recipeListCtrl.initialize();
            if (recipeListView != null) recipeListCtrl.setListView(recipeListView);
        }
        recipeListCtrl.addRecipe(r);
    }

    public void editRecipe(Recipe recipe) {
        var bundle = localeManager.getCurrentBundle();
        if (recipe == null) return;

        Pair<AddRecipeCtrl, Parent> pair = fxml.load(AddRecipeCtrl.class, bundle,
            "client", "scenes", "AddRecipePanel.fxml");

        AddRecipeCtrl addRecipeCtrl = pair.getKey();
        Parent addRecipeRoot = pair.getValue();

        addRecipeCtrl.loadRecipe(recipe);

        contentPane.getChildren().setAll(addRecipeRoot);
    }

    /**
     * Refreshes db to get latest data
     */
    public void refresh(){
        // Temp. Rewriting this once server side is done
        System.out.println("Refresh pressed (Server logic not implemented yet)");

        showMainScreen();
    }
    /**
     * Opens a modal popup asking the user to enter a name for a cloned recipe.
     * The popup is pre-filled with {@code "<original title> (Copy)"} for convenience.
     * @param original the recipe to be cloned (must not be null)
     */
    private void openClonePopup(Recipe original) {
        TextInputDialog dialog = new TextInputDialog(original.getTitle() + " (Copy)");
        dialog.setTitle("Clone Recipe");
        dialog.setHeaderText("Cloning: " + original.getTitle());
        dialog.setContentText("Enter a name for the cloned recipe:");

        var result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String newName = result.get();

        Recipe clone = original.cloneWithTitle(newName);
        client.services.RecipeManager.getInstance().addRecipeOptimistic(clone);
        showRecipe(clone);

    }

}

