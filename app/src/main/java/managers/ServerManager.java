package managers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import POJOs.MapUser;
import POJOs.Mensaje;

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

    public interface ChatCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface messageListenerCallback {
        void onMensajesRecibidos(List<Mensaje> mensajes);
        void onError(String error);
    }

    public interface UsuarioListCallback {
        void onSuccess(List<MapUser> mapUsers);
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

    public void getUserLocations(UsuarioListCallback callback) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Usuarios");

        dbRef.get().addOnSuccessListener(snapshot -> {
            List<MapUser> listaUsuarios = new ArrayList<>();

            for (DataSnapshot userSnap : snapshot.getChildren()) {
                String username = userSnap.getKey();
                Double latitud = userSnap.child("latitud").getValue(Double.class);
                Double longitud = userSnap.child("longitud").getValue(Double.class);

                if (username != null && latitud != null && longitud != null) {
                    MapUser usuarioEnMapa = new MapUser(username, latitud, longitud);
                    listaUsuarios.add(usuarioEnMapa);
                }
            }

            callback.onSuccess(listaUsuarios);

        }).addOnFailureListener(e -> {
            callback.onFailure("Error al obtener ubicaciones: " + e.getMessage());
        });
    }


    public void sendMessage(String emisor, String receptor, String texto, ChatCallback callback) {
        String chatId = getChatId(emisor, receptor);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId);

        Mensaje mensaje = new Mensaje(emisor, receptor, texto);

        dbRef.push().setValue(mensaje)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    private String getChatId(String user1, String user2) {
        return (user1.compareTo(user2) < 0) ? user1 + "_" + user2 : user2 + "_" + user1;
    }

    public void messageListener(String usuario1, String usuario2, messageListenerCallback callback) {
        String chatId = getChatId(usuario1, usuario2);
        DatabaseReference chatRef = FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(chatId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Mensaje> mensajes = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Mensaje mensaje = snap.getValue(Mensaje.class);
                    mensajes.add(mensaje);
                }
                callback.onMensajesRecibidos(mensajes);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

}
