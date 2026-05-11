package nl.vintageforlife.routing.gui;

import nl.vintageforlife.routing.algorithm.IRouteAlgorithm;
import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;
import nl.vintageforlife.routing.util.RouteUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private AlgorithmSelectiePanel algPanel;
    private KaartPanel kaartPanel;
    private ResultatenPanel resultatenPanel;
    private List<Stop> stopList;

    public MainFrame() { initialiseer(); }

    /** Bouwt het hoofdvenster op met alle panelen en knoppen. */
    public void initialiseer() {
        setTitle("Vintage for Life — Route Optimalisatie POC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(6, 6));

        stopList = RouteUtils.genereerTestdata();
        algPanel = new AlgorithmSelectiePanel();
        kaartPanel = new KaartPanel();
        resultatenPanel = new ResultatenPanel();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        JButton btnBereken = new JButton("Bereken Route");
        JButton btnReset = new JButton("Reset testdata");
        btnBereken.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnBereken.addActionListener(e -> berekenRoute());
        btnReset.addActionListener(e -> {
            stopList = RouteUtils.genereerTestdata();
            kaartPanel.setHuidigeRoute(null);
            kaartPanel.repaint();
            JOptionPane.showMessageDialog(this, "Testdata is opnieuw geladen (8 stops).", "Reset",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(btnBereken);
        buttonPanel.add(btnReset);

        JLabel statusBar = new JLabel("  Klaar. Selecteer een algoritme en klik 'Bereken Route'.");
        statusBar.setFont(new Font("SansSerif", Font.ITALIC, 11));
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.WEST);
        southPanel.add(statusBar, BorderLayout.SOUTH);

        add(algPanel, BorderLayout.WEST);
        add(kaartPanel, BorderLayout.CENTER);
        add(resultatenPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(1100, 620));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** Voert het geselecteerde algoritme uit en werkt kaart en resultaten bij. */
    public void berekenRoute() {
        IRouteAlgorithm algoritme = algPanel.getGeteselecteerdAlgoritme();
        if (algoritme == null) {
            JOptionPane.showMessageDialog(this, "Selecteer een algoritme.", "Fout", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            long start = System.currentTimeMillis();
            Route route = algoritme.berekenRoute(stopList, algPanel.getMaxCapaciteit());
            long elapsed = System.currentTimeMillis() - start;

            kaartPanel.setHuidigeRoute(route);
            kaartPanel.repaint();
            resultatenPanel.voegRouteToe(route, elapsed, algPanel.getMaxCapaciteit());
            resultatenPanel.toonResultaten();
        } catch (UnsupportedOperationException ex) {
            JOptionPane.showMessageDialog(this,
                    "Dit algoritme is nog niet geïmplementeerd.\n" +
                    "Neem contact op met je teamgenoot die dit algoritme implementeert.",
                    "Niet beschikbaar", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void voegStopToe(Stop stop) { stopList.add(stop); }
    public void toonResultaten(List<Stop> stops) { resultatenPanel.toonResultaten(); }

    public static void main(String[] args) { SwingUtilities.invokeLater(MainFrame::new); }
}
