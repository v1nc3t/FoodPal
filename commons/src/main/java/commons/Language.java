package commons;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public enum Language {
    EN,
    DE,
    NL;

    private final static String BUNDLE_NAME = "commons.language";

    private String proper;

    /**
     * Gets the 'proper' label for a language
     * @return proper label of the language
     */
    public String proper() {
        return proper;
    }

    /**
     * Enum constructor that initializes possible languages
     * while also giving them the default locale (English) labels.
     */
    Language() {
        Locale defaultLocale = Locale.ENGLISH;
        var resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, defaultLocale);
        loadProperLabel(resourceBundle);
    }

    /**
     * Loads the proper label for each language
     * @param resourceBundle the provided resource bundle from which to read label
     */
    public void loadProperLabel(ResourceBundle resourceBundle) {
        String translationKey = "txt." + this.name().toLowerCase();
        try {
            this.proper = resourceBundle.getString(translationKey);
        } catch (Exception e) {
            System.err.println("Missing translation for " + translationKey
                    + " in resource bundle " +
                    resourceBundle.getBaseBundleName());
            this.proper = this.name().toLowerCase();
        }
    }

    /**
     * Reloads all proper language labels to a new resource bundle of another language
     * @param resourceBundle the provided resource bundle from which to read labels
     */
    public static void reloadLocale(ResourceBundle resourceBundle) {
        for (Language language : Language.values()) {
            language.loadProperLabel(resourceBundle);
        }
    }

    /**
     * Finds and returns the language enum of the proper language label provided.
     * In case such a label does not exist, EN is returned.
     * @param properLabel proper language label
     * @return language enum of the proper language label, EN if label not found
     */
    public static Language valueOfProper(String properLabel) {
        for (Language language : Language.values()) {
            if (Objects.equals(language.proper, properLabel)) return language;
        }
        return EN;
    }
}
