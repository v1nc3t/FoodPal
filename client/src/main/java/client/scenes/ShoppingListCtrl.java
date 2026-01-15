package client.scenes;

import client.services.ShoppingListManager;
import client.services.ShoppingListPrinter;
import client.services.RecipeManager;
import client.services.LocaleManager;
import client.shoppingList.ShoppingListItem;
import com.google.inject.Inject;
import commons.Amount;
import commons.Ingredient;
import commons.RecipeIngredient;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.util.Locale;
import java.util.ResourceBundle;

public class ShoppingListCtrl implements Internationalizable {

    @FXML
    private Label titleLabel;
    @FXML
    private ListView<ShoppingListItem> shoppingListView;
    @FXML
    private Button clearButton;
    @FXML
    private Button printButton;
    @FXML
    private Button addManualButton; // Future extension

    private final ShoppingListManager shoppingListManager;
    private final RecipeManager recipeManager;
    private final LocaleManager localeManager;

    @Inject
    public ShoppingListCtrl(ShoppingListManager shoppingListManager,
            RecipeManager recipeManager,
            LocaleManager localeManager) {
        this.shoppingListManager = shoppingListManager;
        this.recipeManager = recipeManager;
        this.localeManager = localeManager;
        localeManager.register(this);
    }

    @FXML
    public void initialize() {
        shoppingListView.setItems(shoppingListManager.getItems());
        shoppingListView.setCellFactory(param -> new ShoppingListCell());

        setLocale(localeManager.getCurrentLocale());
    }

    @FXML
    public void clearList() {
        shoppingListManager.clear();
    }

    @FXML
    public void printList() {
        ShoppingListPrinter.printShoppingList(shoppingListManager.getItems(), recipeManager,
                shoppingListView.getScene().getWindow());
    }

    @FXML
    public void addManualItem() {
        javafx.scene.control.TextInputDialog nameDialog = new javafx.scene.control.TextInputDialog();
        nameDialog.setTitle("Add Item");
        nameDialog.setHeaderText("Enter item name:");
        nameDialog.setContentText("Name:");

        nameDialog.showAndWait().ifPresent(name -> {
            javafx.scene.control.TextInputDialog amountDialog = new javafx.scene.control.TextInputDialog("1");
            amountDialog.setTitle("Add Item");
            amountDialog.setHeaderText("Enter amount (e.g., '1 kg', '2 pieces'):");
            amountDialog.setContentText("Amount:");

            amountDialog.showAndWait().ifPresent(amountStr -> {
                int quantity = 1;
                String unit = amountStr;

                String[] parts = amountStr.split(" ", 2);
                if (parts.length == 2) {
                    try {
                        quantity = Integer.parseInt(parts[0]);
                        unit = parts[1];
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                } else if (parts.length == 1) {
                    try {
                        quantity = Integer.parseInt(parts[0]);
                        unit = "";
                    } catch (NumberFormatException e) {
                        unit = amountStr;
                    }
                }

                shoppingListManager.addManualItem(name, new Amount(quantity, unit));
            });
        });
    }

    @Override
    public void setLocale(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(localeManager.getBundleName(), locale);
        titleLabel.setText(bundle.getString("txt.shopping_list"));
        clearButton.setText(bundle.getString("txt.clear"));
        addManualButton.setText(bundle.getString("txt.add_item"));

        if (bundle.containsKey("txt.print")) {
            printButton.setText(bundle.getString("txt.print"));
        } else {
            printButton.setText("Print");
        }
        shoppingListView.refresh();
    }

    private class ShoppingListCell extends ListCell<ShoppingListItem> {
        @Override
        protected void updateItem(ShoppingListItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                HBox root = new HBox(10);
                root.setAlignment(Pos.CENTER_LEFT);

                String name = "Unknown";
                if (item.getCustomName() != null) {
                    name = item.getCustomName();
                } else {
                    Ingredient ing = recipeManager
                            .getIngredient(new RecipeIngredient(item.getIngredientId(), item.getAmount()));
                    if (ing != null) {
                        name = ing.getName();
                    }
                }

                if (item.getSourceRecipeName() != null) {
                    name += " (from " + item.getSourceRecipeName() + ")";
                }

                Label nameLabel = new Label(name);
                Label amountLabel = new Label(item.getAmount() != null ? item.getAmount().toPrettyString() : "");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                ResourceBundle bundle = ResourceBundle.getBundle(localeManager.getBundleName(),
                        localeManager.getCurrentLocale());
                Button editBtn = new Button(bundle.getString("txt.edit"));
                editBtn.setOnAction(e -> editItem(item));

                Button removeBtn = new Button("-");
                removeBtn.setOnAction(e -> shoppingListManager.removeItem(item));

                root.getChildren().addAll(nameLabel, amountLabel, spacer, editBtn, removeBtn);
                setGraphic(root);
            }
        }
    }

    private void editItem(ShoppingListItem item) {
        String currentName = getDisplayName(item);

        javafx.scene.control.TextInputDialog nameDialog = new javafx.scene.control.TextInputDialog(
                currentName != null ? currentName : "");
        nameDialog.setTitle("Edit Item");
        nameDialog.setHeaderText("Edit item name:");
        nameDialog.setContentText("Name:");

        nameDialog.showAndWait().ifPresent(newName -> showAmountDialog(item, currentName, newName));
    }

    private String getDisplayName(ShoppingListItem item) {
        if (item.getCustomName() != null) {
            return item.getCustomName();
        }
        Ingredient ing = recipeManager.getIngredient(new RecipeIngredient(item.getIngredientId(), item.getAmount()));
        return ing != null ? ing.getName() : null;
    }

    private void showAmountDialog(ShoppingListItem item, String currentName, String newName) {
        String currentAmount = item.getAmount() != null ? item.getAmount().toPrettyString() : "1";

        javafx.scene.control.TextInputDialog amountDialog = new javafx.scene.control.TextInputDialog(currentAmount);
        amountDialog.setTitle("Edit Item");
        amountDialog.setHeaderText("Edit amount:");
        amountDialog.setContentText("Amount:");

        amountDialog.showAndWait()
                .ifPresent(newAmountStr -> updateItemDetails(item, currentName, newName, newAmountStr));
    }

    private void updateItemDetails(ShoppingListItem item, String currentName, String newName, String newAmountStr) {
        int quantity = 1;
        String unit = newAmountStr;

        String[] parts = newAmountStr.split(" ", 2);
        if (parts.length == 2) {
            try {
                quantity = Integer.parseInt(parts[0]);
                unit = parts[1];
            } catch (NumberFormatException e) {
                // ignore
            }
        } else if (parts.length == 1) {
            try {
                quantity = Integer.parseInt(parts[0]);
                unit = "";
            } catch (NumberFormatException e) {
                unit = newAmountStr;
            }
        }

        ShoppingListItem newItem;
        if (item.getIngredientId() != null && newName.equals(currentName)) {
            newItem = new ShoppingListItem(item.getIngredientId(), new Amount(quantity, unit),
                    item.getSourceRecipeId(), item.getSourceRecipeName());
        } else {
            newItem = new ShoppingListItem(newName, new Amount(quantity, unit));
        }

        shoppingListManager.updateItem(item, newItem);
    }
}
