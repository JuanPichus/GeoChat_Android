package com.example.a22100178_geochat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import POJOs.Usuario;

public class login extends AppCompatActivity {

    EditText et_username;
    Button btn_register;
    Usuario mi_usuario = new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //permisos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1001);
        }


        et_username = findViewById(R.id.et_username);
        btn_register = findViewById(R.id.btn_register);



        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
    }

    public void registrarUsuario(){
        //Validar campo no vacio
        if(!et_username.getText().toString().isEmpty()){
            mi_usuario.setNombre(et_username.getText().toString().trim());

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Usuarios");

            //Iniciar datos de ubicacion en 0s
            Map<String, Object> datos = new HashMap<>();
            datos.put("latitud", 0);
            datos.put("longitud", 0);

            // Guardar usando el nombre de usuario como clave
            dbRef.child(mi_usuario.getNombre()).setValue(datos);

        }
    }
}