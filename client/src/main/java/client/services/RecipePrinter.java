package client.services;

import commons.Recipe;
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

public class RecipePrinter {

    /**
     * Opens print dialog and prints the current recipe
     * @param recipe recipe that is printed
     * @param owner owner window for printing dialog
     */
    public static void printRecipe(Recipe recipe, Window owner) {
        if (recipe == null){
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if(job == null){
            System.out.println("Nothing to print");
            return;
        }

        boolean proceed = job.showPrintDialog(owner);
        if(!proceed){
            return;
        }
        Printer printer = job.getPrinter();
        PageLayout pageLayout = printer.createPageLayout(
            Paper.A4,
            PageOrientation.PORTRAIT,
            Printer.MarginType.DEFAULT
        );
        job.getJobSettings().setPageLayout(pageLayout);

        Node print = printableRecipe(recipe, pageLayout);

        boolean success = job.printPage(pageLayout, print);
        if (success) {
            job.endJob();
        }
    }

    /**
     * This creates a formatted recipe that can be printed
     * @param recipe the recipe
     * @param pageLayout the page layout.
     * @return node that containing formatted recipe
     */
    private static Node printableRecipe(Recipe recipe, PageLayout pageLayout){
        double contentWidth = pageLayout.getPrintableWidth() - 80;

        VBox root = new VBox(8);

        root.setPadding(new Insets(20));
        root.setPrefWidth(contentWidth);
        root.setMaxWidth(contentWidth);

        Label title = getTitle(recipe, contentWidth);
        Label ingredientsTitle = getIngredientsTitle(contentWidth);
        VBox ingredientsBox = getIngredientsBox(recipe, contentWidth);
        Label preparationTitle = getPreparationTitle(contentWidth);
        VBox preparationBox = getPreparationBox(recipe, contentWidth);

        root.getChildren().addAll(
            title,
            new Text(""),
            ingredientsTitle,
            ingredientsBox,
            new Text(""),
            preparationTitle,
            preparationBox);

        return root;
    }

    /**
     * This gets us the title of current recipe
     * @param recipe recipe
     * @param contentWidth max width for the label.
     * @return formatted recipe title
     */
    private static Label getTitle(Recipe recipe, double contentWidth) {
        Label title = new Label(recipe.getTitle());
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 15));
        title.setAlignment(Pos.CENTER);
        title.setWrapText(true);
        title.setPrefWidth(contentWidth);
        return title;
    }

    /**
     * This gets us the ingredientsTitle
     * @param contentWidth max width for the label
     * @return formatted ingredient title.
     */
    private static Label getIngredientsTitle(double contentWidth) {
        Label ingredientsTitle = new Label("Ingredients: ");
        ingredientsTitle.setFont(Font.font("Monospaced", FontWeight.BOLD, 15));
        ingredientsTitle.setPrefWidth(contentWidth);
        return ingredientsTitle;
    }

    /**
     * This gets us the ingredients for the current recipe in formatted way
     * @param recipe recipe
     * @param contentWidth max width for the label.
     * @return returns ingredients in formatted way
     */
    private static VBox getIngredientsBox(Recipe recipe, double contentWidth) {
        VBox ingredientsBox = new VBox(4);
        if (recipe.getIngredients() != null) {
            for (Object ingredient : recipe.getIngredients()) {
                Text text = new Text("- " + ingredient);
                text.setWrappingWidth(contentWidth);
                ingredientsBox.getChildren().add(text);
            }
        }
        return ingredientsBox;
    }

    /**
     * This gets us the Preparation title in formatted way
     * @param contentWidth max width for the label.
     * @return formatted preparationtitle
     */
    private static Label getPreparationTitle(double contentWidth) {
        Label preparationTitle = new Label("Preparation: ");
        preparationTitle.setFont(Font.font("Monospaced", FontWeight.BOLD, 15));
        preparationTitle.setPrefWidth(contentWidth);
        return preparationTitle;
    }

    /**
     * Gets the preparation steps for the current recipe in formatted way
     * @param recipe recipe
     * @param contentWidth max width for the label.
     * @return preparation steps in formatted way
     */
    private static VBox getPreparationBox(Recipe recipe, double contentWidth) {
        VBox preparationBox = new VBox(4);
        if(recipe.getSteps() != null){
            for(int i = 0; i < recipe.getSteps().size(); i++){
                String text = (i + 1) + ". " + recipe.getSteps().get(i);

                Label label = new Label(text);
                label.setWrapText(true);
                label.setPrefWidth(contentWidth);

                preparationBox.getChildren().add(label);
            }
        }
        return preparationBox;
    }
}