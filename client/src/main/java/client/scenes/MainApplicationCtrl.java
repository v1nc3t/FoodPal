package client.scenes;

import client.MyFXML;
import client.config.Config;
import client.services.LocaleManager;
import client.services.RecipeManager;
import commons.Language;
import com.google.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import commons.Recipe;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApplicationCtrl implements Internationalizable {

    /**
     * This is the right pane(This pane will load different screens)
     */
    @FXML
    private Pane contentPane;

    @FXML
    private ChoiceBox<String> orderBy;
    private final StringProperty alphabeticalProperty = new SimpleStringProperty();
    private final StringProperty recentProperty = new SimpleStringProperty();

    @FXML
    private ChoiceBox<String> languageOptions;

    private final StringProperty showRecipesProperty = new SimpleStringProperty();
    @FXML
    private Label showRecipesLabel;

    private final StringProperty englishProperty = new SimpleStringProperty();
    @FXML
    private CheckBox englishFilter;

    private final StringProperty germanProperty = new SimpleStringProperty();
    @FXML
    private CheckBox germanFilter;

    private final StringProperty dutchProperty = new SimpleStringProperty();
    @FXML
    private CheckBox dutchFilter;

    @FXML
    private Button addButton;

    @FXML
    private TextField searchField;
    private final StringProperty searchProperty = new SimpleStringProperty();

    @FXML
    private Button removeButton;

    private final StringProperty refreshProperty = new SimpleStringProperty();
    @FXML
    private Button refreshButton;

    private final StringProperty cloneProperty = new SimpleStringProperty();
    @FXML
    private Button cloneButton;

    private final StringProperty favouriteProperty = new SimpleStringProperty();
    @FXML
    private Button favouriteButton;

    @FXML
    private ListView<ListObject> recipeListView;

    @Inject
    private SidebarListCtrl sidebarListCtrl;

    private final MyFXML fxml;
    private final LocaleManager localeManager;

    private Recipe currentlyShownRecipe = null;

    @Inject
    private RecipeManager recipeManager;

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
        showRecipesLabel.textProperty().bind(showRecipesProperty);
        englishFilter.textProperty().bind(englishProperty);
        germanFilter.textProperty().bind(germanProperty);
        dutchFilter.textProperty().bind(dutchProperty);
        cloneButton.textProperty().bind(cloneProperty);
        searchField.promptTextProperty().bind(searchProperty);
        favouriteButton.textProperty().bind(favouriteProperty);
    }

    /**
     * Dynamically updates properties of UI elements to the language
     * of a corresponding locale
     * 
     * @param newLocale provided locale/language for UI elements
     */
    @Override
    public void setLocale(Locale newLocale) {
        var resourceBundle = ResourceBundle.getBundle(localeManager.getBundleName(), newLocale);

        refreshProperty.set(resourceBundle.getString("txt.refresh"));
        showRecipesProperty.set(resourceBundle.getString("txt.show_recipes"));
        cloneProperty.set(resourceBundle.getString("txt.clone"));
        favouriteProperty.set(resourceBundle.getString("txt.favourite"));
        searchProperty.set(resourceBundle.getString("txt.search"));

        alphabeticalProperty.set(resourceBundle.getString("txt.alphabetical"));
        recentProperty.set(resourceBundle.getString("txt.recent"));

        if (orderBy != null) {
            int selectedIndex = orderBy.getSelectionModel().getSelectedIndex();
            orderBy.getItems().setAll(
                    alphabeticalProperty.get(),
                    recentProperty.get());
            orderBy.getSelectionModel().select(selectedIndex >= 0 ? selectedIndex : 0);
        }

        englishProperty.set(resourceBundle.getString("txt.en"));
        germanProperty.set(resourceBundle.getString("txt.de"));
        dutchProperty.set(resourceBundle.getString("txt.nl"));
        if (languageOptions != null) {
            int selectedIndex = languageOptions.getSelectionModel().getSelectedIndex();
            languageOptions.getItems().setAll(
                    englishProperty.get(),
                    germanProperty.get(),
                    dutchProperty.get());
            if (selectedIndex >= 0) {
                languageOptions.getSelectionModel().select(selectedIndex);
            }
        }
    }

    /**
     * This clears the current screen back to the main(blank for now)
     */
    public void showMainScreen() {
        contentPane.getChildren().clear();
    }

    /**
     * Initializes the main application UI components related to the recipe list.
     * This method is automatically called by the JavaFX runtime after FXML loading.
     * It performs the following:
     * Creates a new {@link SidebarListCtrl} instance, which manages the list of
     * recipes.
     * Binds the existing FXML {@code ListView} to the controller
     * so recipe titles can be displayed.
     * Configures the Remove button so that clicking it puts the list into "remove
     * mode",
     * meaning the next click on a recipe name will remove that specific recipe.
     * Only listing and remove-on-click behavior are implemented at this stage.
     */
    @FXML
    private void initialize() {
        bindElementsProperties();

        setLocale(localeManager.getCurrentLocale());
        prepareLanguageOptions();
        prepareSortBy();

        sidebarListCtrl.initialize();
        if (recipeListView != null) {
            sidebarListCtrl.setListView(recipeListView);
            sidebarListCtrl.setOnRecipeCloneRequest(this::openClonePopup);
            // open viewer on double-click, and ignore clicks when in remove mode
            recipeListView.setOnMouseClicked(evt -> {
                if (evt.getClickCount() != 2)
                    return; // require double-click to open
                var sel = recipeListView.getSelectionModel().getSelectedItem();
                if (sel == null)
                    return;
                // If the list is in remove mode, the click was for deleting — ignore it
                if (sidebarListCtrl != null && sidebarListCtrl.isInRemoveMode()) {
                    recipeListView.getSelectionModel().clearSelection(); // avoid visual flicker
                    evt.consume();
                    return;
                }
                // Open the viewer
                showRecipe(recipeManager.getRecipe(sel.id()));
            });
            if (cloneButton != null) {
                cloneButton.setOnAction(e -> sidebarListCtrl.enterCloneMode());
            }
        }
        if (removeButton != null) {
            removeButton.setOnAction(e -> sidebarListCtrl.enterRemoveMode());
        }
        if (favouriteButton != null) {
            favouriteButton.setOnAction(e -> sidebarListCtrl.enterFavouriteMode());
        }
        // if the currently shown recipe disappears, close viewer.
        recipeManager.getObservableRecipes()
                .addListener((javafx.collections.ListChangeListener<Recipe>) change -> {
                    while (change.next()) {
                        if (change.wasRemoved() && currentlyShownRecipe != null) {
                            boolean stillPresent = recipeManager
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
        sortUponSelection(sidebarListCtrl);
        prepareLanguageFilters(sidebarListCtrl);
        filterUponSelection(sidebarListCtrl);
    }

    /**
     * Initializes the language filter checkboxes according to the config.
     * If reading from the config fails, resorts to defaulting to all languages.
     * @param sidebarListCtrl the recipe list controller which is responsible for
     *                       the recipe list
     */
    private void prepareLanguageFilters(SidebarListCtrl sidebarListCtrl) {
        try {
            Config config = localeManager.getConfigManager().getConfig();
            List<Language> languages = config.getLanguageFilters();
            englishFilter.setSelected(languages.contains(Language.EN));
            germanFilter.setSelected(languages.contains(Language.DE));
            dutchFilter.setSelected(languages.contains(Language.NL));

            // propagate the config languages to the sortUtils
            if (!languages.contains(Language.EN)) {
                sidebarListCtrl.toggleLanguageFilter(Language.EN);
            }
            if (!languages.contains(Language.DE)) {
                sidebarListCtrl.toggleLanguageFilter(Language.DE);
            }
            if (!languages.contains(Language.NL)) {
                sidebarListCtrl.toggleLanguageFilter(Language.NL);
            }
        }
        catch (Exception e) {
            englishFilter.setSelected(true);
            germanFilter.setSelected(true);
            dutchFilter.setSelected(true);
        }
    }

    /**
     * Adds a listener for changes to language filter checkboxes,
     * so the recipe list viewer can get filtered after a selection of a filter.
     * Also propagates the checkbox changes to the config file.
     * @param sidebarListCtrl the recipe list controller which is responsible for
     *                        the recipe list
     */
    private void filterUponSelection(SidebarListCtrl sidebarListCtrl) {
        englishFilter.selectedProperty().addListener((
                observable, oldValue, newValue) -> {
            sidebarListCtrl.toggleLanguageFilter(Language.EN);
            updateConfigFilter(Language.EN);
        });
        germanFilter.selectedProperty().addListener((
                observable, oldValue, newValue) -> {
            sidebarListCtrl.toggleLanguageFilter(Language.DE);
            updateConfigFilter(Language.DE);
        });
        dutchFilter.selectedProperty().addListener((
                observable, oldValue, newValue) -> {
            sidebarListCtrl.toggleLanguageFilter(Language.NL);
            updateConfigFilter(Language.NL);
        });
    }

    /**
     * Updates the config file by adding or removing the given language filter.
     * @param language language to be added or removed
     */
    private void updateConfigFilter(Language language) {
        Config config = localeManager.getConfigManager().getConfig();
        List<Language> languages = config.getLanguageFilters();

        if (languages.contains(language)) {
            languages.remove(language);
        } else {
            languages.add(language);
        }

        config.setLanguageFilters(languages);
        localeManager.getConfigManager().save();
    }

    /**
     * Prepares the sort-by choice box, by default sorting alphabetically.
     */
    private void prepareSortBy() {
        orderBy.getItems().setAll(
                alphabeticalProperty.get(),
                recentProperty.get());
        orderBy.setValue(alphabeticalProperty.get());
    }

    /**
     * Adds a listener for changes to sort-by choice box,
     * so recipe list viewer can get sorted after a selection.
     * 
     * @param sidebarListCtrl the recipe list controller which is responsible for
     *                        the recipe list
     */
    private void sortUponSelection(SidebarListCtrl sidebarListCtrl) {
        orderBy.getSelectionModel().selectedItemProperty().addListener((
                observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            if (newValue.equals(alphabeticalProperty.get())) {
                sidebarListCtrl.setSortMethod("Alphabetical");
            } else if (newValue.equals(recentProperty.get())) {
                sidebarListCtrl.setSortMethod("Most recent");
            }
        });
        // if the currently shown recipe disappears, close viewer.
        recipeManager.getObservableRecipes()
                .addListener((javafx.collections.ListChangeListener<Recipe>) change -> {
                    while (change.next()) {
                        if (change.wasRemoved() && currentlyShownRecipe != null) {
                            boolean stillPresent = recipeManager
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

    private void prepareLanguageOptions() {
        languageOptions.getItems().setAll(
                englishProperty.get(),
                germanProperty.get(),
                dutchProperty.get());

        Locale current = localeManager.getCurrentLocale();
        if (current.equals(LocaleManager.DE)) {
            languageOptions.setValue(germanProperty.get());
        } else if (current.equals(LocaleManager.NL)) {
            languageOptions.setValue(dutchProperty.get());
        } else {
            languageOptions.setValue(englishProperty.get());
        }
    }

    @FXML
    private void changeLanguage() {
        String currentDisplay = languageOptions.getValue();
        if (currentDisplay == null) {
            return;
        }

        Locale newLocale;
        if (currentDisplay.equals(germanProperty.get())) {
            newLocale = LocaleManager.DE;
        } else if (currentDisplay.equals(dutchProperty.get())) {
            newLocale = LocaleManager.NL;
        } else {
            newLocale = LocaleManager.EN;
        }

        if (!newLocale.equals(localeManager.getCurrentLocale())) {
            localeManager.setAllLocale(newLocale);
        }
    }

    /**
     * Loads Recipe panel
     */
    @FXML
    private void addRecipe() {
        var bundle = localeManager.getCurrentBundle();
        Pair<AddRecipeCtrl, Parent> pair = fxml.load(AddRecipeCtrl.class, bundle,
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
        showRecipe(recipe); // reuse your existing private method
    }

    /**
     * Search field for users to search up items/recipes from ist
     */
    public void search() {
        String query = searchField.getText();

        // To be implemented once server side is done.
        System.out.println("Searching for '" + query);
    }

    /**
     * Adds a newly created recipe to the left-hand recipe list.
     * This method is called by {@link AddRecipeCtrl} after the user completes the
     * Add Recipe form and presses the Done button. It ensures that:
     * The recipe appears immediately in the list displayed in the left rectangle.
     * If the {@link SidebarListCtrl} was not initialized yet,
     * it will be created and linked to the FXML {@code ListView}.
     * This method performs an in-memory update only; no server or database
     * persistence is involved at this stage.
     *
     * @param r the newly created {@link Recipe} to add to the UI list
     */
    public void addRecipeToList(Recipe r) {
        if (sidebarListCtrl == null) {
            sidebarListCtrl = new SidebarListCtrl();
            sidebarListCtrl.initialize();
            if (recipeListView != null)
                sidebarListCtrl.setListView(recipeListView);
        }
        sidebarListCtrl.addRecipe(r);
    }

    public void editRecipe(Recipe recipe) {
        var bundle = localeManager.getCurrentBundle();
        if (recipe == null)
            return;

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
    public void refresh() {
        // Temp. Rewriting this once server side is done
        System.out.println("Refresh pressed (Server logic not implemented yet)");

        showMainScreen();
    }

    /**
     * Opens a modal popup asking the user to enter a name for a cloned recipe.
     * The popup is pre-filled with {@code "<original title> (Copy)"} for
     * convenience.
     * 
     * @param original the recipe to be cloned (must not be null)
     */
    private void openClonePopup(Recipe original) {
        TextInputDialog dialog = new TextInputDialog(original.getTitle() + " (Copy)");
        dialog.setTitle("Clone Recipe");
        dialog.setHeaderText("Cloning: " + original.getTitle());
        dialog.setContentText("Enter a name for the cloned recipe:");

        var result = dialog.showAndWait();
        if (result.isEmpty())
            return;

        String newName = result.get();

        Recipe clone = original.cloneWithTitle(newName);
        recipeManager.addRecipeOptimistic(clone);
        showRecipe(clone);

    }

    @FXML
    private void showShoppingList() {
        var bundle = localeManager.getCurrentBundle();
        Pair<ShoppingListCtrl, Parent> pair = fxml.load(ShoppingListCtrl.class, bundle,
                "client", "scenes", "ShoppingList.fxml");

        ShoppingListCtrl shoppingListCtrl = pair.getKey();
        Parent shoppingListRoot = pair.getValue();

        contentPane.getChildren().setAll(shoppingListRoot);
        // Reset selection in sidebar or handle "active view" state if needed
        currentlyShownRecipe = null;
    }

}
