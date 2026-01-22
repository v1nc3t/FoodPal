package client.scenes;

import client.MyFXML;
import client.config.Config;
import client.config.FavoriteRecipe;
import client.services.LocaleManager;
import client.services.RecipeManager;
import client.services.WebSocketService;
import client.utils.ServerUtils;
import commons.*;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.util.Pair;
import commons.Recipe;
import client.shoppingList.ShoppingListItem;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.*;

public class MainApplicationCtrl implements Internationalizable {

    /**
     * This is the right pane(This pane will load different screens)
     */
    @FXML
    private Pane contentPane;

    @FXML
    private ChoiceBox<String> orderBy;
    private final StringProperty alphabeticalProperty = new SimpleStringProperty();
    private final StringProperty reverseAlphabeticalProperty = new SimpleStringProperty();

    @FXML
    private ComboBox<Language> languageOptions;

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

    private final StringProperty onlyShowFavouritesProperty = new SimpleStringProperty();
    @FXML
    private Label onlyShowFavouritesLabel;

    @FXML
    private CheckBox onlyShowFavouritesToggle;

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
    private ListView<ListObject> sidebarListView;

    @FXML
    private ToggleButton recipeToggleButton;
    private final StringProperty recipeToggleTextProperty = new SimpleStringProperty();
    @FXML
    private ToggleButton ingredientToggleButton;
    private final StringProperty ingredientToggleTextProperty = new SimpleStringProperty();
    private final ToggleGroup categoryToggleGroup = new ToggleGroup();

    private final StringProperty syncProperty = new SimpleStringProperty();
    private final StringProperty cloneRecipeProperty = new SimpleStringProperty();
    private final StringProperty cloningProperty = new SimpleStringProperty();
    private final StringProperty enterCloneNameProperty = new SimpleStringProperty();
    private final StringProperty copyProperty = new SimpleStringProperty();

    @FXML
    private ToggleButton themeToggle;

    @Inject
    private SidebarListCtrl sidebarListCtrl;

    private final MyFXML fxml;
    private final LocaleManager localeManager;

    /// The id of the currently shown object, can be either an id of {@link Recipe},
    /// or an id of {@link Ingredient}
    private UUID currentlyShownId = null;

    @Inject
    private RecipeManager recipeManager;
    @Inject
    private ServerUtils serverUtils;
    @Inject
    private WebSocketService webSocketService;

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
        onlyShowFavouritesLabel.textProperty().bind(onlyShowFavouritesProperty);
        cloneButton.textProperty().bind(cloneProperty);
        searchField.promptTextProperty().bind(searchProperty);
        favouriteButton.textProperty().bind(favouriteProperty);
        recipeToggleButton.textProperty().bind(recipeToggleTextProperty);
        ingredientToggleButton.textProperty().bind(ingredientToggleTextProperty);
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
        onlyShowFavouritesProperty.set(resourceBundle.getString("txt.only_favourites"));
        recipeToggleTextProperty.set(resourceBundle.getString("txt.recipe"));
        ingredientToggleTextProperty.set(resourceBundle.getString("txt.ingredient"));
        syncProperty.set(resourceBundle.getString("txt.sync_error"));
        alphabeticalProperty.set(resourceBundle.getString("txt.alphabetical"));
        reverseAlphabeticalProperty.set(resourceBundle.getString("txt.reverse_alphabetical"));
        cloneRecipeProperty.set(resourceBundle.getString("txt.clone_recipe"));
        cloningProperty.set(resourceBundle.getString("txt.cloning"));
        enterCloneNameProperty.set(resourceBundle.getString("txt.enter_clone_name"));
        copyProperty.set(resourceBundle.getString("txt.copy"));

        if (orderBy != null) {
            int selectedIndex = orderBy.getSelectionModel().getSelectedIndex();
            orderBy.getItems().setAll(
                    alphabeticalProperty.get(),
                    reverseAlphabeticalProperty.get());
            orderBy.getSelectionModel().select(selectedIndex >= 0 ? selectedIndex : 0);
        }

        englishProperty.set(resourceBundle.getString("txt.en"));
        germanProperty.set(resourceBundle.getString("txt.de"));
        dutchProperty.set(resourceBundle.getString("txt.nl"));

        Language.reloadLocale(resourceBundle);

        if (languageOptions != null) {
            var items = languageOptions.getItems();
            languageOptions.setItems(null);
            languageOptions.setItems(items);

            String currentCode = newLocale.getLanguage().toUpperCase();
            languageOptions.setValue(Language.valueOf(currentCode));
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

        initializeSidebarListCtrl();

        prepareLanguageOptions();
        prepareSortBy();
        prepareSearchField();
        prepareToggleTheme();

        sidebarListCtrl.initialize();

        sidebarListCtrl.setListView(sidebarListView);

        sidebarListCtrl.setOnAddRecipeRequest(this::addRecipe);
        sidebarListCtrl.setOnAddIngredientRequest(this::addIngredient);
        sidebarListCtrl.setOnRecipeCloneRequest(this::openClonePopup);
        // open viewer on double-click, and ignore clicks when in remove mode
        sidebarListView.setOnMouseClicked(evt -> {
            if (evt.getClickCount() != 2)
                return; // require double-click to open

            var sel = sidebarListView.getSelectionModel().getSelectedItem();
            if (sel == null)
                return;

            // If the list is in remove mode, the click was for deleting — ignore it
            if (sidebarListCtrl != null && sidebarListCtrl.isInRemoveMode()) {
                sidebarListView.getSelectionModel().clearSelection(); // avoid visual flicker
                evt.consume();
                return;
            }

            // Open the viewer
            switch (sidebarListCtrl.getSidebarMode()) {
                case ESidebarMode.Recipe ->
                    showRecipe(recipeManager.getRecipe(sel.id()));
                case ESidebarMode.Ingredient ->
                    showIngredient(recipeManager.getIngredient(sel.id()));
            }
        });

        addButton.setOnAction(_ -> sidebarListCtrl.enterAddMode());
        cloneButton.setOnAction(_ -> sidebarListCtrl.enterCloneMode());
        removeButton.setOnAction(_ -> sidebarListCtrl.enterRemoveMode());
        favouriteButton.setOnAction(e -> sidebarListCtrl.enterFavouriteMode());

        sortUponSelection(sidebarListCtrl);
        prepareLanguageFilters(sidebarListCtrl);
        filterUponSelection(sidebarListCtrl);

        recipeManager.setOnFavoriteRecipeDeleted(this::showDeletedRecipePrompt);
        Platform.runLater(() -> {
            refresh(recipeManager::refreshFavoriteRecipes);
        });

        sidebarListCtrl.propagateFavouritesNoConfig(
                new HashSet<>(localeManager.getConfigManager().getConfig()
                        .getFavoriteRecipes()
                        .stream()
                        .map(FavoriteRecipe::id)
                        .toList()));

        initializeWebSockets();
    }

    private void initializeWebSockets() {
        webSocketService.connect();

        // Listen for individual recipe updates/deletes to keep list consistent
        webSocketService.subscribe("recipe", null, response -> {
            if (response.type() == WebSocketTypes.UPDATE) {
                Recipe recipe = webSocketService.convertData(response.data(), Recipe.class);
                recipeManager.applyRecipeUpdate(recipe);
            } else if (response.type() == WebSocketTypes.DELETE) {
                UUID id = UUID.fromString((String) response.data());
                recipeManager.applyRecipeDelete(id);
            }
        });

        // Listen for individual ingredient updates/deletes
        webSocketService.subscribe("ingredient", null, response -> {
            if (response.type() == WebSocketTypes.UPDATE) {
                Ingredient ingredient = webSocketService.convertData(response.data(), Ingredient.class);
                recipeManager.applyIngredientUpdate(ingredient);
            } else if (response.type() == WebSocketTypes.DELETE) {
                UUID id = UUID.fromString((String) response.data());
                recipeManager.applyIngredientDelete(id);
            }
        });
    }

    private void prepareToggleTheme() {
        Platform.runLater(() -> {
            themeToggle.setText("\u263C");
            setTheme(themeToggle.getScene());
        });
    }

    private void prepareSearchField() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            sidebarListCtrl.setSearchQuery(newValue);
        });

        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                searchField.clear(); // This triggers the listener above with ""
                contentPane.requestFocus(); // Optional: move focus away from search
            }
        });
    }

    /**
     * Shows an alert to the user, describing that a recipe that was previously
     * saved as favorite was deleted, it uses that recipes last saved name in
     * the prompt
     * 
     * @param recipe the recipe that was deleted
     */
    public void showDeletedRecipePrompt(FavoriteRecipe recipe) {
        String message = localeManager
                .getCurrentBundle()
                .getString("txt.recipe_was_deleted")
                .replace("$name", recipe.name());

        var alert = new Alert(Alert.AlertType.WARNING);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Initializes the language filter checkboxes according to the config.
     * If reading from the config fails, resorts to defaulting to all languages.
     * 
     * @param sidebarListCtrl the recipe list controller which is responsible for
     *                        the recipe list
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
        } catch (Exception e) {
            englishFilter.setSelected(true);
            germanFilter.setSelected(true);
            dutchFilter.setSelected(true);
        }
    }

    /**
     * Adds a listener for changes to language filter and favourites checkboxes,
     * so the recipe list viewer can get filtered after a selection of a filter.
     * Also propagates the checkbox changes to the config file.
     * 
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
        onlyShowFavouritesToggle.selectedProperty().addListener((
                observable, oldValue, newValue) -> {
            sidebarListCtrl.toggleOnlyFavourites();
        });
    }

    /**
     * Updates the config file by adding or removing the given language filter.
     * 
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

    private void initializeSidebarListCtrl() {
        recipeToggleButton.setToggleGroup(categoryToggleGroup);
        ingredientToggleButton.setToggleGroup(categoryToggleGroup);
        recipeToggleButton.setSelected(true);
        categoryToggleGroup.selectedToggleProperty().addListener((_, _, newValue) -> {
            if (newValue == recipeToggleButton) {
                sidebarListCtrl.setSidebarMode(ESidebarMode.Recipe);
                cloneButton.setDisable(false);
                favouriteButton.setDisable(false);
                onlyShowFavouritesToggle.setDisable(false);
                englishFilter.setDisable(false);
                dutchFilter.setDisable(false);
                germanFilter.setDisable(false);
            } else if (newValue == ingredientToggleButton) {
                sidebarListCtrl.setSidebarMode(ESidebarMode.Ingredient);
                cloneButton.setDisable(true);
                favouriteButton.setDisable(true);
                onlyShowFavouritesToggle.setDisable(true);
                englishFilter.setDisable(true);
                dutchFilter.setDisable(true);
                germanFilter.setDisable(true);
            }
            showMainScreen();
        });
    }

    /**
     * Prepares the sort-by choice box, by default sorting alphabetically.
     */
    private void prepareSortBy() {
        orderBy.getItems().setAll(
                alphabeticalProperty.get(),
                reverseAlphabeticalProperty.get());
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
            } else if (newValue.equals(reverseAlphabeticalProperty.get())) {
                sidebarListCtrl.setSortMethod("Reverse alphabetical");
            }
        });
        // if the currently shown recipe disappears, close viewer.
        recipeManager.getObservableRecipes()
                .addListener((javafx.collections.ListChangeListener<Recipe>) change -> {
                    while (change.next()) {
                        if (change.wasRemoved() && currentlyShownId != null
                                && sidebarListCtrl.getSidebarMode() == ESidebarMode.Recipe) {
                            boolean stillPresent = recipeManager
                                    .getObservableRecipes()
                                    .stream()
                                    .anyMatch(r -> java.util.Objects.equals(r.getId(), currentlyShownId));
                            if (!stillPresent) {
                                javafx.application.Platform.runLater(() -> {
                                    showMainScreen();
                                    currentlyShownId = null;
                                });
                            }
                        }
                    }
                });
    }

    private void prepareLanguageOptions() {
        languageOptions.setItems(FXCollections.observableArrayList(Language.values()));

        languageOptions.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Language item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(createFlagView(item));
                    setText(null); // Explicitly hide text here
                }
            }
        });

        languageOptions.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Language item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(createFlagView(item));
                    setText(item.proper());
                }
            }
        });

        try {
            String currentCode = localeManager.getCurrentLocale().getLanguage().toUpperCase();
            languageOptions.setValue(Language.valueOf(currentCode));
        } catch (Exception e) {
            languageOptions.setValue(Language.EN);
        }
    }

    private ImageView createFlagView(Language item) {
        String path = "/client/flags/" + item.name().toLowerCase() + ".png";

        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            System.err.println("Could not find resource :" + path);
            return new ImageView();
        }

        Image img = new Image(stream);
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(18);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    @FXML
    private void changeLanguage() {
        Language selected = languageOptions.getValue();
        if (selected == null)
            return;

        // Convert Enum to Locale
        Locale newLocale = new Locale(selected.name().toLowerCase());

        if (!newLocale.equals(localeManager.getCurrentLocale())) {
            localeManager.setAllLocale(newLocale);
        }
    }

    /**
     * Loads Recipe panel
     */
    private void addRecipe() {
        var bundle = localeManager.getCurrentBundle();
        Pair<AddRecipeCtrl, Parent> pair = fxml.load(AddRecipeCtrl.class, bundle,
                "client", "scenes", "AddRecipePanel.fxml");

        AddRecipeCtrl addRecipeCtrl = pair.getKey();
        Parent addRecipeRoot = pair.getValue();

        contentPane.getChildren().setAll(addRecipeRoot);
    }

    private void addIngredient() {
        var bundle = localeManager.getCurrentBundle();
        Pair<EditIngredientCtrl, Parent> pair = fxml.load(EditIngredientCtrl.class, bundle,
                "client", "scenes", "EditIngredient.fxml");

        EditIngredientCtrl ctrl = pair.getKey();
        Parent root = pair.getValue();

        contentPane.getChildren().setAll(root);

        ctrl.setSaveCallback(recipeIngredient -> {
            // showIngredient(recipeManager.getIngredient(recipeIngredient.getIngredientRef()));
            refresh();
        });
    }

    private void showRecipe(Recipe recipe) {
        var bundle = localeManager.getCurrentBundle();
        if (recipe == null) {
            showMainScreen();
            currentlyShownId = null;
            return;
        }

        Pair<RecipeViewerCtrl, Parent> pair = fxml.load(RecipeViewerCtrl.class, bundle,
                "client", "scenes", "RecipeViewer.fxml");

        RecipeViewerCtrl viewerCtrl = pair.getKey();
        viewerCtrl.setOnRecipeEdit(this::editRecipe);
        Parent viewerRoot = pair.getValue();

        viewerCtrl.setRecipe(recipe);

        contentPane.getChildren().setAll(viewerRoot);
        currentlyShownId = recipe.getId();
    }

    private void showIngredient(Ingredient ingredient) {
        var bundle = localeManager.getCurrentBundle();
        if (ingredient == null) {
            showMainScreen();
            currentlyShownId = null;
            return;
        }

        Pair<IngredientViewerCtrl, Parent> pair = fxml.load(IngredientViewerCtrl.class, bundle,
                "client", "scenes", "IngredientViewer.fxml");

        IngredientViewerCtrl viewerCtrl = pair.getKey();
        viewerCtrl.setOnIngredientEdit(this::editIngredient);
        viewerCtrl.setIngredient(ingredient);

        Parent viewerRoot = pair.getValue();

        contentPane.getChildren().setAll(viewerRoot);
        currentlyShownId = ingredient.getId();
    }

    private void editIngredient(Ingredient ingredient) {
        var bundle = localeManager.getCurrentBundle();
        Pair<EditIngredientCtrl, Parent> editIngredientPair = fxml.load(EditIngredientCtrl.class,
                bundle, "client", "scenes", "EditIngredient.fxml");

        EditIngredientCtrl editIngredientCtrl = editIngredientPair.getKey();
        Parent editIngredientRoot = editIngredientPair.getValue();

        editIngredientCtrl.setIngredient(ingredient);
        editIngredientCtrl.setOnShowIngredient(this::showIngredient);

        Parent viewerRoot = editIngredientPair.getValue();
        contentPane.getChildren().setAll(viewerRoot);
        currentlyShownId = ingredient.getId();
    }

    public void showRecipeViewer(Recipe recipe) {
        showRecipe(recipe);
    }

    public void showIngredientViewer(Ingredient ingredient) { showIngredient(ingredient);}

    /**
     * Search field for users to search up items/recipes from ist
     */
    public void search() {
        sidebarListCtrl.setSearchQuery(searchField.getText());
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

    public void refresh() {
        refresh(null);
    }

    /**
     * Refreshes db to get latest data
     */
    public void refresh(Runnable afterRefresh) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (!serverUtils.isServerAvailable()) {
                    throw new Exception("Server is offline");
                }

                List<Recipe> recipes = serverUtils.getRecipes();
                List<Ingredient> ingredients = serverUtils.getIngredients();

                Platform.runLater(() -> {
                    recipeManager.sync(recipes, ingredients);
                    if (afterRefresh != null)
                        afterRefresh.run();
                });
                return null;
            }
        };

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            new Alert(
                    Alert.AlertType.ERROR,
                    syncProperty.get()+ ": " + task.getException().getMessage()).show();
        });

        new Thread(task).start();
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
        TextInputDialog dialog = new TextInputDialog(
                original.getTitle() + " (" + copyProperty.get() + ")"
        );
        dialog.setTitle(cloneRecipeProperty.get());
        dialog.setHeaderText(cloningProperty.get() + ": " + original.getTitle());
        dialog.setContentText(enterCloneNameProperty.get() + ": ");

        var result = dialog.showAndWait();
        if (result.isEmpty())
            return;

        String newName = result.get();

        Recipe clone = original.cloneWithTitle(newName);
        recipeManager.addRecipeOptimistic(clone);

        try {
            recipeManager.setRecipe(clone);
        } catch (Exception e) {
            System.err.println("Failed to save clone to server: " + e.getMessage());
        }

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
        currentlyShownId = null;
    }

    public void showIngredientOverview(List<ShoppingListItem> items) {
        var bundle = localeManager.getCurrentBundle();
        Pair<IngredientOverviewCtrl, Parent> pair = fxml.load(IngredientOverviewCtrl.class, bundle,
                "client", "scenes", "IngredientOverview.fxml");

        IngredientOverviewCtrl ctrl = pair.getKey();
        Parent root = pair.getValue();

        Stage stage = new Stage();
        stage.setTitle(bundle.getString("txt.review_ingredients"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));

        ctrl.setStage(stage);
        ctrl.setItems(items);

        stage.showAndWait();
    }

    @FXML
    private void toggleTheme() {
        Scene scene = themeToggle.getScene();

        setTheme(scene);

        boolean darkMode = themeToggle.isSelected();
        themeToggle.setText(darkMode ? "\u263E" : "\u263C");
    }

    public void setTheme(Scene scene) {
        scene.getStylesheets().clear();

        scene.getStylesheets().add(
                getClass().getResource(getStyleSheetPath()).toExternalForm());
    }

    public String getStyleSheetPath() {
        return themeToggle.isSelected()
                ? "/client/styles/dark.css"
                : "/client/styles/light.css";
    }
}
