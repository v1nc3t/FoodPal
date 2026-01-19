package client.scenes;

import client.config.ConfigManager;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Controller for the left-hand recipe list.
 * Uses {@link RecipeManager} as the single source of truth for all recipe data.
 */
public class SidebarListCtrl {

    @Inject
    private RecipeManager recipeManager;
    @Inject
    private ConfigManager configManager;
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
                        try {
                            recipeManager.removeIngredient(sel.id());
                        }
                        catch (Exception ex){
                            var alert = new Alert(Alert.AlertType.ERROR);
                            alert.initModality(Modality.APPLICATION_MODAL);
                            alert.setContentText(ex.getMessage());
                            alert.showAndWait();
                        }
                    }
                }
            }
            else if (cloneMode && onCloneRequest != null && currentMode == ESidebarMode.Recipe)
                onCloneRequest.accept(recipeManager.getRecipe(sel.id()));
            else if (favouriteMode && currentMode == ESidebarMode.Recipe) {
                recipeManager.toggleFavourite(sel.id());
                listView.refresh(); // redraw star
                updateFavourites(recipeManager.getFavouriteRecipesSnapshot());
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
    public void updateFavourites(Set<UUID> favouriteRecipes) {
        if (recipeSortUtils == null || ingredientsSortUtils == null) {
            initializeSortUtils();
        }

        recipeSortUtils.setFavourites(favouriteRecipes.stream().toList());

        setListViewSorted();

        configManager.getConfig().setFavoriteRecipeIDs(favouriteRecipes.stream().toList());
        configManager.save();
    }

    /**
     * Binds the current list view items to the RecipeManager's ObservableList
     * (in a sorted manner through SortUtils)
     */
    private void setListViewSorted() {
        if (recipeSortUtils == null || ingredientsSortUtils == null) {
            initializeSortUtils();
        }

        ObservableList<ListObject> baseList = (currentMode == ESidebarMode.Recipe)
                ? recipeSortUtils.applyFilters()
                : ingredientsSortUtils.applyFilters();

        listView.setItems(createSortedFileteredList(baseList, currentSearchQuery));
    }

    private SortedList<ListObject> createSortedFileteredList(
            ObservableList<ListObject> baseList,
            String query) {
        FilteredList<ListObject> filteredList = new FilteredList<>(baseList, item -> {
            if (query == null || query.isEmpty()) return true;

            if (currentMode == ESidebarMode.Recipe) {
                Recipe recipe = recipeManager.getRecipe(item.id());
                return recipeMatchScore(recipe, query) >= 0;
            } else {
                return ingredentMatchScore(item.name(), query) >= 0;
            }
        });

        SortedList<ListObject> sortedList = new SortedList<>(filteredList);

        sortedList.setComparator((a, b) -> {
            if (query == null || query.isEmpty()) {
                return a.name().compareToIgnoreCase(b.name());
            }

            int scoreA, scoreB;
            if (currentMode == ESidebarMode.Recipe) {
                scoreA = recipeMatchScore(recipeManager.getRecipe(a.id()), query);
                scoreB = recipeMatchScore(recipeManager.getRecipe(b.id()), query);
            } else {
                scoreA = ingredentMatchScore(a.name(), query);
                scoreB = ingredentMatchScore(b.name(), query);
            }

            if (scoreA != scoreB) {
                return Integer.compare(scoreB, scoreA);
            }
            return a.name().compareToIgnoreCase(b.name());
        });

        return sortedList;
    }

    private int recipeMatchScore(Recipe recipe, String query) {
        if (query == null || query.isBlank()) return 0;

        String[] terms = query.toLowerCase().split("\\s+");

        int totalScore = 0;
        for (String term : terms) {
            boolean isExcluded = term.startsWith("-");
            String actualTerm = isExcluded ? term.substring(1) : term;
            if (actualTerm.isEmpty()) continue;

            int termScore = 0;

            if (recipe.getTitle().toLowerCase().contains(actualTerm)) {
                termScore += 100;
            }
            if (recipe.steps.stream()
                    .anyMatch(step -> step.toLowerCase().contains(actualTerm))) {
                termScore += 10;
            }
            if (recipe.ingredients.stream()
                    .anyMatch(ri -> {
                        var ingredient = recipeManager.getIngredient(ri.ingredientRef);
                        return ingredient != null
                                && ingredient.getName().toLowerCase().contains(actualTerm);
                    })) {
                termScore += 50;
            }

            if (isExcluded) {
                if (termScore > 0) return -1;
            } else {
                if (termScore == 0) return -1;
                totalScore += termScore;
            }
        }
        return totalScore;
    }

    private int ingredentMatchScore(String ingredientName, String query) {
        if (query == null || query.isBlank()) return 0;

        String[] terms = query.toLowerCase().split("\\s+");

        int totalScore = 0;
        for(String term : terms) {
            boolean isExcluded = term.startsWith("-");

            String actualTerm = isExcluded ? term.substring(1) : term;
            if (actualTerm.isEmpty()) continue;

            boolean found = ingredientName.toLowerCase().contains(actualTerm);

            if (isExcluded && found) return -1;
            if (!isExcluded && !found) return -1;

            if (found) {
                totalScore += ingredientName.toLowerCase().startsWith(actualTerm) ? 50 : 25;
            }
        }
        return totalScore;
    }

    public void setSearchQuery(String query) {
        this.currentSearchQuery = query.toLowerCase().trim();
        setListViewSorted();
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
     * Adds a recipe to the list using the RecipeManager.
     * Uses an optimistic add, meaning the recipe appears immediately in the UI.
     *
     * @param r the recipe to add
     */
    public void addRecipe(Recipe r) {
        if (r == null) return;
        recipeManager.addRecipeOptimistic(r);
    }

    /**
     * Removes a recipe from the list using the RecipeManager.
     *
     * @param r the recipe to remove
     */
    public void removeRecipe(Recipe r) {
        if (r == null) return;
        recipeManager.removeRecipe(r.getId());
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

    /**
     * Returns a plain Java snapshot of all recipes currently displayed.
     * Useful for testing, logging, or non-JavaFX logic.
     *
     * @return an immutable snapshot of recipes
     */
    public List<Recipe> getRecipesSnapshot() {
        return recipeManager.getRecipesSnapshot();
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
