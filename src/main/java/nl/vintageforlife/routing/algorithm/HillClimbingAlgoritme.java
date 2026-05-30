package nl.vintageforlife.routing.algorithm;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;
import nl.vintageforlife.routing.util.AfstandsCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Dit algoritme begint met de route van Nearest Neighbor en probeert die te verbeteren door
 * stukken van de route om te draaien. Bij elke ronde wordt de beste verbetering uitgevoerd.
 * Zodra er geen verbetering meer mogelijk is, stopt het algoritme. Anders dan Simulated Annealing
 * accepteert Hill Climbing nooit een slechtere route — daardoor is het sneller maar kan het
 * blijven hangen op een oplossing die lokaal goed maar globaal niet de beste is.
 */
public class HillClimbingAlgoritme implements IRouteAlgorithm {

    private final String naam = "Hill Climbing";
    private int iMaxIteraties; // maximaal aantal verbeterrondes voordat we stoppen

    public HillClimbingAlgoritme() { this.iMaxIteraties = 1000; }
    public HillClimbingAlgoritme(int iMaxIteraties) { this.iMaxIteraties = iMaxIteraties; }

    @Override
    public Route berekenRoute(List<Stop> stops, int maxCapaciteit) {
        if (stops == null || stops.isEmpty()) return new Route(naam);

        // Begin met de route die Nearest Neighbor heeft berekend
        List<Stop> currentTour = new ArrayList<>(
                new NearestNeighborAlgoritme().berekenRoute(stops, maxCapaciteit).getStops());
        double currentAfstand = AfstandsCalculator.berekenTourAfstand(currentTour);

        int rondes = 0;
        boolean verbeterd = true;

        // Blijf verbeteren zolang er nog winst te behalen valt
        while (verbeterd && rondes < iMaxIteraties) {
            verbeterd = false;
            double besteAfstand = currentAfstand;
            List<Stop> besteTour = currentTour;

            // Probeer alle mogelijke 2-opt omkeringen en kies de allerbeste verbetering
            for (int i = 1; i < currentTour.size() - 1; i++) {
                for (int j = i + 1; j < currentTour.size(); j++) {
                    List<Stop> kandidaat = AfstandsCalculator.twoOpt(currentTour, i, j);

                    // Sla kandidaten over die de capaciteitsgrens overschrijden
                    if (!AfstandsCalculator.isCapaciteitGeldig(kandidaat, maxCapaciteit)) continue;

                    double kandidaatAfstand = AfstandsCalculator.berekenTourAfstand(kandidaat);

                    // Sla deze kandidaat op als die de kortste tot nu toe is
                    if (kandidaatAfstand < besteAfstand) {
                        besteAfstand = kandidaatAfstand;
                        besteTour = kandidaat;
                        verbeterd = true;
                    }
                }
            }

            // Als er een verbetering is gevonden, ga verder met die nieuwe route
            if (verbeterd) {
                currentTour = besteTour;
                currentAfstand = besteAfstand;
            }
            rondes++;
        }

        Route result = new Route(naam);
        for (Stop s : currentTour) result.voegtStop(s);
        result.setTotaalAfstand(currentAfstand);
        return result;
    }

    public int getIMaxIteraties() { return iMaxIteraties; }
    public void setIMaxIteraties(int max) { this.iMaxIteraties = max; }
    public String getNaam() { return naam; }
}
