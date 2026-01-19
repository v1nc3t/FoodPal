package client.services;

import commons.Amount;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Window;

import java.util.UUID;
import java.util.function.Function;

public class RecipePrinter {

    /**
     * Opens print dialog and prints the current recipe
     *
     * @param recipe recipe that is printed
     * @param owner owner window for printing dialog
     * @param ingredientNameResolver resolves ingredient UUID to name
     */
    public static void printRecipe(
            Recipe recipe,
            Window owner,
            Function<UUID, String> ingredientNameResolver
    ) {
        if (recipe == null) {
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            System.out.println("Nothing to print");
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
                Printer.MarginType.DEFAULT
        );
        job.getJobSettings().setPageLayout(pageLayout);

        Node print = printableRecipe(recipe, pageLayout, ingredientNameResolver);

        boolean success = job.printPage(pageLayout, print);
        if (success) {
            job.endJob();
        }
    }


    private static Node printableRecipe(
            Recipe recipe,
            PageLayout pageLayout,
            Function<UUID, String> ingredientNameResolver
    ) {
        double contentWidth = pageLayout.getPrintableWidth() - 80;

        VBox root = new VBox(8);
        root.setPadding(new Insets(20));
        root.setPrefWidth(contentWidth);
        root.setMaxWidth(contentWidth);

        Label title = getTitle(recipe, contentWidth);
        Label ingredientsTitle = getIngredientsTitle(contentWidth);
        VBox ingredientsBox = getIngredientsBox(recipe, contentWidth, ingredientNameResolver);
        Label preparationTitle = getPreparationTitle(contentWidth);
        VBox preparationBox = getPreparationBox(recipe, contentWidth);

        root.getChildren().addAll(
                title,
                new Text(""),
                ingredientsTitle,
                ingredientsBox,
                new Text(""),
                preparationTitle,
                preparationBox
        );

        return root;
    }

    private static Label getTitle(Recipe recipe, double contentWidth) {
        Label title = new Label(recipe.getTitle());
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 15));
        title.setAlignment(Pos.CENTER);
        title.setWrapText(true);
        title.setPrefWidth(contentWidth);
        return title;
    }

    private static Label getIngredientsTitle(double contentWidth) {
        Label ingredientsTitle = new Label("Ingredients:");
        ingredientsTitle.setFont(Font.font("Monospaced", FontWeight.BOLD, 15));
        ingredientsTitle.setPrefWidth(contentWidth);
        return ingredientsTitle;
    }


    private static VBox getIngredientsBox(
            Recipe recipe,
            double contentWidth,
            Function<UUID, String> ingredientNameResolver
    ) {
        VBox ingredientsBox = new VBox(4);

        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                Amount amount = ingredient.getAmount();
                String name = ingredientNameResolver.apply(ingredient.getIngredientRef());

                String line;
                if (amount.unit() != null) {
                    line = "- " + name + " " +
                            formatQuantity(amount.quantity()) + " " +
                            formatUnit(amount.unit());
                } else {
                    line = "- " + name + " " + amount.description();
                }

                Text text = new Text(line);
                text.setWrappingWidth(contentWidth);
                ingredientsBox.getChildren().add(text);
            }
        }

        return ingredientsBox;
    }

    private static Label getPreparationTitle(double contentWidth) {
        Label preparationTitle = new Label("Preparation:");
        preparationTitle.setFont(Font.font("Monospaced", FontWeight.BOLD, 15));
        preparationTitle.setPrefWidth(contentWidth);
        return preparationTitle;
    }

    private static VBox getPreparationBox(Recipe recipe, double contentWidth) {
        VBox preparationBox = new VBox(4);

        if (recipe.getSteps() != null) {
            for (int i = 0; i < recipe.getSteps().size(); i++) {
                String text = (i + 1) + ". " + recipe.getSteps().get(i);

                Label label = new Label(text);
                label.setWrapText(true);
                label.setPrefWidth(contentWidth);

                preparationBox.getChildren().add(label);
            }
        }

        return preparationBox;
    }



    private static String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((int) quantity);
        }
        return String.format("%.1f", quantity);
    }

    private static String formatUnit(Unit unit) {
        return switch (unit) {
            case KILOGRAM -> "kg";
            case GRAM -> "g";
            case LITER -> "l";
            case MILLILITER -> "ml";
            default -> unit.name().toLowerCase();
        };
    }
}
