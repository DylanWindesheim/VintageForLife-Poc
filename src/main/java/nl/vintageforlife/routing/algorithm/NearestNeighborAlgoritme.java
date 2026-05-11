package nl.vintageforlife.routing.algorithm;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;
import nl.vintageforlife.routing.util.AfstandsCalculator;

import java.util.ArrayList;
import java.util.List;

/** Greedy nearest-neighbor heuristic — altijd naar de dichtstbijzijnde onbezochte stop. O(n²). */
public class NearestNeighborAlgoritme implements IRouteAlgorithm {

    private final String naam = "Nearest Neighbor";
    private int iMaxIteraties;

    public NearestNeighborAlgoritme() { this.iMaxIteraties = 1000; }
    public NearestNeighborAlgoritme(int iMaxIteraties) { this.iMaxIteraties = iMaxIteraties; }

    @Override
    public Route berekenRoute(List<Stop> stops, int maxCapaciteit) {
        if (stops == null || stops.isEmpty()) return new Route(naam);

        Route route = new Route(naam);
        List<Stop> unvisited = new ArrayList<>(stops);
        double totaalAfstand = 0.0;
        double huidigeGewicht = 0.0;

        // Index 0 is het depot
        Stop current = unvisited.remove(0);
        route.voegtStop(current);
        Stop depot = current;

        while (!unvisited.isEmpty()) {
            Stop nearest = null;
            double minDist = Double.MAX_VALUE;

            for (Stop candidate : unvisited) {
                if (huidigeGewicht + candidate.getGewicht() > maxCapaciteit) continue;
                double d = AfstandsCalculator.berekenAfstand(current.getAdres(), candidate.getAdres());
                if (d < minDist) { minDist = d; nearest = candidate; }
            }

            if (nearest == null) break; // geen stop past meer binnen de capaciteit

            huidigeGewicht += nearest.getGewicht();
            totaalAfstand += minDist;
            unvisited.remove(nearest);
            route.voegtStop(nearest);
            current = nearest;
        }

        totaalAfstand += AfstandsCalculator.berekenAfstand(current.getAdres(), depot.getAdres());
        route.setTotaalAfstand(totaalAfstand);
        return route;
    }

    public void voegtStopToe(Route route, Stop stop) { route.voegtStop(stop); }

    public int getIMaxIteraties() { return iMaxIteraties; }
    public void setIMaxIteraties(int max) { this.iMaxIteraties = max; }
    public String getNaam() { return naam; }
}
