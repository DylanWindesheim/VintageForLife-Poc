package nl.vintageforlife.routing.algorithm;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;

import java.util.List;

/** Brute-force TSP — genereert alle permutaties en kiest de kortste tour. Alleen bruikbaar tot ~10 stops (O(n!)). */
public class BruteForceAlgoritme implements IRouteAlgorithm {

    private final String naam = "Brute Force";
    private int iMaxIteraties;

    public BruteForceAlgoritme() { this.iMaxIteraties = 100_000; }
    public BruteForceAlgoritme(int iMaxIteraties) { this.iMaxIteraties = iMaxIteraties; }

    @Override
    public Route berekenRoute(List<Stop> stops, int maxCapaciteit) {
        // TODO: implementeren — genereer alle permutaties van stops[1..n], kies de route met kleinste totaalAfstand
        throw new UnsupportedOperationException("BruteForceAlgoritme is nog niet geïmplementeerd.");
    }

    /** TODO: implementeren — geeft alle permutaties van de stoplijst terug via recursie. */
    public List<List<Stop>> genereerPermutaties(List<Stop> stops) {
        throw new UnsupportedOperationException("genereerPermutaties is nog niet geïmplementeerd.");
    }

    public int getIMaxIteraties()         { return iMaxIteraties; }
    public void setIMaxIteraties(int max)  { this.iMaxIteraties = max; }
    public String getNaam()                { return naam; }
}
