package nl.vintageforlife.routing.model;

import java.util.ArrayList;
import java.util.List;

public class Klant {

    private String naam;
    private String email;
    private String telefoonnummer;
    private List<Stop> stops;

    public Klant() {
        this.stops = new ArrayList<>();
    }

    public Klant(String naam, String email, String telefoonnummer) {
        this.naam = naam;
        this.email = email;
        this.telefoonnummer = telefoonnummer;
        this.stops = new ArrayList<>();
    }

    public void voegtStopToe(Stop stop) {
        stops.add(stop);
    }

    public List<Stop> getStops() {
        return new ArrayList<>(stops);
    }

    public String getNaam() { return naam; }
    public String getEmail() { return email; }
    public String getTelefoonnummer() { return telefoonnummer; }

    public void setNaam(String naam) { this.naam = naam; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefoonnummer(String tel) { this.telefoonnummer = tel; }
}
