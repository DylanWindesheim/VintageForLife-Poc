package nl.vintageforlife.routing.algorithm;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;

import java.util.List;

public interface IRouteAlgorithm {

    /**
     * Berekent een route langs alle stops en geeft die terug.
     * De stop op index 0 is altijd het depot (vertrekpunt).
     * De totale afstand wordt ingesteld op het Route-object.
     */
    Route berekenRoute(List<Stop> stops, int maxCapaciteit);
}
