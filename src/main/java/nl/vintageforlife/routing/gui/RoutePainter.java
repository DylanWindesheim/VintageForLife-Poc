package nl.vintageforlife.routing.gui;

import nl.vintageforlife.routing.model.Adres;
import nl.vintageforlife.routing.model.Route;
import nl.vintageforlife.routing.model.Stop;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class RoutePainter implements Painter<JXMapViewer> {

    private final Route route;

    public RoutePainter(Route route) {
        this.route = route;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
        g = (Graphics2D) g.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Rectangle bounds = map.getViewportBounds();
        List<Stop> stops = route.getStops();

        // Route lines between consecutive stops
        g.setColor(new Color(30, 100, 210, 210));
        g.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < stops.size() - 1; i++) {
            Point p1 = geoToScreen(stops.get(i).getAdres(), map, bounds);
            Point p2 = geoToScreen(stops.get(i + 1).getAdres(), map, bounds);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
            tekenPijl(g, p1, p2, new Color(30, 100, 210, 210));
        }

        // Dashed closing leg: last stop → depot
        if (stops.size() > 1) {
            Point pLast  = geoToScreen(stops.get(stops.size() - 1).getAdres(), map, bounds);
            Point pDepot = geoToScreen(stops.get(0).getAdres(), map, bounds);
            g.setColor(new Color(200, 90, 0, 180));
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10f, new float[]{8f, 5f}, 0f));
            g.drawLine(pLast.x, pLast.y, pDepot.x, pDepot.y);
        }

        // Markers with sequence numbers
        for (int i = 0; i < stops.size(); i++) {
            Point p = geoToScreen(stops.get(i).getAdres(), map, bounds);

            // Shadow
            g.setColor(new Color(0, 0, 0, 60));
            g.fillOval(p.x - 9, p.y - 7, 18, 18);

            // Marker
            Color fill = (i == 0) ? new Color(20, 160, 20) : new Color(210, 40, 40);
            g.setColor(fill);
            g.fillOval(p.x - 10, p.y - 10, 20, 20);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1.5f));
            g.drawOval(p.x - 10, p.y - 10, 20, 20);

            // Sequence number
            g.setFont(new Font("SansSerif", Font.BOLD, 10));
            String num = String.valueOf(i + 1);
            FontMetrics fm = g.getFontMetrics();
            g.setColor(Color.WHITE);
            g.drawString(num, p.x - fm.stringWidth(num) / 2, p.y + fm.getAscent() / 2 - 1);

            // Name label
            g.setFont(new Font("SansSerif", Font.BOLD, 11));
            String label = stops.get(i).getKlantNaam();
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRoundRect(p.x + 12, p.y - 10, g.getFontMetrics().stringWidth(label) + 8, 16, 4, 4);
            g.setColor(Color.WHITE);
            g.drawString(label, p.x + 16, p.y + 2);
        }

        g.dispose();
    }

    private Point geoToScreen(Adres adres, JXMapViewer map, Rectangle bounds) {
        GeoPosition geo = new GeoPosition(adres.getLatitude(), adres.getLongitude());
        Point2D pt = map.getTileFactory().geoToPixel(geo, map.getZoom());
        return new Point((int) (pt.getX() - bounds.x), (int) (pt.getY() - bounds.y));
    }

    private void tekenPijl(Graphics2D g, Point from, Point to, Color color) {
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        int midX = (from.x + to.x) / 2;
        int midY = (from.y + to.y) / 2;
        int len  = 10;

        g.setColor(color);
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(midX, midY,
                (int) (midX - len * Math.cos(angle - Math.PI / 6)),
                (int) (midY - len * Math.sin(angle - Math.PI / 6)));
        g.drawLine(midX, midY,
                (int) (midX - len * Math.cos(angle + Math.PI / 6)),
                (int) (midY - len * Math.sin(angle + Math.PI / 6)));
    }
}
