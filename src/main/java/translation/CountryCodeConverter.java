package translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CountryCodeConverter {
    private final Map<String, String> countryCodeToCountry = new HashMap<>();
    private final Map<String, String> countryToCountryCode = new HashMap<>();
    private final Set<String> originalCountryNames = new HashSet<>();

    public CountryCodeConverter() {
        this("country-codes.txt");
    }

    public CountryCodeConverter(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        try {
            loadCountryData(filename);
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException("Failed to load country code data from: " + filename, ex);
        }
    }

    private void loadCountryData(String filename) throws IOException, URISyntaxException {
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
            processCountryLine(line, i);
        }
    }

    private void processCountryLine(String line, int lineNumber) {
        String[] parts = line.split("\t");
        if (parts.length < 3) {
            System.err.println("Warning: Skipping invalid line " + lineNumber + " - expected at least 3 columns: " + line);
            return;
        }

        String countryName = parts[0].trim();
        String countryCode = parts[2].trim();

        if (!countryCode.matches("[A-Z]{3}")) {
            System.err.println("Warning: Skipping invalid country code format: " + countryCode);
            return;
        }

        countryCodeToCountry.put(countryCode, countryName);
        countryToCountryCode.put(countryName.toLowerCase(), countryCode);
        originalCountryNames.add(countryName);
    }

    public String fromCountryCode(String code) {
        if (code == null) return null;
        return countryCodeToCountry.get(code.toUpperCase());
    }

    public String fromCountry(String country) {
        if (country == null) return null;
        return countryToCountryCode.get(country.toLowerCase());
    }

    public Set<String> getCountryNames() {
        return Collections.unmodifiableSet(originalCountryNames);
    }

    public Set<String> getCountryCodes() {
        return Collections.unmodifiableSet(countryCodeToCountry.keySet());
    }

    public int getNumCountries() {
        return originalCountryNames.size();
    }
}