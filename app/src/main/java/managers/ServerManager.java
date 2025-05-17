package managers;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ServerManager {

    public ServerManager(){

    }

    //Estas clases de callback sirven como un return true/false de las llamadas al servidor dado que los procesos con el servidor son externos y no siguen la linea de flujo del sistema, por lo que no pueden usar logica de return true/false para determinar errores
    public interface LoginCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface UbicacionCallback {
        void onSuccess();
        void onFailure(String error);
    }


    public void sendUser(String username, String password, LoginCallback callback){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Usuarios");

        Map<String, Object> datos = new HashMap<>();
        datos.put("password", password);
        datos.put("latitud", 0);
        datos.put("longitud", 0);

        dbRef.child(username).setValue(datos)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(); // Llamar al callback si fue exitoso
                })
                .addOnFailureListener(e -> {
                    Log.w("Server", "Error de servidor: " + e.getMessage());
                    callback.onFailure(e.getMessage()); // Enviar el error al callback
                });
    }

    public void sendLocation(String username, String passwordIngresada, double nuevaLat, double nuevaLong, UbicacionCallback callback){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(username);

        dbRef.get().addOnSuccessListener(snapshot -> {
            if(snapshot.exists()) {
                String passwordGuardada = snapshot.child("password").getValue(String.class);

                if(passwordGuardada != null && passwordGuardada.equals(passwordIngresada)) {
                    Map<String, Object> actualizacion = new HashMap<>();
                    actualizacion.put("latitud", nuevaLat);
                    actualizacion.put("longitud", nuevaLong);

                    dbRef.updateChildren(actualizacion)
                            .addOnSuccessListener(aVoid -> {
                                callback.onSuccess();  // Notifica éxito al exterior
                            })
                            .addOnFailureListener(e -> {
                                callback.onFailure("Error al actualizar ubicación: " + e.getMessage());
                            });

                } else {
                    callback.onFailure("Contraseña incorrecta");
                }
            } else {
                callback.onFailure("Usuario no encontrado");
            }
        }).addOnFailureListener(e -> {
            callback.onFailure("Error de conexión: " + e.getMessage());
        });
    }
}
