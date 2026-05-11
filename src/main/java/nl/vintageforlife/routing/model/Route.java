package nl.vintageforlife.routing.model;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private List<Stop> stops;
    private double totaalAfstand;
    private double totaalGewicht;
    private String algoritmeNaam;

    public Route() {
        this.stops = new ArrayList<>();
    }

    public Route(String algoritmeNaam) {
        this.stops = new ArrayList<>();
        this.algoritmeNaam = algoritmeNaam;
    }

    public void voegtStop(Stop stop) {
        stops.add(stop);
        totaalGewicht += stop.getGewicht();
    }

    /** UML: getInformatieStops():List<Stop> */
    public List<Stop> getInformatieStops() {
        return new ArrayList<>(stops);
    }

    public List<Stop> getStops() { return stops; }
    public double getTotaalAfstand() { return totaalAfstand; }
    public double getTotaalGewicht() { return totaalGewicht; }
    public String getAlgoritmeNaam() { return algoritmeNaam; }

    public void setStops(List<Stop> stops) { this.stops = stops; }
    public void setTotaalAfstand(double totaalAfstand) { this.totaalAfstand = totaalAfstand; }
    public void setTotaalGewicht(double totaalGewicht) { this.totaalGewicht = totaalGewicht; }
    public void setAlgoritmeNaam(String naam) { this.algoritmeNaam = naam; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Route [").append(algoritmeNaam).append("]\n");
        sb.append(String.format("Totaal afstand: %.0f m (%.2f km)%n",
                totaalAfstand, totaalAfstand / 1000.0));
        sb.append(String.format("Totaal gewicht: %.1f kg%n", totaalGewicht));
        for (int i = 0; i < stops.size(); i++) {
            sb.append(i + 1).append(". ").append(stops.get(i)).append("\n");
        }
        return sb.toString();
    }
}
