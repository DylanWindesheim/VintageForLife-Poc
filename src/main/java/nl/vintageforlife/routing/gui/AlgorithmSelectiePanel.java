package nl.vintageforlife.routing.gui;

import nl.vintageforlife.routing.algorithm.*;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/** Het linkerpaneel waar de gebruiker een algoritme en maximale laadcapaciteit kiest. */
public class AlgorithmSelectiePanel extends JPanel {

    private IRouteAlgorithm geselecteerdAlgoritme;
    private final Map<String, IRouteAlgorithm> algoritmes = new LinkedHashMap<>();
    private final JComboBox<String> comboBox;
    private final JSpinner capaciteitSpinner;

    public AlgorithmSelectiePanel() {
        setPreferredSize(new Dimension(230, 520));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Algoritme selectie"));

        // Voeg alle beschikbare algoritmen toe aan de lijst
        algoritmes.put("Nearest Neighbor", new NearestNeighborAlgoritme());
        algoritmes.put("Simulated Annealing", new SimulatedAnnealingAlgoritme());
        algoritmes.put("Brute Force", new BruteForceAlgoritme());
        algoritmes.put("Genetisch Algoritme", new GenetischAlgoritme());
        algoritmes.put("Hill Climbing", new HillClimbingAlgoritme());

        // Keuzelijst met alle algoritmen
        comboBox = new JComboBox<>(algoritmes.keySet().toArray(new String[0]));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        comboBox.addActionListener(e -> selectAlgorithm());

        // Invoerveld voor het maximale gewicht dat de vrachtwagen mag meenemen
        capaciteitSpinner = new JSpinner(new SpinnerNumberModel(500, 50, 5000, 50));
        capaciteitSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Tekstgebied dat laat zien welke algoritmen al werken en welke nog niet
        JTextArea statusArea = new JTextArea(
                "Geïmplementeerd:\n  ✓ Nearest Neighbor\n  ✓ Simulated Annealing\n\n" +
                "Nog te implementeren:\n  ○ Brute Force\n  ○ Genetisch Algoritme\n  ○ Hill Climbing");
        statusArea.setEditable(false);
        statusArea.setBackground(getBackground());
        statusArea.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        add(Box.createVerticalStrut(8));
        add(new JLabel("Kies algoritme:"));
        add(Box.createVerticalStrut(4));
        add(comboBox);
        add(Box.createVerticalStrut(12));
        add(new JLabel("Max. capaciteit (kg):"));
        add(Box.createVerticalStrut(4));
        add(capaciteitSpinner);
        add(Box.createVerticalStrut(16));
        add(new JSeparator());
        add(Box.createVerticalStrut(8));
        add(statusArea);
        add(Box.createVerticalGlue());

        selectAlgorithm();
    }

    /** Slaat het gekozen algoritme op zodat MainFrame het kan opvragen. */
    public void selectAlgorithm() {
        geselecteerdAlgoritme = algoritmes.get((String) comboBox.getSelectedItem());
    }

    public IRouteAlgorithm getGeteselecteerdAlgoritme() { return geselecteerdAlgoritme; }
    public int getMaxCapaciteit() { return (int) capaciteitSpinner.getValue(); }

    /** Voegt een extra algoritme toe aan de keuzelijst. */
    public void addAlgoritme(String naam, IRouteAlgorithm algo) {
        algoritmes.put(naam, algo);
        comboBox.addItem(naam);
    }
}
