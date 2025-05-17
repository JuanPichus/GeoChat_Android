package managers;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ServerManager {

    public ServerManager(){

    }

    public interface LoginCallback {
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

    public void sendLocation(){

    }
}
