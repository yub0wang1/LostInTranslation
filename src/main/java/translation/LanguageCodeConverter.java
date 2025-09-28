package translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LanguageCodeConverter {
    private final Map<String, String> languageCodeToName = new HashMap<>();
    private final Map<String, String> languageNameToCode = new HashMap<>();
    private final Set<String> originalLanguageNames = new HashSet<>();

    public LanguageCodeConverter() {
        this("language-codes.txt");
    }

    public LanguageCodeConverter(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        try {
            loadLanguageData(filename);
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException("Failed to load language code data from: " + filename, ex);
        }
    }

    private void loadLanguageData(String filename) throws IOException, URISyntaxException {
        var resource = getClass().getClassLoader().getResource(filename);
        if (resource == null) {
            throw new IOException("Resource file not found: " + filename);
        }

        List<String> lines = Files.readAllLines(Paths.get(resource.toURI()));

        if (lines.isEmpty()) {
            throw new IOException("File is empty: " + filename);
        }

        // Skip header and process each line
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            processLanguageLine(line, i);
        }
    }

    private void processLanguageLine(String line, int lineNumber) {
        String[] parts = line.split("\t");
        if (parts.length < 2) {
            System.err.println("Warning: Skipping invalid line " + lineNumber + " - expected at least 2 columns: " + line);
            return;
        }

        String languageName = parts[0].trim();
        String languageCode = parts[1].trim();

        if (!languageCode.matches("[a-z]{2,3}(-[a-z]{2,3})?")) {
            System.err.println("Warning: Skipping invalid language code format at line " + lineNumber + ": " + languageCode);
            return;
        }

        languageCodeToName.put(languageCode, languageName.toLowerCase());
        languageNameToCode.put(languageName.toLowerCase(), languageCode);
        originalLanguageNames.add(languageName);
    }

    public String fromLanguageCode(String code) {
        if (code == null) return null;
        return languageCodeToName.get(code.toLowerCase());
    }

    public String fromLanguage(String language) {
        if (language == null) return null;
        return languageNameToCode.get(language.toLowerCase());
    }

    public Set<String> getLanguageNames() {
        return Collections.unmodifiableSet(originalLanguageNames);
    }

    public Set<String> getLanguageCodes() {
        return Collections.unmodifiableSet(languageCodeToName.keySet());
    }

    public int getNumLanguages() {
        return originalLanguageNames.size();
    }
}