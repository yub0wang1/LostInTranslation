package translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface that reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private final List<String> languageCodes = new ArrayList<>();
    private final List<String> countryCodes = new ArrayList<>();
    // the key used is "countryCode-languageCode"; the value is the translated country name
    private final Map<String, String> translations = new HashMap<>();

    // Constants for JSON field names
    private static final String ID_FIELD = "id";
    private static final String ALPHA2_FIELD = "alpha2";
    private static final String ALPHA3_FIELD = "alpha3";
    private static final Set<String> EXCLUDED_KEYS = Set.of(ID_FIELD, ALPHA2_FIELD, ALPHA3_FIELD);

    /**
     * Construct a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Construct a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        try {
            loadAndParseData(filename);
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException("Failed to load resource file: " + filename, ex);
        }
    }

    private void loadAndParseData(String filename) throws IOException, URISyntaxException {
        // Check if resource exists
        var resource = getClass().getClassLoader().getResource(filename);
        if (resource == null) {
            throw new IOException("Resource file not found: " + filename);
        }

        // Read the file
        String jsonString = Files.readString(Paths.get(resource.toURI()));
        JSONArray jsonArray = new JSONArray(jsonString);

        parseCountryData(jsonArray);
    }

    private void parseCountryData(JSONArray jsonArray) {
        Set<String> uniqueLanguageCodes = new HashSet<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject countryData = jsonArray.getJSONObject(i);

            // Validate required fields
            if (!countryData.has(ALPHA3_FIELD)) {
                throw new RuntimeException("Missing required field 'alpha3' in country data at index " + i);
            }

            String countryCode = countryData.getString(ALPHA3_FIELD);
            countryCodes.add(countryCode);

            // Process translations for this country
            processCountryTranslations(countryData, countryCode, uniqueLanguageCodes);
        }

        // Add all unique language codes at the end
        languageCodes.addAll(uniqueLanguageCodes);
    }

    private void processCountryTranslations(JSONObject countryData, String countryCode, Set<String> uniqueLanguageCodes) {
        for (String key : countryData.keySet()) {
            if (!EXCLUDED_KEYS.contains(key)) {
                String languageCode = key;
                String translation = countryData.getString(key);

                // Store the translation
                String translationKey = countryCode + "-" + languageCode;
                translations.put(translationKey, translation);

                // Track unique language codes
                uniqueLanguageCodes.add(languageCode);
            }
        }
    }

    @Override
    public List<String> getLanguageCodes() {
        return new ArrayList<>(languageCodes);
    }

    @Override
    public List<String> getCountryCodes() {
        return new ArrayList<>(countryCodes);
    }

    @Override
    public String translate(String countryCode, String languageCode) {
        if (countryCode == null || languageCode == null) {
            return null;
        }
        return translations.get(countryCode + "-" + languageCode);
    }
}