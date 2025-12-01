package client.scenes;

import commons.Recipe;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.print.Paper;

import java.util.List;

public class RecipeViewerCtrl {

    @FXML
    private Label titleLabel;
    @FXML
    private ListView<String> ingredientsList;
    @FXML
    private ListView<String> preparationList;
    @FXML
    private Button editButton;
    @FXML
    private Button printButton;

    private Recipe currentRecipe;

    private MainApplicationCtrl mainCtrl;

  /**
   * Called by MainApplicationCtrl after loading this FXML.
   * Allows the viewer to call back into the main controller.
   *
   * @param mainCtrl the main application controller
   */
    public void setMainCtrl(MainApplicationCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

  /**
   * This sets the values inside the Recipe viewer
   * @param recipe the recipe
   */
    public void setRecipe(Recipe recipe){
        this.currentRecipe = recipe;

        if(recipe == null){
            titleLabel.setText("");
            ingredientsList.getItems().clear();
            preparationList.getItems().clear();
            return;
        }

        titleLabel.setText(recipe.getTitle());
        setIngredientsList(recipe);
        setPreparationList(recipe);
    }

  /**
   * This sets the ingredients
   * @param recipe the recipe
   */
    private void setIngredientsList(Recipe recipe) {
        ingredientsList.getItems().clear();
        List<?> ingredients = recipe.getIngredients();
        if (ingredients != null) {
            for (Object ingredient : ingredients) {
                ingredientsList.getItems().add(String.valueOf(ingredient));
            }
        }
    }

  /**
   * This sets the steps
   * @param recipe the recipe
   */
    private void setPreparationList(Recipe recipe) {
        preparationList.getItems().clear();
        List<String> steps = recipe.getSteps();
        if (steps != null) {
            preparationList.getItems().addAll(steps);
        }
    }

  /**
   * Opens the AddRecipe panel pre-filled with the currently viewed recipe.
   */
    @FXML
    private void editRecipe() {
        if (mainCtrl != null && currentRecipe != null) {
            mainCtrl.editRecipe(currentRecipe);
        }
    }

  /**
   * Opens print dialog and prints the current recipe
   */
    @FXML
      private void printRecipe(){
        if (currentRecipe == null){
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if(job == null){
            System.out.println("Nothing to print");
            return;
        }

        boolean proceed = job.showPrintDialog(titleLabel.getScene().getWindow());
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

        Node print = printableRecipe(pageLayout);

        boolean success = job.printPage(pageLayout, print);
        if (success) {
            job.endJob();
        }
    }

    /**
     * This creates a formatted recipe that can be printed
     * @param pageLayout the page layout.
     * @return node that containing formatted recipe
     */
    private Node printableRecipe(PageLayout pageLayout){
        double contentWidth = pageLayout.getPrintableWidth() - 80;

        VBox root = new VBox(8);

        root.setPadding(new Insets(20));
        root.setPrefWidth(contentWidth);
        root.setMaxWidth(contentWidth);

        Label title = getTitle(contentWidth);

        Label ingredientsTitle = getIngredientsTitle(contentWidth);

        VBox ingredientsBox = getIngredientsBox(pageLayout);

        Label preparationTitle = getPreparationTitle(contentWidth);

        VBox preparationBox = getPreparationBox(contentWidth);

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
     * @param contentWidth max width for the label.
     * @return formatted recipe title
     */
    private Label getTitle(double contentWidth) {
        Label title = new Label(currentRecipe.getTitle());
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
     * @param pageLayout page layout
     * @return returns ingredients in formatted way
     */
    private VBox getIngredientsBox(PageLayout pageLayout) {
        VBox ingredientsBox = new VBox(4);
        if (currentRecipe.getIngredients() != null) {
            for (Object ingredient : currentRecipe.getIngredients()) {
                Text text = new Text("- " + ingredient);
                text.setWrappingWidth(pageLayout.getPrintableWidth());
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
     * @param contentWidth max width for the label.
     * @return preparation steps in formatted way
     */
    private VBox getPreparationBox(double contentWidth) {
        VBox preparationBox = new VBox(4);
        if(currentRecipe.getSteps() != null){
            for(int i = 0; i < currentRecipe.getSteps().size(); i++){
                String text = (i + 1) + ". " + currentRecipe.getSteps().get(i);

                Label label = new Label(text);
                label.setWrapText(true);
                label.setPrefWidth(contentWidth);

                preparationBox.getChildren().add(label);
            }
        }
        return preparationBox;
    }
}