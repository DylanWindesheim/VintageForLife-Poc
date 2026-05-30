package nl.vintageforlife.routing.algorithm;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;
import nl.vintageforlife.routing.model.StopType;
import nl.vintageforlife.routing.util.AfstandsCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Dit algoritme berekent een route door steeds naar de dichtstbijzijnde nog niet bezochte
 * stop te rijden. Het is snel maar niet altijd de kortst mogelijke route.
 * Met iMaxIteraties kun je het maximale aantal stops per rit beperken.
 *
 * Het retourproces wordt correct verwerkt: de vrachtwagen vertrekt beladen met alle
 * leveringen. Bij elke levering daalt het gewicht, bij elke retour stijgt het.
 * Een retourstop wordt overgeslagen als die de maximale capaciteit zou overschrijden.
 */
public class NearestNeighborAlgoritme implements IRouteAlgorithm {

    private final String naam = "Nearest Neighbor";
    private int iMaxIteraties; // maximaal aantal stops dat per rit bezocht mag worden

    public NearestNeighborAlgoritme() { this.iMaxIteraties = Integer.MAX_VALUE; }
    public NearestNeighborAlgoritme(int iMaxIteraties) { this.iMaxIteraties = iMaxIteraties; }

    @Override
    public Route berekenRoute(List<Stop> stops, int maxCapaciteit) {
        if (stops == null || stops.isEmpty()) return new Route(naam);

        Route route = new Route(naam);
        List<Stop> unvisited = new ArrayList<>(stops); // lijst van stops die nog niet bezocht zijn

        // De eerste stop in de lijst is altijd het depot (vertrekpunt)
        Stop depot = unvisited.remove(0);
        route.voegtStop(depot);

        // De vrachtwagen vertrekt met alle leveringen al ingeladen
        double huidigeGewicht = unvisited.stream()
                .filter(s -> s.getStopType() == StopType.LEVERING)
                .mapToDouble(Stop::getGewicht)
                .sum();

        double totaalAfstand = 0.0;
        int aantalBezocht = 0;
        Stop current = depot;

        while (!unvisited.isEmpty() && aantalBezocht < iMaxIteraties) {

            // Zoek de dichtstbijzijnde stop die past qua gewicht
            Stop nearest = null;
            double minDist = Double.MAX_VALUE;

            for (Stop candidate : unvisited) {
                // Retourstop: gewicht stijgt — sla over als de vrachtwagen vol raakt
                if (candidate.getStopType() == StopType.RETOUR
                        && huidigeGewicht + candidate.getGewicht() > maxCapaciteit) continue;
                double d = AfstandsCalculator.berekenAfstand(current.getAdres(), candidate.getAdres());
                if (d < minDist) { minDist = d; nearest = candidate; }
            }

            // Geen geschikte stop meer gevonden — stop de rit
            if (nearest == null) break;

            // Levering: gewicht daalt (goederen worden afgeleverd)
            // Retour: gewicht stijgt (goederen worden opgehaald)
            if (nearest.getStopType() == StopType.LEVERING) {
                huidigeGewicht -= nearest.getGewicht();
            } else {
                huidigeGewicht += nearest.getGewicht();
            }

            totaalAfstand += minDist;
            unvisited.remove(nearest);
            route.voegtStop(nearest);
            current = nearest;
            aantalBezocht++;
        }

        // Rij terug naar het depot om de rit te sluiten
        totaalAfstand += AfstandsCalculator.berekenAfstand(current.getAdres(), depot.getAdres());
        route.setTotaalAfstand(totaalAfstand);
        return route;
    }

    public int getIMaxIteraties() { return iMaxIteraties; }
    public void setIMaxIteraties(int max) { this.iMaxIteraties = max; }
    public String getNaam() { return naam; }
}
