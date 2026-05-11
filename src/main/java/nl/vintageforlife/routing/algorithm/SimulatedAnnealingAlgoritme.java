package nl.vintageforlife.routing.algorithm;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;
import nl.vintageforlife.routing.util.AfstandsCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Simulated Annealing metaheuristiek — bootstrapt vanuit NN, accepteert slechtere oplossingen
 *  met kans exp(-delta/T) zodat lokale optima worden ontsnapt. */
public class SimulatedAnnealingAlgoritme implements IRouteAlgorithm {

    private final String naam = "Simulated Annealing";
    private double temperature;
    private double koelFactor;

    public SimulatedAnnealingAlgoritme() { this.temperature = 10_000.0; this.koelFactor = 0.9995; }
    public SimulatedAnnealingAlgoritme(double temperature, double koelFactor) {
        this.temperature = temperature;
        this.koelFactor = koelFactor;
    }

    @Override
    public Route berekenRoute(List<Stop> stops, int maxCapaciteit) {
        if (stops == null || stops.isEmpty()) return new Route(naam);

        // Startoplossing via NN zodat SA vanuit een redelijke route begint
        List<Stop> currentTour = new ArrayList<>(
                new NearestNeighborAlgoritme().berekenRoute(stops, maxCapaciteit).getStops());
        double currentAfstand = berekenTourAfstand(currentTour);
        List<Stop> bestTour = new ArrayList<>(currentTour);
        double bestAfstand = currentAfstand;

        Random rng = new Random();
        double temp = this.temperature;

        while (temp > 1.0) {
            int size = currentTour.size();
            int i = 1 + rng.nextInt(size - 1);
            int j = 1 + rng.nextInt(size - 1);
            if (i == j) { temp *= koelFactor; continue; }

            List<Stop> neighbour = new ArrayList<>(currentTour);
            Stop tmp = neighbour.get(i);
            neighbour.set(i, neighbour.get(j));
            neighbour.set(j, tmp);

            double neighbourAfstand = berekenTourAfstand(neighbour);
            double delta = neighbourAfstand - currentAfstand;

            if (delta < 0 || Math.random() < Math.exp(-delta / temp)) {
                currentTour = neighbour;
                currentAfstand = neighbourAfstand;
                if (currentAfstand < bestAfstand) {
                    bestTour = new ArrayList<>(currentTour);
                    bestAfstand = currentAfstand;
                }
            }
            temp *= koelFactor;
        }

        Route result = new Route(naam);
        for (Stop s : bestTour) result.voegtStop(s);
        result.setTotaalAfstand(bestAfstand);
        return result;
    }

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
