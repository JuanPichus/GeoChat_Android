package com.example.a22100178_geochat;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import POJOs.Mensaje;
import POJOs.Usuario;
import managers.ServerManager;

public class chat extends AppCompatActivity {

    Usuario myUser = new Usuario();
    ServerManager myServerManager = new ServerManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //metodo para enviar mensaje, pongalo despues de las validaciones de datos y cambien los parametros
        myServerManager.sendMessage(myUser.getNombre(), "Username_prueba", "Mensaje_prueba", new ServerManager.ChatCallback() {
            @Override
            public void onSuccess() {
                //Si el mensaje se envio con exito, añadan logica aqui para que ocurre despues
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        //Este metodo es el que actualiza la lista de mensajes
        myServerManager.messageListener(myUser.getNombre(), "username-prueba", new ServerManager.messageListenerCallback() {
            @Override
            public void onMensajesRecibidos(List<Mensaje> mensajes) {
                //implementen aqui la logica de que hacer con los nuevos mensajes; devuelve una list de la mensajes (objetos mensaje)

            }

            @Override
            public void onError(String error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });


    }






}