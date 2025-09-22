package translation;

import java.util.List;

/**
 * An interface providing methods related to translating country names between
 * different languages. This class works in terms of country codes and language codes.
 */
public interface Translator {

    /**
     * Return the language codes for all languages whose translations are
     * available.
     * @return list of language codes which are available for this country
     */
    List<String> getLanguageCodes();

    /**
     * Return the country codes for all countries whose translations are
     * available from this Translator.
     * @return list of country codes for which we have translations available
     */
    List<String> getCountryCodes();

    /**
     * Return the name of the country based on the specified country code and language code.
     * This method does not return the country code.
     * @param countryCode the country code
     * @param languageCode the language code
     * @return the name of the country in the given language or null if no translation is available
     */
    String translate(String countryCode, String languageCode);
}