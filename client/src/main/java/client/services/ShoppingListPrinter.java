package client.services;

import client.shoppingList.ShoppingListItem;
import commons.Ingredient;
import javafx.geometry.Insets;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Window;

import java.util.List;

public class ShoppingListPrinter {

    /**
     * Opens print dialog and prints the shopping list
     * 
     * @param items         list of items to print
     * @param recipeManager need to look up ingredient names
     * @param owner         owner window
     */
    public static void printShoppingList(List<ShoppingListItem> items, RecipeManager recipeManager, Window owner) {
        if (items == null || items.isEmpty()) {
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            System.out.println("No printer job created");
            return;
        }

        boolean proceed = job.showPrintDialog(owner);
        if (!proceed) {
            return;
        }

        Printer printer = job.getPrinter();
        PageLayout pageLayout = printer.createPageLayout(
                Paper.A4,
                PageOrientation.PORTRAIT,
                Printer.MarginType.DEFAULT);
        job.getJobSettings().setPageLayout(pageLayout);

        Node node = createPrintableNode(items, recipeManager, pageLayout);

        boolean success = job.printPage(pageLayout, node);
        if (success) {
            job.endJob();
        }
    }

    private static Node createPrintableNode(List<ShoppingListItem> items, RecipeManager recipeManager,
            PageLayout pageLayout) {
        double contentWidth = pageLayout.getPrintableWidth() - 40;

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setPrefWidth(contentWidth);
        root.setMaxWidth(contentWidth);

        Label title = new Label("Shopping List");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));

        root.getChildren().add(title);

        for (ShoppingListItem item : items) {

            String line = "- ";
            if (item.getCustomName() != null) {
                line += item.getCustomName();
            } else {
                Ingredient ing = recipeManager
                        .getIngredient(new commons.RecipeIngredient(item.getIngredientId(), item.getAmount()));
                if (ing != null) {
                    line += ing.getName();
                } else {
                    line += "Unknown Ingredient";
                }
            }

            if (item.getAmount() != null) {
                line += " (" + item.getAmount().toPrettyString() + ")";
            }

            Text text = new Text(line);
            text.setFont(Font.font("Monospaced", 12));
            text.setWrappingWidth(contentWidth);
            root.getChildren().add(text);
        }

        return root;
    }
}
