package client.scenes;

import client.config.ConfigManager;
import client.config.FavoriteRecipe;
import client.services.LocaleManager;
import client.services.RecipeManager;
import client.utils.SortUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Language;
import commons.Recipe;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller for the left-hand recipe list.
 * Uses {@link RecipeManager} as the single source of truth for all recipe data.
 */
public class SidebarListCtrl {

    @Inject
    private RecipeManager recipeManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private LocaleManager localeManager;
    private SortUtils recipeSortUtils;
    private SortUtils ingredientsSortUtils;
    private ListView<ListObject> listView;
    private boolean removeMode = false;
    private boolean cloneMode = false;
    private boolean favouriteMode = false;
    private java.util.function.Consumer<Recipe> onCloneRequest;
    private ESidebarMode currentMode = ESidebarMode.Recipe;
    private String currentSearchQuery = "";



    /**
     * Initializes RecipeList controller
     */
    @FXML
    public void initialize() {
        if (recipeSortUtils == null || ingredientsSortUtils == null) {
            initializeSortUtils();
        }
    }

    /**
     * Sets the sidebar mode, if different, and resets the list view
     * @param mode the mode to become
     */
    public void setSidebarMode(ESidebarMode mode) {
        if (currentMode != mode) {
            currentMode = mode;
            setListViewSorted();
        }
    }

    /**
     * Gets the sidebar mode
     * @return mode that is currently set
     */
    public ESidebarMode getSidebarMode() {
        return currentMode;
    }

    /**
     * Initializes SortUtils for sorting and filtering
     */
    private void initializeSortUtils() {
        if (recipeSortUtils == null) initializeRecipeSortUtils();
        if (ingredientsSortUtils == null) initializeIngredientSortUtils();
    }

    /**
     * Initializes SortUtils for sorting using Recipes from the RecipeManager
     */
    private void initializeRecipeSortUtils() {
        // This makes a list which is automatically updated whenever the list of recipes changes.
        var recipesList = recipeManager.getObservableRecipes();
        recipeSortUtils = SortUtils.fromRecipeList(recipesList);
    }

    /**
     * Initializes SortUtils for sorting and filtering
     */
    private void initializeIngredientSortUtils() {
        // This makes a list which is automatically updated whenever the list of recipes changes.
        var ingredientsList = recipeManager.getObservableIngredients();
        ingredientsSortUtils = SortUtils.fromIngredientList(ingredientsList);
    }

    /**
     * Connects this controller to the UI ListView, and binds it to a sorted list of recipes.
     * @param lv the ListView defined in FXML
     */
    public void setListView(ListView<ListObject> lv) {
        this.listView = lv;

        setListViewSorted();

        loadListViewProperties();
    }

    /**
     * Applies functional properties to the UI ListView:
     * <ul>
     *  <li>A cell factory that displays each recipe's title.</li>
     *  <li>A remove on-click handler when in remove mode.</li>
     * </ul>
     */
    private void loadListViewProperties() {
        listView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(ListObject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    boolean fav = recipeManager.isFavourite(item.id());
                    setText((fav ? "★ " : "") + item.name());
                }

            }
        });

        // Handle remove-mode and clone-mode clicks in a single event filter.
        listView.addEventFilter(MouseEvent.MOUSE_CLICKED, ev -> {
            ListObject sel = listView.getSelectionModel().getSelectedItem();
            if (sel == null
                || (!removeMode && !cloneMode && !favouriteMode)) {
                exitRemoveMode();
                exitCloneMode();
                exitFavouriteMode();
                return;
            }
            if (removeMode) {
                switch (currentMode) {
                    case ESidebarMode.Recipe ->
                            recipeManager.removeRecipe(sel.id());
                    case ESidebarMode.Ingredient -> {
                        int usageCount = recipeManager.ingredientUsedIn(sel.id());
                        if (usageCount == 0)
                            recipeManager.removeIngredient(sel.id());
                        else {
                            var alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.initModality(Modality.APPLICATION_MODAL);
                            var text = localeManager
                                            .getCurrentBundle()
                                            .getString("txt.ingredient_deletion_confirmation")
                                            .replace("$num", Integer.toString(usageCount));
                            var label = new Label(text);
                            label.setWrapText(true);
                            alert.getDialogPane().setContent(label);
                            alert.showAndWait();
                            var result = alert.getResult().getButtonData().isDefaultButton();
                            if (result)
                                recipeManager.removeIngredient(sel.id());
                        }
                    }
                }
            }
            else if (cloneMode && onCloneRequest != null && currentMode == ESidebarMode.Recipe)
                onCloneRequest.accept(recipeManager.getRecipe(sel.id()));
            else if (favouriteMode && currentMode == ESidebarMode.Recipe) {
                recipeManager.toggleFavourite(sel.id());
                listView.refresh(); // redraw star
                updateFavourites(new HashSet<>(recipeManager
                        .getFavouriteRecipesSnapshot()
                        .stream()
                        .map(id -> new FavoriteRecipe(id, recipeManager.getRecipe(id).title))
                        .toList()));
                exitFavouriteMode();
            }

            exitRemoveMode();
            exitFavouriteMode();
            exitCloneMode();
            ev.consume();
            listView.getSelectionModel().clearSelection();
        });

    }

    /**
     * Updates the given list of favourite recipe UUIDs to sortUtils
     * and applies filters to the list view, in case any relevant changes were made.
     * Also updates the config to the latest list of favourites.
     * @param favouriteRecipes list of favourite recipe UUIDs
     */
    public void updateFavourites(Set<FavoriteRecipe> favouriteRecipes) {
        if (recipeSortUtils == null || ingredientsSortUtils == null) {
            initializeSortUtils();
        }

        recipeSortUtils.setFavourites(favouriteRecipes.stream().map(FavoriteRecipe::id).toList());

        setListViewSorted();

        configManager.getConfig().setFavoriteRecipes(favouriteRecipes.stream().toList());
        configManager.save();
    }

    /**
     * Binds the current list view items to the RecipeManager's ObservableList
     * (in a sorted manner through SortUtils)
     */
    private void setListViewSorted() {
        if (recipeSortUtils == null || ingredientsSortUtils == null) initializeSortUtils();

        SortUtils currentUtils = (currentMode == ESidebarMode.Recipe) ? recipeSortUtils : ingredientsSortUtils;

        FilteredList<ListObject> filteredList = new FilteredList<>(currentUtils.getList(), item ->
                matchesUtilityFilters(item, currentUtils) && matchesSearchFilter(item)
        );

        SortedList<ListObject> sortedList = new SortedList<>(filteredList);
        sortedList.setComparator((a, b) -> {
            int scoreCompare = compareSearchRelevance(a, b);
            return (scoreCompare != 0) ? scoreCompare : currentUtils.getComparator().compare(a, b);
        });

        listView.setItems(sortedList);
    }

    /**
     * Handles Language and Favourites filters
     **/
    private boolean matchesUtilityFilters(ListObject item, SortUtils utils) {
        boolean langMatch = item.language().isEmpty() || utils.getLanguageFilters().contains(item.language().get());
        boolean favMatch = !utils.isOnlyFavourites() || utils.getFavourites().contains(item.id());
        return langMatch && favMatch;
    }

    /**
     * Handles Search query logic
     **/
    private boolean matchesSearchFilter(ListObject item) {
        if (currentSearchQuery == null || currentSearchQuery.isEmpty()) return true;
        return getScore(item) >= 0;
    }

    /**
     * Compares two items based on search score
     **/
    private int compareSearchRelevance(ListObject a, ListObject b) {
        if (currentSearchQuery == null || currentSearchQuery.isEmpty()) return 0;
        return Integer.compare(getScore(b), getScore(a)); // Descending score
    }

    /**
     * Centralizes score fetching for both modes
     **/
    private int getScore(ListObject item) {
        return (currentMode == ESidebarMode.Recipe)
                ? recipeMatchScore(recipeManager.getRecipe(item.id()), currentSearchQuery)
                : ingredentMatchScore(item.name(), currentSearchQuery);
    }

    private int recipeMatchScore(Recipe recipe, String query) {
        if (query == null || query.isBlank()) return 0;

        int totalScore = 0;
        for (String term : query.toLowerCase().split("\\s+")) {
            int termScore = getSingleTermScore(recipe, term);
            if (termScore == -1) return -1;

            totalScore += termScore;
        }
        return totalScore;
    }

    private int getSingleTermScore(Recipe recipe, String term) {
        boolean isExcluded = term.startsWith("-");

        String actualTerm = getAcutalTerm(term);
        if (actualTerm.isEmpty()) return 0;

        int score = calculateBaseRecipeScore(recipe, actualTerm);

        if (isExcluded) {
            return (score > 0) ? -1 : 0;
        }
        return (score == 0) ? -1 : score;
    }

    private int calculateBaseRecipeScore(Recipe recipe, String actualTerm) {
        int score = 0;

        if (recipe.getTitle().toLowerCase().contains(actualTerm)) score += 100;
        if (recipe.steps.stream().anyMatch(s -> s.toLowerCase().contains(actualTerm))) score += 10;
        if (hasIngredientMatch(recipe, actualTerm)) score += 50;

        return score;
    }

    private boolean hasIngredientMatch(Recipe recipe, String term) {
        return recipe.ingredients.stream().anyMatch(ri -> {
            var ing = recipeManager.getIngredient(ri.ingredientRef);
            return ing != null && ing.getName().toLowerCase().contains(term);
        });
    }

    private int ingredentMatchScore(String ingredientName, String query) {
        if (query == null || query.isBlank()) return 0;

        int totalScore = 0;
        for (String term : query.toLowerCase().split("\\s+")) {
            int termScore = calculateIngredientTermScore(ingredientName.toLowerCase(), term);

            if (termScore == -1) return -1;
            totalScore += termScore;
        }
        return totalScore;
    }

    private int calculateIngredientTermScore(String name, String term) {
        boolean isExcluded = term.startsWith("-");

        String actualTerm = getAcutalTerm(term);
        if (actualTerm.isEmpty()) return 0;

        boolean found = name.contains(actualTerm);
        if (isExcluded) return found ? -1 : 0;
        if (!found) return -1;

        return name.startsWith(actualTerm) ? 50 : 25;
    }

    public void setSearchQuery(String query) {
        this.currentSearchQuery = query.toLowerCase().trim();
        setListViewSorted();
    }

    private String getAcutalTerm(String term) {
        return term.startsWith("-") ? term.substring(1) : term;
    }

    /**
     * Toggles the language filter of the provided language in SortUtils
     * @param language provided language to toggle filter of
     */
    public void toggleLanguageFilter(Language language) {
        if (recipeSortUtils == null || ingredientsSortUtils == null) {
            initializeSortUtils();
        }

        recipeSortUtils.toggleLanguageFilter(language);
        ingredientsSortUtils.toggleLanguageFilter(language);

        setListViewSorted();
    }

    /**
     * Sets SortUtils to use the specified ordering
     * @param sortMethod provided ordering manner
     */
    public void setSortMethod(String sortMethod) {
        if (recipeSortUtils == null || ingredientsSortUtils == null) {
            initializeSortUtils();
        }

        recipeSortUtils.setSortMethod(sortMethod);
        ingredientsSortUtils.setSortMethod(sortMethod);

        setListViewSorted();
    }

    /**
     * Enables remove mode.
     * The next click on a list item will remove it instead of selecting it.
     */
    public void enterRemoveMode() {

        cloneMode = false;
        removeMode = true;
        if (listView != null) listView.requestFocus();
    }
    /**
     * Returns true if the controller is currently in remove mode.
     */
    public boolean isInRemoveMode() {
        return removeMode;
    }

    /**
     * Disables remove mode without removing anything.
     */
    public void exitRemoveMode() {
        removeMode = false;
    }

    public void enterCloneMode() {
        removeMode = false;
        cloneMode = true;
        if (listView != null) listView.requestFocus();
    }

    public void exitCloneMode() {
        cloneMode = false;
    }

    public boolean isInCloneMode() {
        return cloneMode;
    }

    public void setOnRecipeCloneRequest(java.util.function.Consumer<Recipe> callback) {
        this.onCloneRequest = callback;
    }

    public void enterFavouriteMode() {
        removeMode = false;
        cloneMode = false;
        favouriteMode = true;
        if (listView != null) listView.requestFocus();
    }

    private void exitFavouriteMode() {
        favouriteMode = false;
    }

    /**
     * Toggles whether sortUtils filters only favourites.
     */
    public void toggleOnlyFavourites() {
        if (recipeSortUtils == null || ingredientsSortUtils == null) {
            initializeSortUtils();
        }

        recipeSortUtils.setOnlyFavourites(!recipeSortUtils.isOnlyFavourites());

        setListViewSorted();
    }
}
