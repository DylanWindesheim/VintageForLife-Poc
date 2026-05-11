package nl.vintageforlife.routing.util;

import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;

public class OsmHttpsTileFactory extends DefaultTileFactory {

    private static final TileFactoryInfo OSM_HTTPS = new TileFactoryInfo(
            1, 17, 17, 256, true, true, "", "x", "y", "z") {
        @Override
        public String getTileUrl(int x, int y, int zoom) {
            return "https://tile.openstreetmap.org/" + (17 - zoom) + "/" + x + "/" + y + ".png";
        }
    };

    public OsmHttpsTileFactory() {
        super(OSM_HTTPS);
        setUserAgent("VintageForLife-POC/1.0");
        setThreadPoolSize(4);
    }
}
