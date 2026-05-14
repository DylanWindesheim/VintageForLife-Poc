package nl.vintageforlife.routing.gui;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/** Het rechterpaneel dat de berekende route toont: samenvatting bovenaan, stoppenlijst in het midden
 *  en een detailweergave onderaan. */
public class ResultatenPanel extends JPanel {

    private final Map<String, Route> routes; // alle berekende routes, gesorteerd op naam
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextArea detailArea;
    private final JLabel labelAfstand;
    private final JLabel labelTijd;
    private final JLabel labelAantal;
    private final JLabel labelCapaciteit;

    public ResultatenPanel() {
        routes = new LinkedHashMap<>();
        setPreferredSize(new Dimension(290, 520));
        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createTitledBorder("Resultaten"));

        // Samenvatting bovenaan met vier regels: afstand, tijd, stops en lading
        JPanel summaryPanel = new JPanel(new GridLayout(4, 1, 2, 2));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        labelAfstand = new JLabel("Afstand:  —");
        labelTijd = new JLabel("Berekeningtijd:  —");
        labelAantal = new JLabel("Aantal stops:  —");
        labelCapaciteit = new JLabel("Lading:  —");
        summaryPanel.add(labelAfstand);
        summaryPanel.add(labelTijd);
        summaryPanel.add(labelAantal);
        summaryPanel.add(labelCapaciteit);

        // Tabel met alle stops van de laatste berekende route
        tableModel = new DefaultTableModel(new String[]{"#", "Klant", "Type", "Gewicht"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(20);
        table.getColumnModel().getColumn(0).setPreferredWidth(25);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(65);
        table.getColumnModel().getColumn(3).setPreferredWidth(65);

        // Detailtekstgebied onderaan toont de volledige route als tekst
        detailArea = new JTextArea(6, 20);
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane detailScroll = new JScrollPane(detailArea);
        detailScroll.setBorder(BorderFactory.createTitledBorder("Detail"));

        add(summaryPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(detailScroll, BorderLayout.SOUTH);
    }

    /** Slaat de route op en werkt de samenvattingslabels bij. Kleurt lading rood als die te zwaar is. */
    public void voegRouteToe(Route route, long tijdMs, int maxCapaciteit) {
        routes.put(route.getAlgoritmeNaam(), route);
        labelTijd.setText(String.format("Berekeningtijd:  %d ms", tijdMs));
        labelAfstand.setText(String.format("Afstand:  %.0f m  (%.1f km)",
                route.getTotaalAfstand(), route.getTotaalAfstand() / 1000.0));
        int aantalStops = Math.max(0, route.getStops().size() - 1); // -1 omdat index 0 het depot is
        labelAantal.setText("Stops ingepland:  " + aantalStops);

        double gewicht = route.getTotaalGewicht();
        String tekst = String.format("Lading:  %.0f / %d kg", gewicht, maxCapaciteit);
        if (gewicht > maxCapaciteit) {
            labelCapaciteit.setForeground(Color.RED);
            tekst += " overschreden!";
        } else {
            labelCapaciteit.setForeground(new Color(0, 140, 0));
        }
        labelCapaciteit.setText(tekst);
    }

    /** Vult de tabel met de stops van de meest recent berekende route. */
    public void toonResultaten() {
        tableModel.setRowCount(0);
        if (routes.isEmpty()) return;
        Route latest = routes.values().stream().reduce((a, b) -> b).orElse(null);
        if (latest == null) return;

        int row = 1;
        for (Stop stop : latest.getStops()) {
            tableModel.addRow(new Object[]{
                    row++, stop.getKlantNaam(), stop.getStopType(),
                    String.format("%.1f kg", stop.getGewicht())
            });
        }
        detailArea.setText(latest.toString());
        detailArea.setCaretPosition(0);
    }

    public Map<String, Route> getRoutes() { return routes; }
}
