package nl.vintageforlife.routing.util;

import nl.vintageforlife.routing.model.Adres;
import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;
import nl.vintageforlife.routing.model.StopType;

import java.util.ArrayList;
import java.util.List;

public class RouteUtils {

    private RouteUtils() {}

    /** Genereert 8 teststops met echte Nederlandse coördinaten. Index 0 is altijd het depot (Zwolle). */
    public static List<Stop> genereerTestdata() {
        List<Stop> stops = new ArrayList<>();
        stops.add(maakStop("Depot", "Informaticalaan 1", "8017 CA", "Zwolle", 52.5168, 6.0830, 0.0, 0.0, StopType.LEVERING));
        stops.add(maakStop("Klant A", "Grote Markt 5", "2011 RD", "Haarlem", 52.3812, 4.6369, 45.0, 0.8, StopType.LEVERING));
        stops.add(maakStop("Klant B", "Marktstraat 12", "7411 GX", "Deventer", 52.2551, 6.1594, 30.0, 0.5, StopType.LEVERING));
        stops.add(maakStop("Klant C", "Keizersgracht 100", "1015 CN", "Amsterdam", 52.3676, 4.9041, 60.0, 1.2, StopType.LEVERING));
        stops.add(maakStop("Klant D", "Binnenhof 1", "2513 AA", "Den Haag", 52.0799, 4.3137, 25.0, 0.4, StopType.LEVERING));
        stops.add(maakStop("Klant E", "Stadhuisplein 10", "3012 AR", "Rotterdam", 51.9225, 4.4792, 55.0, 1.0, StopType.LEVERING));
        stops.add(maakStop("Klant F", "Neude 5", "3512 AE", "Utrecht", 52.0906, 5.1208, 35.0, 0.6, StopType.LEVERING));
        stops.add(maakStop("Klant G", "Grote Kerkplein 3", "5611 GX", "Eindhoven", 51.4416, 5.4697, 20.0, 0.3, StopType.RETOUR));
        return stops;
    }

    private static Stop maakStop(String klantNaam, String straat, String postcode, String stad,
                                  double lat, double lon, double gewicht, double volume, StopType type) {
        return new Stop(type, new Adres(straat, postcode, stad, lat, lon), gewicht, volume, klantNaam);
    }

    /** Geeft de kompashoek in graden van adres a naar adres b. */
    public static double berekenWinkelhoek(Adres a, Adres b) {
        double dLon = Math.toRadians(b.getLongitude() - a.getLongitude());
        double lat1 = Math.toRadians(a.getLatitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    /** Geeft het totale gewicht van een lijst stops terug. */
    public static double berekenStal(List<Stop> stops) {
        return stops.stream().mapToDouble(Stop::getGewicht).sum();
    }

    /** Drukt een geformatteerde routesamenvatting af naar stdout. */
    public static void exporteerRoute(Route r) {
        System.out.println("=== ROUTE EXPORT ===");
        System.out.println(r);
        System.out.println("====================");
    }
}
