package nl.vintageforlife.routing.util;

import nl.vintageforlife.routing.model.Adres;
import nl.vintageforlife.routing.model.Stop;
import nl.vintageforlife.routing.model.StopType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AfstandsCalculator {

    private AfstandsCalculator() {}

    private static final double AARDE_STRAAL_METER = 6_371_000.0;

    /** Geeft de afstand in meters tussen twee adressen op basis van lat/lon. */
    public static double berekenAfstand(Adres a, Adres b) {
        double lat1 = Math.toRadians(a.getLatitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double deltaLat = Math.toRadians(b.getLatitude() - a.getLatitude());
        double deltaLon = Math.toRadians(b.getLongitude() - a.getLongitude());
        double sinDLat = Math.sin(deltaLat / 2);
        double sinDLon = Math.sin(deltaLon / 2);
        double h = sinDLat * sinDLat + Math.cos(lat1) * Math.cos(lat2) * sinDLon * sinDLon;
        return AARDE_STRAAL_METER * 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));
    }

    /** Berekent de totale afstand van een rondrit inclusief de terugrit naar het depot (index 0). */
    public static double berekenTourAfstand(List<Stop> tour) {
        double total = 0.0;
        for (int i = 0; i < tour.size() - 1; i++) {
            total += berekenAfstand(tour.get(i).getAdres(), tour.get(i + 1).getAdres());
        }
        if (tour.size() > 1) {
            total += berekenAfstand(tour.get(tour.size() - 1).getAdres(), tour.get(0).getAdres());
        }
        return total;
    }

    /**
     * Controleert of de vrachtwagen nergens boven de maximale capaciteit uitkomt.
     * De vrachtwagen vertrekt vol met alle leveringen (index 0 is het depot en telt niet mee).
     * Bij levering daalt het gewicht, bij retour stijgt het.
     */
    public static boolean isCapaciteitGeldig(List<Stop> tour, int maxCapaciteit) {
        double gewicht = tour.stream()
                .filter(s -> s.getStopType() == StopType.LEVERING)
                .mapToDouble(Stop::getGewicht)
                .sum();
        for (int i = 1; i < tour.size(); i++) {
            Stop s = tour.get(i);
            if (s.getStopType() == StopType.RETOUR) {
                gewicht += s.getGewicht();
                if (gewicht > maxCapaciteit) return false;
            } else {
                gewicht -= s.getGewicht();
            }
        }
        return true;
    }

    /** Keert het stuk van de tour tussen index i en j om en geeft de nieuwe tour terug (2-opt). */
    public static List<Stop> twoOpt(List<Stop> tour, int i, int j) {
        List<Stop> newTour = new ArrayList<>(tour.subList(0, i));
        List<Stop> reversed = new ArrayList<>(tour.subList(i, j + 1));
        Collections.reverse(reversed);
        newTour.addAll(reversed);
        newTour.addAll(tour.subList(j + 1, tour.size()));
        return newTour;
    }

}
