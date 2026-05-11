package nl.vintageforlife.routing.algorithm;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;

import java.util.List;

/** Hill Climbing TSP — iteratieve verbetering via 2-opt swaps, accepteert nooit een slechtere oplossing. */
public class HillClimbingAlgoritme implements IRouteAlgorithm {

    private final String naam = "Hill Climbing";

    @Override
    public Route berekenRoute(List<Stop> stops, int maxCapaciteit) {
        // TODO: implementeren — start vanuit NN-tour, wissel paren (i,j) zolang de route korter wordt
        throw new UnsupportedOperationException("HillClimbingAlgoritme is nog niet geïmplementeerd.");
    }

    public String getNaam() { return naam; }
}
