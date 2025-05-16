package com.example.a22100178_geochat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import POJOs.Usuario;

public class login extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
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
            dbRef.child(mi_usuario.getNombre()).setValue(datos)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(getApplicationContext(), "¡El registro fue exitoso!", Toast.LENGTH_SHORT).show();
                // Redirigir a MainActivity
                if (checkLocationPermissions()) {
                    goToMainActivity();
                } else {
                    requestLocationPermissions();
                }


            })
            .addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        }
    }
    private boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        }, LOCATION_PERMISSION_REQUEST_CODE);
    }
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // opcional, para que no se pueda volver con el botón "atrás"
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                goToMainActivity();
            } else {
                Toast.makeText(this, "Se requieren permisos de ubicación para continuar", Toast.LENGTH_LONG).show();
            }
        }
    }

}