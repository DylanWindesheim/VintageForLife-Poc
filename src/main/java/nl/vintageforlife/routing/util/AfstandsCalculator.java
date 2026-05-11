package nl.vintageforlife.routing.util;

import nl.vintageforlife.routing.model.Adres;
import nl.vintageforlife.routing.model.Stop;

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

    /** Geeft een symmetrische n×n afstandsmatrix in meters terug voor de gegeven stoplijst. */
    public static double[][] maakAfstandsMatrix(List<Stop> stops) {
        int n = stops.size();
        double[][] matrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double d = berekenAfstand(stops.get(i).getAdres(), stops.get(j).getAdres());
                matrix[i][j] = d;
                matrix[j][i] = d;
            }
        }
        return matrix;
    }
}
