package nl.vintageforlife.routing.algorithm;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;
import nl.vintageforlife.routing.util.AfstandsCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Dit algoritme werkt zoals de natuur: een groep routes ("populatie") evolueert over meerdere
 * generaties. De kortste routes hebben de meeste kans om te worden doorgegeven aan de volgende
 * generatie. Routes worden gecombineerd (crossover) en soms willekeurig aangepast (mutatie)
 * zodat nieuwe oplossingen worden ontdekt. Na iMaxIteraties generaties wordt de beste gevonden
 * route teruggegeven.
 */
public class GenetischAlgoritme implements IRouteAlgorithm {

    private final String naam = "Genetisch Algoritme";
    private int iMaxIteraties; // aantal generaties dat het algoritme doorloopt
    private static final int POPULATIE_GROOTTE = 60;
    private static final double MUTATIE_KANS = 0.15;

    public GenetischAlgoritme() { this.iMaxIteraties = 500; }
    public GenetischAlgoritme(int iMaxIteraties) { this.iMaxIteraties = iMaxIteraties; }

    @Override
    public Route berekenRoute(List<Stop> stops, int maxCapaciteit) {
        if (stops == null || stops.isEmpty()) return new Route(naam);

        Stop depot = stops.get(0);
        List<Stop> klanten = new ArrayList<>(stops.subList(1, stops.size()));

        Random rng = new Random();

        // Maak een beginpopulatie van willekeurig geschudde routes die de capaciteit respecteren
        List<List<Stop>> populatie = new ArrayList<>();
        for (int i = 0; i < POPULATIE_GROOTTE; i++) {
            List<Stop> tour;
            int pogingen = 0;
            do {
                tour = new ArrayList<>(klanten);
                Collections.shuffle(tour, rng);
                pogingen++;
            } while (!AfstandsCalculator.isCapaciteitGeldig(volledig(tour, depot), maxCapaciteit) && pogingen < 50);
            populatie.add(tour);
        }

        List<Stop> besteTour = beste(populatie, depot);
        double besteAfstand = AfstandsCalculator.berekenTourAfstand(volledig(besteTour, depot));

        // Laat de populatie evolueren over iMaxIteraties generaties
        for (int gen = 0; gen < iMaxIteraties; gen++) {
            List<List<Stop>> nieuw = new ArrayList<>();

            // Houd de beste tour altijd door (elitisme)
            nieuw.add(new ArrayList<>(besteTour));

            while (nieuw.size() < POPULATIE_GROOTTE) {
                // Selecteer twee ouders via toernooiselectie en combineer ze
                List<Stop> ouder1 = toernooi(populatie, depot, rng);
                List<Stop> ouder2 = toernooi(populatie, depot, rng);
                List<Stop> kind = crossover(ouder1, ouder2);
                // Gebruik ouder1 als het kind een ongeldige route oplevert
                if (!AfstandsCalculator.isCapaciteitGeldig(volledig(kind, depot), maxCapaciteit)) {
                    kind = new ArrayList<>(ouder1);
                }

                // Pas soms een kleine willekeurige wijziging toe; alleen als het resultaat geldig blijft
                if (rng.nextDouble() < MUTATIE_KANS) {
                    List<Stop> gemuteerd = muteer(kind);
                    if (AfstandsCalculator.isCapaciteitGeldig(volledig(gemuteerd, depot), maxCapaciteit)) {
                        kind = gemuteerd;
                    }
                }
                nieuw.add(kind);
            }

            populatie = nieuw;

            // Bijhouden of deze generatie een betere route heeft opgeleverd
            List<Stop> kandidaat = beste(populatie, depot);
            double kandidaatAfstand = AfstandsCalculator.berekenTourAfstand(volledig(kandidaat, depot));
            if (kandidaatAfstand < besteAfstand) {
                besteAfstand = kandidaatAfstand;
                besteTour = kandidaat;
            }
        }

        Route route = new Route(naam);
        route.voegtStop(depot);
        for (Stop s : besteTour) route.voegtStop(s);
        route.setTotaalAfstand(besteAfstand);
        return route;
    }

    /**
     * Order Crossover (OX): kopieert een willekeurig segment van ouder1 en vult
     * de rest aan met de volgorde uit ouder2. Zo blijven alle stops aanwezig.
     */
    public List<Stop> crossover(List<Stop> ouder1, List<Stop> ouder2) {
        int n = ouder1.size();
        Random rng = new Random();
        int start = rng.nextInt(n);
        int eind = start + rng.nextInt(n - start);

        List<Stop> kind = new ArrayList<>(Collections.nCopies(n, null));

        // Kopieer het segment van ouder1
        for (int i = start; i <= eind; i++) kind.set(i, ouder1.get(i));

        // Vul de rest aan met de volgorde uit ouder2, sla al aanwezige stops over
        int pos = (eind + 1) % n;
        for (Stop stop : ouder2) {
            if (!kind.contains(stop)) {
                kind.set(pos, stop);
                pos = (pos + 1) % n;
            }
        }
        return kind;
    }

    /** Verwisselt twee willekeurige stops om diversiteit in de populatie te houden. */
    public List<Stop> muteer(List<Stop> tour) {
        List<Stop> nieuw = new ArrayList<>(tour);
        Random rng = new Random();
        int i = rng.nextInt(nieuw.size());
        int j = rng.nextInt(nieuw.size());
        Collections.swap(nieuw, i, j);
        return nieuw;
    }

    /** Kiest twee willekeurige tours uit de populatie en geeft de kortste van de twee terug. */
    private List<Stop> toernooi(List<List<Stop>> populatie, Stop depot, Random rng) {
        List<Stop> a = populatie.get(rng.nextInt(populatie.size()));
        List<Stop> b = populatie.get(rng.nextInt(populatie.size()));
        double afstandA = AfstandsCalculator.berekenTourAfstand(volledig(a, depot));
        double afstandB = AfstandsCalculator.berekenTourAfstand(volledig(b, depot));
        return afstandA <= afstandB ? a : b;
    }

    /** Geeft de tour met de kortste afstand uit de populatie terug. */
    private List<Stop> beste(List<List<Stop>> populatie, Stop depot) {
        List<Stop> beste = null;
        double besteAfstand = Double.MAX_VALUE;
        for (List<Stop> tour : populatie) {
            double afstand = AfstandsCalculator.berekenTourAfstand(volledig(tour, depot));
            if (afstand < besteAfstand) { besteAfstand = afstand; beste = tour; }
        }
        return beste;
    }

    /** Bouwt een volledige tour met depot vooraan voor afstandsberekening. */
    private List<Stop> volledig(List<Stop> tour, Stop depot) {
        List<Stop> volledig = new ArrayList<>();
        volledig.add(depot);
        volledig.addAll(tour);
        return volledig;
    }

    public int getIMaxIteraties() { return iMaxIteraties; }
    public void setIMaxIteraties(int max) { this.iMaxIteraties = max; }
    public String getNaam() { return naam; }
}
