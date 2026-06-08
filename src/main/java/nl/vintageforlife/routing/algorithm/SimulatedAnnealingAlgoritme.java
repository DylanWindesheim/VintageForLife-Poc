package nl.vintageforlife.routing.algorithm;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;
import nl.vintageforlife.routing.util.AfstandsCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulatedAnnealingAlgoritme implements IRouteAlgorithm {

    private final String naam = "Simulated Annealing";
    private double temperature; // begintemperatuur — hoe hoger, hoe meer vrijheid om slechtere routes te accepteren
    private double koelFactor;  // hoe snel de temperatuur daalt per stap (waarde tussen 0 en 1)

    public SimulatedAnnealingAlgoritme() { this.temperature = 10_000.0; this.koelFactor = 0.9995; }
    public SimulatedAnnealingAlgoritme(double temperature, double koelFactor) {
        this.temperature = temperature;
        this.koelFactor = koelFactor;
    }

    @Override
    public Route berekenRoute(List<Stop> stops, int maxCapaciteit) {
        if (stops == null || stops.isEmpty()) return new Route(naam);

        // Begin met de route die Nearest Neighbor heeft berekend als startpunt
        List<Stop> currentTour = new ArrayList<>(
                new NearestNeighborAlgoritme().berekenRoute(stops, maxCapaciteit).getStops());
        double currentAfstand = AfstandsCalculator.berekenTourAfstand(currentTour);

        // Sla de beste gevonden route apart op
        List<Stop> bestTour = new ArrayList<>(currentTour);
        double bestAfstand = currentAfstand;

        Random rng = new Random();
        double temp = this.temperature;

        // Blijf verbeteren zolang de temperatuur hoog genoeg is
        while (temp > 1.0) {
            int size = currentTour.size();
            if (size < 3) break; // minder dan 2 klanten, niets te verbeteren

            // Kies twee willekeurige posities in de route (het depot op index 0 blijft altijd staan)
            int i = 1 + rng.nextInt(size - 2);
            int j = i + 1 + rng.nextInt(size - 1 - i);

            // Maak een nieuwe route door het stuk tussen i en j om te draaien (2-opt)
            List<Stop> neighbour = AfstandsCalculator.twoOpt(currentTour, i, j);

            // Sla de nieuwe route over als die de capaciteitsgrens overschrijdt
            if (!AfstandsCalculator.isCapaciteitGeldig(neighbour, maxCapaciteit)) {
                temp *= koelFactor;
                continue;
            }

            double neighbourAfstand = AfstandsCalculator.berekenTourAfstand(neighbour);
            double delta = neighbourAfstand - currentAfstand;

            // Accepteer de nieuwe route als die korter is, of soms ook als die langer is
            if (delta < 0 || rng.nextDouble() < Math.exp(-delta / temp)) {
                currentTour = neighbour;
                currentAfstand = neighbourAfstand;

                // Sla de nieuwe route op als het de beste tot nu toe is
                if (currentAfstand < bestAfstand) {
                    bestTour = new ArrayList<>(currentTour);
                    bestAfstand = currentAfstand;
                }
            }

            // Verlaag de temperatuur — het algoritme wordt steeds strikter
            temp *= koelFactor;
        }

        Route result = new Route(naam);
        for (Stop s : bestTour) result.voegtStop(s);
        result.setTotaalAfstand(bestAfstand);
        return result;
    }

    public double getTemperature() { return temperature; }
    public void setTemperature(double t) { this.temperature = t; }
    public double getKoelFactor() { return koelFactor; }
    public void setKoelFactor(double k) { this.koelFactor = k; }
    public String getNaam() { return naam; }
}
