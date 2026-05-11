package nl.vintageforlife.routing.model;

public class Adres {

    private String straat;
    private String postcode;
    private String stad;
    private double latitude;
    private double longitude;

    public Adres() {}

    public Adres(String straat, String postcode, String stad, double latitude, double longitude) {
        this.straat = straat;
        this.postcode = postcode;
        this.stad = stad;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getStraat() { return straat; }
    public String getPostcode() { return postcode; }
    public String getStad() { return stad; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public void setStraat(String straat) { this.straat = straat; }
    public void setPostcode(String postcode) { this.postcode = postcode; }
    public void setStad(String stad) { this.stad = stad; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    @Override
    public String toString() {
        return straat + ", " + postcode + " " + stad;
    }
}
