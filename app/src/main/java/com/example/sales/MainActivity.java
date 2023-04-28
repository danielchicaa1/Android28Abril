package com.example.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // Instanciar la clase de FirebaseFirestore para el CRUD de la colección de users
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText etName, etUsername, etPassword;
    Button btnSave, btnSearch, btnedit, btndelete;
    String idUserFind;
    String findUsername;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Referenciar los objetos de cada id
        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSave = findViewById(R.id.btnSave);
        btnSearch = findViewById(R.id.btnSearch);
        btnedit = findViewById(R.id.btnEdit);
        btndelete = findViewById(R.id.btnDelete);
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(idUserFind)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(),"Usuario eliminado correctamente",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etUsername.getText().toString().isEmpty() && !etName.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()){
                    if (!findUsername.equals(etUsername.getText().toString())){ // Si el username encontrado es diferente al nuevo
                        // Búsqueda del usuario en la colección users
                        db.collection("users")
                                .whereEqualTo("username",etUsername.getText().toString())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){
                                            if (task.getResult().isEmpty()){ // No encontró el nuevo username
                                                // Actualizar los datos del usuario
                                                Map<String, Object> user = new HashMap<>();
                                                user.put("name", etName.getText().toString());
                                                user.put("username", etUsername.getText().toString());
                                                user.put("password", etPassword.getText().toString());
                                                db.collection("users").document(idUserFind)
                                                        .set(user)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(getApplicationContext(),"Usuario actualizado correctamente...",Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getApplicationContext(),"Error de conexión a la base de datos",Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(),"Usuario Existe. Inténelo con otro...",Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }
                                });
                    }
                    else{ // Son iguales los usernames (anterior y el actual), es decir, no se cambia el username
                        // Actualizar los datos del usuario
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", etName.getText().toString());
                        user.put("username", etUsername.getText().toString());
                        user.put("password", etPassword.getText().toString());
                        db.collection("users").document(idUserFind)
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getApplicationContext(),"Usuario actualizado correctamente...",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"Error de conexión a la base de datos",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Debe ingresar todos los datos",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verificar que el nombre del usuario se haya digitado
                if (!etUsername.getText().toString().isEmpty()){
                    // Búsqueda del usuario en la colección users
                    db.collection("users")
                            .whereEqualTo("username",etUsername.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (!task.getResult().isEmpty()){
                                            // Encontró el documento con el username específico
                                            for (QueryDocumentSnapshot document : task.getResult()){
                                                findUsername = document.getString("username");
                                                idUserFind = document.getId(); // devuelve id del documento
                                                // Asignar el comtenido de cada campo a su control respectivo
                                                etName.setText(document.getString("name"));
                                                //etPassword.setText(document.getString("password"));
                                            }
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(),"Usuario NO existe. Inténelo con otro...",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            });
                }
                else{
                    Toast.makeText(getApplicationContext(),"Debe ingresar el usuario a buscar ...",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verificar que el nombre del usuario se haya digitado
                if (!etUsername.getText().toString().isEmpty() && !etName.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()){
                    // Búsqueda del usuario en la colección users
                    db.collection("users")
                            .whereEqualTo("username",etUsername.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().isEmpty()){
                                            // No Encontró el documento con el username específico
                                            // Create a new user with a first and last name
                                            Map<String, Object> user = new HashMap<>();
                                            user.put("name", etName.getText().toString());
                                            user.put("username", etUsername.getText().toString());
                                            user.put("password", etPassword.getText().toString());

                                            // Add a new document with a generated ID
                                            db.collection("users")
                                                    .add(user)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(getApplicationContext(),"Usuario creado correctamente... ",Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(),"Error al crear el usuario: "+e,Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(),"Usuario Existente. Inténelo con otro ...",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            });
                }
                else{
                    Toast.makeText(getApplicationContext(),"Debe ingresar todos los datos ...",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}