package POJOs;

public class Usuario {

    public static String nombre;

    public static String password;
    public double latitud;
    public double longitud;

    public  String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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



    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Usuario.password = password;
    }


    public Usuario() {}

    public Usuario(String nombre,String password, double latitud, double longitud) {
        this.nombre = nombre;
        this.password = password;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Usuario(String nombre, double latitud, double longitud) {
        this.nombre = nombre;
        this.password = null;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}

