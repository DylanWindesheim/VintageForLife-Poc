package nl.vintageforlife.routing.gui;

import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.util.OsmHttpsTileFactory;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Het middenpaneel met de OpenStreetMap kaart. Je kunt de kaart slepen en inzoomen met het scrollwiel. */
public class KaartPanel extends JPanel {

    private Route huidigeRoute;
    private final JXMapViewer mapViewer;

    public KaartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Kaart"));
        setPreferredSize(new Dimension(640, 540));

        // Stel de kaartviewer in met OpenStreetMap tegels, startzoom en beginpositie (Nederland)
        mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(new OsmHttpsTileFactory());
        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(new GeoPosition(52.3, 5.3));

        // Muisgebeurtenissen: slepen om te pannen, scrollwiel om in/uit te zoomen
        MouseInputListener pan = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(pan);
        mapViewer.addMouseMotionListener(pan);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        add(mapViewer, BorderLayout.CENTER);
    }

    /** Stelt een nieuwe route in en tekent die meteen op de kaart. */
    public void setHuidigeRoute(Route route) {
        this.huidigeRoute = route;
        herlaadPainters();
    }

    @Override
    public void repaint() {
        super.repaint();
        if (mapViewer != null) mapViewer.repaint();
    }

    /** Verwijdert de oude route van de kaart en tekent de nieuwe. */
    private void herlaadPainters() {
        if (huidigeRoute == null || huidigeRoute.getStops().isEmpty()) {
            mapViewer.setOverlayPainter(null);
            mapViewer.repaint();
            return;
        }
        List<org.jxmapviewer.painter.Painter<JXMapViewer>> painters = new ArrayList<>();
        painters.add(new RoutePainter(huidigeRoute));
        mapViewer.setOverlayPainter(new CompoundPainter<>(painters));
        mapViewer.repaint();
    }

    public void toonMarkers() { mapViewer.repaint(); }
}
