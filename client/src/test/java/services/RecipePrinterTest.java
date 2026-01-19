package services;

import client.services.RecipePrinter;
import commons.Amount;
import commons.Language;
import commons.Recipe;
import commons.RecipeIngredient;
import javafx.application.Platform;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class RecipePrinterTest {

    @Start
    public void start(Stage stage) {
        stage.show();
        Platform.setImplicitExit(false);
    }

    @Test
    void printRecipeWithNullDoesNotThrow() {
        assertDoesNotThrow(() ->
                RecipePrinter.printRecipe(null, null, id -> "TestIngredient")
        );
    }

    @Test
    void printableRecipeContainsTitleAndHeaders() throws Exception {
        Printer printer = Printer.getDefaultPrinter();
        Assumptions.assumeTrue(printer != null, "No printer available in CI");

        VBox root = invokePrintableRecipe(
                createRecipe(
                        "Pancakes",
                        List.of("Flour", "Milk"),
                        List.of("Mix", "Bake")
                ),
                printer
        );

        String text = extractAllText(root);
        assertTrue(text.contains("Pancakes"));
        assertTrue(text.contains("Ingredients"));
        assertTrue(text.contains("Preparation"));
    }

    @Test
    void printableIngredientsArePrefixedWithDash() throws Exception {
        Printer printer = Printer.getDefaultPrinter();
        Assumptions.assumeTrue(printer != null, "No printer available in CI");

        VBox root = invokePrintableRecipe(
                createRecipe(
                        "Test",
                        List.of("Salt", "Pepper"),
                        List.of()
                ),
                printer
        );

        String text = extractAllText(root);
        assertTrue(text.contains("- "));
    }

    @Test
    void preparationStepsAreNumbered() throws Exception {
        Printer printer = Printer.getDefaultPrinter();
        Assumptions.assumeTrue(printer != null, "No printer available in CI");

        VBox root = invokePrintableRecipe(
                createRecipe(
                        "Test",
                        List.of(),
                        List.of("Step one", "Step two")
                ),
                printer
        );

        String text = extractAllText(root);
        assertTrue(text.contains("1. Step one"));
        assertTrue(text.contains("2. Step two"));
    }



    private VBox invokePrintableRecipe(Recipe recipe, Printer printer) throws Exception {
        Method method = RecipePrinter.class.getDeclaredMethod(
                "printableRecipe",
                Recipe.class,
                PageLayout.class,
                Function.class
        );
        method.setAccessible(true);

        PageLayout layout = printer.createPageLayout(
                Paper.A4,
                PageOrientation.PORTRAIT,
                Printer.MarginType.DEFAULT
        );


        Function<UUID, String> nameResolver = id -> "TestIngredient";

        Node node = (Node) method.invoke(null, recipe, layout, nameResolver);
        assertInstanceOf(VBox.class, node);
        return (VBox) node;
    }

    private String extractAllText(VBox root) {
        StringBuilder sb = new StringBuilder();

        for (Node node : root.getChildren()) {
            if (node instanceof Label label) {
                sb.append(label.getText());
            } else if (node instanceof Text text) {
                sb.append(text.getText());
            } else if (node instanceof VBox box) {
                sb.append(extractAllText(box));
            }
        }
        return sb.toString();
    }

    private Recipe createRecipe(String title, List<String> ingredientNames, List<String> steps) {
        List<RecipeIngredient> ingredients = ingredientNames.stream()
                .map(name -> new RecipeIngredient(
                        UUID.randomUUID(),
                        new Amount(1, "g")
                ))
                .toList();

        return new Recipe(title, ingredients, steps, 1, Language.EN);
    }
}
