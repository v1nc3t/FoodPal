package client.scenes;

import client.services.*;
import client.shoppingList.ShoppingListItem;
import com.google.inject.Inject;
import commons.Amount;
import commons.Ingredient;
import commons.RecipeIngredient;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.Locale;
import java.util.Optional;
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
    private Button addManualButton;

    private final StringProperty titleProperty = new SimpleStringProperty();
    private final StringProperty clearProperty = new SimpleStringProperty();
    private final StringProperty addManualProperty = new SimpleStringProperty();
    private final StringProperty printProperty = new SimpleStringProperty();
    private final StringProperty nameProperty = new SimpleStringProperty();
    private final StringProperty amountProperty = new SimpleStringProperty();
    private final StringProperty editProperty = new SimpleStringProperty();
    private final StringProperty itemProperty = new SimpleStringProperty();
    private final StringProperty confirmDeleteProperty = new SimpleStringProperty();
    private final StringProperty shoppingListProperty = new SimpleStringProperty();
    private final StringProperty sureProperty = new SimpleStringProperty();
    private final StringProperty yesProperty = new SimpleStringProperty();
    private final StringProperty noProperty = new SimpleStringProperty();

    private String addItemDialogHeader;
    private String addAmountDialogHeader;

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
        bindProperties();
        shoppingListView.setItems(shoppingListManager.getItems());
        shoppingListView.setCellFactory(param -> new ShoppingListCell());

        setLocale(localeManager.getCurrentLocale());
    }

    @FXML
    public void clearList() {
        ButtonType deleteButton = new ButtonType(
                yesProperty.get(), ButtonBar.ButtonData.OK_DONE
        );
        ButtonType cancelButton = new ButtonType(
                noProperty.get(), ButtonBar.ButtonData.CANCEL_CLOSE
        );

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(confirmDeleteProperty.get());
        alert.setHeaderText(clearProperty.get() + " " + shoppingListProperty.get());
        alert.setContentText(sureProperty.get());

        alert.getButtonTypes().setAll(deleteButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            shoppingListManager.clear();
        }
    }

    @FXML
    public void printList() {
        String text = ShoppingListTextFormatter.toText(
                shoppingListManager.getItems(),
                recipeManager
        );

        TextFileExporter.save(
                text,
                shoppingListView.getScene().getWindow()
        );
    }


    @FXML
    public void addManualItem() {
        javafx.scene.control.TextInputDialog nameDialog =
                new javafx.scene.control.TextInputDialog();
        nameDialog.setTitle(addManualProperty.get());
        nameDialog.setHeaderText(addItemDialogHeader);
        nameDialog.setContentText(nameProperty.get() + ":");

        nameDialog.showAndWait().ifPresent(name -> {
            javafx.scene.control.TextInputDialog amountDialog =
                    new javafx.scene.control.TextInputDialog("1");
            amountDialog.setTitle(addManualProperty.get());
            amountDialog.setHeaderText(addAmountDialogHeader);
            amountDialog.setContentText(amountProperty.get() + ":");

            amountDialog.showAndWait().ifPresent(amountStr -> {
                double quantity = 1;
                String unit = amountStr;

                String[] parts = amountStr.split(" ");
                if (parts.length == 2) {
                    try {
                        quantity = Double.parseDouble(parts[0]);
                        unit = parts[1];
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                } else if (parts.length == 1) {
                    try {
                        quantity = Double.parseDouble(parts[0]);
                        unit = "";
                    } catch (NumberFormatException e) {
                        unit = amountStr;
                    }
                }

                shoppingListManager.addManualItem(name, new Amount(quantity, unit));
            });
        });
    }

    private void bindProperties() {
        titleLabel.textProperty().bind(titleProperty);
        clearButton.textProperty().bind(clearProperty);
        addManualButton.textProperty().bind(addManualProperty);
        printButton.textProperty().bind(printProperty);
    }

    @Override
    public void setLocale(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(localeManager.getBundleName(), locale);
        titleProperty.set(bundle.getString("txt.shopping_list"));
        clearProperty.set(bundle.getString("txt.clear"));
        addManualProperty.set(bundle.getString("txt.add_item"));
        printProperty.set(bundle.getString("txt.print"));
        nameProperty.set(bundle.getString("txt.name"));
        amountProperty.set(bundle.getString("txt.amount"));
        editProperty.set(bundle.getString("txt.edit"));
        itemProperty.set(bundle.getString("txt.item"));
        confirmDeleteProperty.set(bundle.getString("txt.confirm_delete"));
        shoppingListProperty.set(bundle.getString("txt.shopping_list"));
        sureProperty.set(bundle.getString("txt.sure"));
        yesProperty.set(bundle.getString("txt.yes"));
        noProperty.set(bundle.getString("txt.no"));

        addItemDialogHeader = bundle.getString("txt.add_item_dialog_header");
        addAmountDialogHeader = bundle.getString("txt.add_amount_dialog_header");

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
                            .getIngredient(new RecipeIngredient(item.getIngredientId(),
                                    item.getAmount()));
                    if (ing != null) {
                        name = ing.getName();
                    }
                }

                if (item.getSourceRecipeName() != null) {
                    name += " (from " + item.getSourceRecipeName() + ")";
                }

                Label nameLabel = new Label(name);
                Label amountLabel = new Label(item.getAmount() != null ?
                        item.getAmount().toPrettyString() : "");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                ResourceBundle bundle = ResourceBundle.getBundle(localeManager.getBundleName(),
                        localeManager.getCurrentLocale());
                Button editBtn = new Button(editProperty.get());
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
        nameDialog.setTitle(editProperty.get() + " " + itemProperty.get());
        nameDialog.setHeaderText(editProperty.get() + " " + itemProperty.get() + nameProperty.get() + ":");
        nameDialog.setContentText(nameProperty.get() + ":");

        nameDialog.showAndWait().ifPresent(newName -> showAmountDialog(item, currentName, newName));
    }

    private String getDisplayName(ShoppingListItem item) {
        if (item.getCustomName() != null) {
            return item.getCustomName();
        }
        Ingredient ing = recipeManager.getIngredient(new RecipeIngredient(item.getIngredientId(),
                item.getAmount()));
        return ing != null ? ing.getName() : null;
    }

    private void showAmountDialog(ShoppingListItem item, String currentName, String newName) {
        String currentAmount = item.getAmount() != null ? item.getAmount().toPrettyString() : "1";

        javafx.scene.control.TextInputDialog amountDialog =
                new javafx.scene.control.TextInputDialog(currentAmount);
        amountDialog.setTitle(editProperty.get() + " " + itemProperty.get());
        amountDialog.setHeaderText(editProperty.get() + " " + amountProperty.get() + ":");
        amountDialog.setContentText(amountProperty.get() + ":");

        amountDialog.showAndWait()
                .ifPresent(newAmountStr ->
                        updateItemDetails(item, currentName, newName, newAmountStr));
    }

    private void updateItemDetails(ShoppingListItem item, String currentName,
                                   String newName, String newAmountStr) {
        double quantity = 1;
        String unit = newAmountStr;

        String[] parts = newAmountStr.split(" ");
        if (parts.length == 2) {
            try {
                quantity = Double.parseDouble(parts[0]);
                unit = parts[1];
            } catch (NumberFormatException e) {
                // ignore
            }
        } else if (parts.length == 1) {
            try {
                quantity = Double.parseDouble(parts[0]);
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
