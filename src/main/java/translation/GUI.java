package translation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JPanel languagePanel = new JPanel();
            languagePanel.add(new JLabel("Language:"));

            LanguageCodeConverter converter = new LanguageCodeConverter();

            JComboBox<String> languageComboBox = new JComboBox<>();
            for (String name : converter.getLanguageNames()) {
                languageComboBox.addItem(name);
            }
            languagePanel.add(languageComboBox);

            JPanel countryPanel = new JPanel();
            countryPanel.setLayout(new GridLayout(0, 2));
            countryPanel.add(new JLabel("Country:"), 0);

            CountryCodeConverter countryConverter = new CountryCodeConverter();
            JList<String> countryList = new JList<>(
                    countryConverter.getCountryNames().toArray(new String[0])
            );
            JScrollPane scrollPane = new JScrollPane(countryList);
            countryPanel.add(scrollPane, 1);

            JPanel resultPanel = new JPanel();
            JLabel resultLabelText = new JLabel("Translation:");
            JLabel resultLabel = new JLabel(" ");
            resultPanel.add(resultLabelText);
            resultPanel.add(resultLabel);

            JSONTranslator translator = new JSONTranslator();

            Runnable updateTranslation = () -> {
                String langName = (String) languageComboBox.getSelectedItem();
                String countryName = countryList.getSelectedValue();
                if (langName == null || countryName == null) {
                    resultLabel.setText(" ");
                    return;
                }
                String langCode = converter.fromLanguage(langName.toLowerCase());       // e.g., "fr"
                String countryCode = countryConverter.fromCountry(countryName.toLowerCase()); // e.g., "DEU"
                String translated = translator.translate(countryCode, langCode);
                resultLabel.setText(translated != null ? translated : "No translation found!");
            };

            // Update when user changes either selection
            languageComboBox.addActionListener(e -> updateTranslation.run());
            countryList.addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) updateTranslation.run(); });


            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            mainPanel.add(languagePanel);
            mainPanel.add(Box.createVerticalStrut(8));
            mainPanel.add(countryPanel);
            mainPanel.add(Box.createVerticalStrut(8));
            mainPanel.add(resultPanel);

            // --- Frame ---
            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        });
    }
}