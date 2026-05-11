package nl.vintageforlife.routing.algorithm;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;

import java.util.List;

public interface IRouteAlgorithm {

    /**
     * Calculates and returns an optimised route for the given stops.
     *
     * @param stops list of delivery/return stops (depot is always index 0)
     * @param maxCapaciteit maximum vehicle load in kg
     * @return a fully built Route with totaalAfstand set
     */
    Route berekenRoute(List<Stop> stops, int maxCapaciteit);
}
