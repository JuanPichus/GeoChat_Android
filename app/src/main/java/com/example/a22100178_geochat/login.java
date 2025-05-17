package com.example.a22100178_geochat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import POJOs.Usuario;
import managers.ServerManager;

public class login extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    EditText et_username;
    EditText et_pass;
    Button btn_register;
    Usuario myUser = new Usuario();

    ServerManager myServerManager = new ServerManager();

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
        et_pass = findViewById(R.id.et_password);
        btn_register = findViewById(R.id.btn_register);


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermissions()) {
                    registrarUsuario();
                } else {
                    requestLocationPermissions();
                }
            }
        });
    }

    public void registrarUsuario(){
        //Validar campo no vacio
        if(!et_username.getText().toString().isEmpty() && !et_pass.getText().toString().isEmpty()){
            myUser.setNombre(et_username.getText().toString().trim());
            myUser.setPassword(et_pass.getText().toString().trim());

            myServerManager.sendUser(myUser.getNombre(), myUser.getPassword(), new ServerManager.LoginCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "¡El registro fue exitoso!", Toast.LENGTH_SHORT).show();
                    goToNextActivity();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(getApplicationContext(), "Error de servidor: " + error, Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            Toast.makeText(getApplicationContext(),"Porfavor llene los campos...", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkLocationPermissions() {
        boolean fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return fine && coarse;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void goToNextActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (checkLocationPermissions()) {
                registrarUsuario();
            } else {
                Toast.makeText(this, "Se requieren permisos de ubicación para continuar", Toast.LENGTH_LONG).show();
            }
        }
    }
}