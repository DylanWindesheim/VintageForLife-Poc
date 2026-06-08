package nl.vintageforlife.routing.algorithm;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;
import nl.vintageforlife.routing.model.StopType;
import nl.vintageforlife.routing.util.AfstandsCalculator;

import java.util.ArrayList;
import java.util.List;

public class BruteForceAlgoritme implements IRouteAlgorithm {

    private final String naam = "Brute Force";
    private int iMaxIteraties; // maximaal aantal permutaties dat wordt geprobeerd

    public BruteForceAlgoritme() { this.iMaxIteraties = 100_000; }
    public BruteForceAlgoritme(int iMaxIteraties) { this.iMaxIteraties = iMaxIteraties; }

    @Override
    public Route berekenRoute(List<Stop> stops, int maxCapaciteit) {
        if (stops == null || stops.isEmpty()) return new Route(naam);

        // Het depot staat altijd vast op index 0 — alleen de overige stops worden gepermuteerd
        Stop depot = stops.get(0);
        List<Stop> tePermutteren = new ArrayList<>(stops.subList(1, stops.size()));

        // Begingewicht: vrachtwagen vertrekt vol met alle leveringen
        double beginGewicht = tePermutteren.stream()
                .filter(s -> s.getStopType() == StopType.LEVERING)
                .mapToDouble(Stop::getGewicht)
                .sum();

        BesteTour resultaat = new BesteTour();
        permuteer(tePermutteren, 0, depot, beginGewicht, maxCapaciteit, resultaat);

        if (resultaat.tour == null) return new Route(naam);

        Route route = new Route(naam);
        route.voegtStop(depot);
        for (Stop s : resultaat.tour) route.voegtStop(s);
        route.setTotaalAfstand(resultaat.afstand);
        return route;
    }

    /** Doorloopt alle permutaties via swap-recursie en slaat de kortste geldige tour op. */
    private void permuteer(List<Stop> lijst, int start, Stop depot,
            double beginGewicht, int maxCapaciteit, BesteTour resultaat) {
        if (resultaat.aantalPogingen >= iMaxIteraties) return;

        if (start == lijst.size()) {
            resultaat.aantalPogingen++;

            // Controleer of de capaciteit nergens wordt overschreden
            double gewicht = beginGewicht;
            for (Stop s : lijst) {
                gewicht += s.getStopType() == StopType.RETOUR ? s.getGewicht() : -s.getGewicht();
                if (gewicht > maxCapaciteit) return;
            }

            // Bouw de volledige tour en bereken de afstand
            List<Stop> tour = new ArrayList<>();
            tour.add(depot);
            tour.addAll(lijst);
            double afstand = AfstandsCalculator.berekenTourAfstand(tour);
            if (afstand < resultaat.afstand) {
                resultaat.afstand = afstand;
                resultaat.tour = new ArrayList<>(lijst);
            }
            return;
        }

        // Wissel elk element op de huidige positie en ga dieper in de recursie
        for (int i = start; i < lijst.size(); i++) {
            wissel(lijst, start, i);
            permuteer(lijst, start + 1, depot, beginGewicht, maxCapaciteit, resultaat);
            wissel(lijst, start, i); // herstel de volgorde na terugkeer
        }
    }

    private void wissel(List<Stop> lijst, int a, int b) {
        Stop tmp = lijst.get(a);
        lijst.set(a, lijst.get(b));
        lijst.set(b, tmp);
    }

    // Hulpklasse om de beste tour doorheen de recursie bij te houden
    private static class BesteTour {
        List<Stop> tour = null;
        double afstand = Double.MAX_VALUE;
        int aantalPogingen = 0;
    }

    public int getIMaxIteraties() { return iMaxIteraties; }
    public void setIMaxIteraties(int max) { this.iMaxIteraties = max; }
    public String getNaam() { return naam; }
}
