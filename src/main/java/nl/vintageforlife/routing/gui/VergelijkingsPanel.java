package nl.vintageforlife.routing.gui;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Toont een vergelijkingstabel van alle berekende routes zodat duidelijk is
 * welk algoritme de kortste route vindt en hoe lang het daarvoor nodig heeft.
 */
public class VergelijkingsPanel extends JPanel {

    private final DefaultTableModel vergelijkModel;
    private final JTable vergelijkTabel;
    private final DefaultTableModel stopModel;
    private final JTable stopTabel;
    private final JLabel labelBeste;

    // Opgeslagen resultaten voor na het berekenen
    private final List<Route> routes = new ArrayList<>();
    private final List<Long> tijden = new ArrayList<>();

    public VergelijkingsPanel() {
        setLayout(new BorderLayout(8, 8));

        // Bovenstuk: vergelijkingstabel met alle algoritmen
        vergelijkModel = new DefaultTableModel(
                new String[]{"Algoritme", "Afstand (km)", "Tijd (ms)", "Stops", "Verschil"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        vergelijkTabel = new JTable(vergelijkModel);
        vergelijkTabel.setRowHeight(26);
        vergelijkTabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        vergelijkTabel.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        vergelijkTabel.setShowVerticalLines(false);
        vergelijkTabel.setGridColor(new Color(220, 220, 220));
        vergelijkTabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Centreer de kolommen "Tijd", "Stops" en "Verschil"
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        vergelijkTabel.getColumnModel().getColumn(1).setCellRenderer(center);
        vergelijkTabel.getColumnModel().getColumn(2).setCellRenderer(center);
        vergelijkTabel.getColumnModel().getColumn(3).setCellRenderer(center);
        vergelijkTabel.getColumnModel().getColumn(4).setCellRenderer(center);

        vergelijkTabel.getColumnModel().getColumn(0).setPreferredWidth(160);
        vergelijkTabel.getColumnModel().getColumn(1).setPreferredWidth(110);
        vergelijkTabel.getColumnModel().getColumn(2).setPreferredWidth(90);
        vergelijkTabel.getColumnModel().getColumn(3).setPreferredWidth(60);
        vergelijkTabel.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Klik op een rij → toon de stops van dat algoritme onderaan
        vergelijkTabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) toonStops(vergelijkTabel.getSelectedRow());
        });

        labelBeste = new JLabel(" ");
        labelBeste.setFont(new Font("SansSerif", Font.BOLD, 13));
        labelBeste.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 0));

        JPanel bovenPanel = new JPanel(new BorderLayout(0, 4));
        bovenPanel.add(new JScrollPane(vergelijkTabel), BorderLayout.CENTER);
        bovenPanel.add(labelBeste, BorderLayout.SOUTH);

        // Onderstuk: stoppenlijst van het geselecteerde algoritme
        stopModel = new DefaultTableModel(new String[]{"#", "Klant", "Adres", "Type", "Gewicht"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        stopTabel = new JTable(stopModel);
        stopTabel.setRowHeight(22);
        stopTabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        stopTabel.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        stopTabel.setShowVerticalLines(false);
        stopTabel.setGridColor(new Color(220, 220, 220));
        stopTabel.getColumnModel().getColumn(0).setPreferredWidth(30);
        stopTabel.getColumnModel().getColumn(1).setPreferredWidth(120);
        stopTabel.getColumnModel().getColumn(2).setPreferredWidth(220);
        stopTabel.getColumnModel().getColumn(3).setPreferredWidth(80);
        stopTabel.getColumnModel().getColumn(4).setPreferredWidth(80);

        JLabel stopHeader = new JLabel("Stops van geselecteerd algoritme:");
        stopHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        stopHeader.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 0));

        JPanel onderPanel = new JPanel(new BorderLayout(0, 4));
        onderPanel.add(stopHeader, BorderLayout.NORTH);
        onderPanel.add(new JScrollPane(stopTabel), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, bovenPanel, onderPanel);
        splitPane.setResizeWeight(0.55);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    /** Voegt een berekend resultaat toe aan de lijst. */
    public void voegToe(Route route, long tijdMs) {
        routes.add(route);
        tijden.add(tijdMs);
    }

    /** Vult de vergelijkingstabel op basis van alle opgeslagen resultaten. */
    public void toonResultaten() {
        vergelijkModel.setRowCount(0);
        if (routes.isEmpty()) return;

        // Zoek de kortste afstand om het verschil te berekenen
        double kortste = routes.stream()
                .mapToDouble(Route::getTotaalAfstand)
                .min().orElse(0);

        String besteNaam = "";
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            double afstand = route.getTotaalAfstand();
            int stops = Math.max(0, route.getStops().size() - 1);
            String verschil = afstand == kortste ? "beste" :
                    String.format("+%.1f%%", (afstand - kortste) / kortste * 100);
            vergelijkModel.addRow(new Object[]{
                    route.getAlgoritmeNaam(),
                    String.format("%.2f", afstand / 1000.0),
                    tijden.get(i),
                    stops,
                    verschil
            });
            if (afstand == kortste) besteNaam = route.getAlgoritmeNaam();
        }

        labelBeste.setText("Beste resultaat: " + besteNaam);

        // Selecteer automatisch de beste rij
        for (int i = 0; i < vergelijkModel.getRowCount(); i++) {
            if ("beste".equals(vergelijkModel.getValueAt(i, 4))) {
                vergelijkTabel.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    /** Toont de stoppenlijst van de geselecteerde rij onderaan. */
    private void toonStops(int rij) {
        stopModel.setRowCount(0);
        if (rij < 0 || rij >= routes.size()) return;
        int nr = 1;
        for (Stop stop : routes.get(rij).getStops()) {
            stopModel.addRow(new Object[]{
                    nr++, stop.getKlantNaam(),
                    stop.getAdres() != null ? stop.getAdres().toString() : "",
                    stop.getStopType(),
                    String.format("%.0f kg", stop.getGewicht())
            });
        }
    }

    /** Wist alle resultaten zodat opnieuw berekend kan worden. */
    public void reset() {
        routes.clear();
        tijden.clear();
        vergelijkModel.setRowCount(0);
        stopModel.setRowCount(0);
        labelBeste.setText(" ");
    }
}
