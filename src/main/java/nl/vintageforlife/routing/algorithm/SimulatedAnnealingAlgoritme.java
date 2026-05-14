package nl.vintageforlife.routing.algorithm;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;
import nl.vintageforlife.routing.util.AfstandsCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Dit algoritme begint met de route van Nearest Neighbor en probeert die daarna te verbeteren.
 * Het wisselt steeds twee stukken van de route om en kijkt of de totale afstand korter wordt.
 * Soms wordt een slechtere route toch geaccepteerd — zo voorkom je dat het vastloopt op een
 * oplossing die lokaal goed lijkt maar globaal beter kan. Naarmate het algoritme langer loopt
 * (temperatuur daalt) worden slechtere keuzes minder snel geaccepteerd.
 */
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
        double currentAfstand = berekenTourAfstand(currentTour);

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
            List<Stop> neighbour = twoOpt(currentTour, i, j);
            double neighbourAfstand = berekenTourAfstand(neighbour);
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

    /** Draait het stuk van de route tussen index i en j om. Dit is een 2-opt verbetering. */
    private List<Stop> twoOpt(List<Stop> tour, int i, int j) {
        List<Stop> newTour = new ArrayList<>(tour.subList(0, i));
        List<Stop> reversed = new ArrayList<>(tour.subList(i, j + 1));
        Collections.reverse(reversed);
        newTour.addAll(reversed);
        newTour.addAll(tour.subList(j + 1, tour.size()));
        return newTour;
    }

    /** Berekent de totale afstand van een route, inclusief de terugrit naar het depot. */
    private double berekenTourAfstand(List<Stop> tour) {
        double total = 0.0;
        for (int i = 0; i < tour.size() - 1; i++) {
            total += AfstandsCalculator.berekenAfstand(tour.get(i).getAdres(), tour.get(i + 1).getAdres());
        }
        if (tour.size() > 1) {
            total += AfstandsCalculator.berekenAfstand(
                    tour.get(tour.size() - 1).getAdres(), tour.get(0).getAdres());
        }
        return total;
    }

    public double getTemperature() { return temperature; }
    public void setTemperature(double t) { this.temperature = t; }
    public double getKoelFactor() { return koelFactor; }
    public void setKoelFactor(double k) { this.koelFactor = k; }
    public String getNaam() { return naam; }
}
