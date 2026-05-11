package nl.vintageforlife.routing.model;

public class Stop {

    private StopType stopType;
    private Adres adres;
    private double gewicht;
    private double volume;
    private String klantNaam;

    public Stop() {}

    public Stop(StopType stopType, Adres adres, double gewicht, double volume, String klantNaam) {
        this.stopType = stopType;
        this.adres = adres;
        this.gewicht = gewicht;
        this.volume = volume;
        this.klantNaam = klantNaam;
    }

    public StopType getStopType() { return stopType; }
    public Adres getAdres() { return adres; }
    public double getGewicht() { return gewicht; }
    public double getVolume() { return volume; }
    public String getKlantNaam() { return klantNaam; }

    public void setStopType(StopType stopType) { this.stopType = stopType; }
    public void setAdres(Adres adres) { this.adres = adres; }
    public void setGewicht(double gewicht) { this.gewicht = gewicht; }
    public void setVolume(double volume) { this.volume = volume; }
    public void setKlantNaam(String klantNaam) { this.klantNaam = klantNaam; }

    @Override
    public String toString() {
        return klantNaam + " - " + adres + " [" + stopType + "]";
    }
}
