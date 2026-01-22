package client.utils;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFieldUtils {

    /**
     * Extracts a non-empty string from a field
     * @param textField user input
     * @param label to customize error message
     * @return a string of user input
     * @throws IllegalArgumentException when input is null
     */
    public static String getStringFromField(TextField textField, Label label, StringProperty errorProperty) {
        String text = textField.getText();
        if (text.isEmpty()) {
            String message = label.getText() + " " + errorProperty.get();

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(message);
            alert.showAndWait();

            throw new IllegalArgumentException(label.getText() + " cannot be empty");
        }
        return text;
    }

    /**
     * Extracts a non-null positive integer from a field
     * @param textField user input
     * @param label to customize error message
     * @return a positive integer of user input
     * @throws NumberFormatException when input is null, non-positive, or not an integer
     */
    public static int getPositiveIntFromField(TextField textField, Label label, StringProperty errorProperty) {
        String text = textField.getText();

        try {
            int input = Integer.parseInt(text);
            if (input > 0) {
                return input;
            }
            else {
                throw new NumberFormatException("Input not in a valid format.");
            }
        } catch (NumberFormatException e) {
            String message = label.getText() + " " + errorProperty.get();

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(message);
            alert.showAndWait();

            throw new NumberFormatException(message);
        }
    }

    /**
     * Extract a non-null positive finite double from a field
     * @param textField user input
     * @param label to customize error message
     * @return a positive double of user input
     * @throws NumberFormatException when input is null, non-positive, or not double
     */
    public static double getPositiveDoubleFromField(TextField textField, Label label, StringProperty errorProperty) {
        String text = textField.getText();
        text = text.replace(',', '.');
        boolean validated = validatePattern(text);

        try {
            double input = Double.parseDouble(text);
            if (validated && !Double.isNaN(input) && !Double.isInfinite(input)
                    && input >= 0 && input < Double.MAX_VALUE) {
                return input;
            } else {
                throw new NumberFormatException("Input not in a valid format.");
            }
        } catch (NumberFormatException e) {
            String message = label.getText() + " " + errorProperty.get();

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(message);
            alert.showAndWait();

            throw new NumberFormatException(message);
        }
    }

    /**
     * Validates the user input by performing a regex pattern check,
     * allowing signs, digits, and a decimal part up to 5 digits after the decimal point.
     * @param text user input
     * @return true if the pattern matches, false otherwise
     */
    private static boolean validatePattern(String text) {
        Pattern plainNumber = Pattern.compile("^[+-]?(\\d+)(?:\\.(\\d+))?$");
        text = text.trim();
        Matcher matcher = plainNumber.matcher(text);
        if (matcher.matches()) {
            String fractionalPart = matcher.group(2);
            if (fractionalPart != null) {
                return fractionalPart.length() <= 5;
            }
        } else {
            return false;
        }

        return true;
    }
}
