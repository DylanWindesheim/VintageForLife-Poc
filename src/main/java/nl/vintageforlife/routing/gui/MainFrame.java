package nl.vintageforlife.routing.gui;

import nl.vintageforlife.routing.algorithm.*;
import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;
import nl.vintageforlife.routing.util.RouteUtils;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Het hoofdvenster van de applicatie. Berekent alle algoritmen tegelijk en toont de vergelijking. */
public class MainFrame extends JFrame {

    private VergelijkingsPanel vergelijkingsPanel;
    private JSpinner capaciteitSpinner;
    private List<Stop> stopList;

    // Alle beschikbare algoritmen op volgorde
    private final Map<String, IRouteAlgorithm> algoritmen = new LinkedHashMap<>();

    public MainFrame() {
        algoritmen.put("Nearest Neighbor", new NearestNeighborAlgoritme());
        algoritmen.put("Simulated Annealing", new SimulatedAnnealingAlgoritme());
        algoritmen.put("Hill Climbing", new HillClimbingAlgoritme());
        algoritmen.put("Brute Force", new BruteForceAlgoritme());
        algoritmen.put("Genetisch Algoritme", new GenetischAlgoritme());
        initialiseer();
    }

    private void initialiseer() {
        setTitle("Vintage for Life — Route Optimalisatie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        stopList = RouteUtils.genereerTestdata();

        // Bovenste balk met instellingen en knoppen
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel capaciteitLabel = new JLabel("Max. capaciteit (kg):");
        capaciteitSpinner = new JSpinner(new SpinnerNumberModel(500, 50, 5000, 50));
        capaciteitSpinner.setPreferredSize(new Dimension(80, 28));

        JButton btnBereken = new JButton("Bereken alle routes");
        JButton btnReset = new JButton("Reset testdata");
        btnBereken.addActionListener(e -> berekenAlleRoutes());
        btnReset.addActionListener(e -> {
            stopList = RouteUtils.genereerTestdata();
            vergelijkingsPanel.reset();
        });

        topPanel.add(capaciteitLabel);
        topPanel.add(capaciteitSpinner);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(btnBereken);
        topPanel.add(btnReset);

        vergelijkingsPanel = new VergelijkingsPanel();

        add(topPanel, BorderLayout.NORTH);
        add(vergelijkingsPanel, BorderLayout.CENTER);

        pack();
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** Voert alle algoritmen uit en stuurt de resultaten naar het vergelijkingspaneel. */
    private void berekenAlleRoutes() {
        int maxCapaciteit = (int) capaciteitSpinner.getValue();
        vergelijkingsPanel.reset();

        for (Map.Entry<String, IRouteAlgorithm> entry : algoritmen.entrySet()) {
            long start = System.currentTimeMillis();
            Route route = entry.getValue().berekenRoute(stopList, maxCapaciteit);
            long tijd = System.currentTimeMillis() - start;
            vergelijkingsPanel.voegToe(route, tijd);
        }

        vergelijkingsPanel.toonResultaten();
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
