package POJOs;

public class MapUser {
    public MapUser(String username, Double latitud, Double longitud) {
        this.Username = username;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public MapUser(){

    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String Username;
    public double latitud;
    public double longitud;
}
