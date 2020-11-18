package com.ovalle.basedatosfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ovalle.basedatosfirebase.entidades.Usuario;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    // objetos de Firebase
    private FirebaseDatabase database;
    private DatabaseReference reference;
    // Widget
    private EditText txtNombre;
    private Spinner spGenero;
    private Button btnCrear, btnActualizar, btnEliminar;
    private RecyclerView recyclerUsuarios;
    // Ayuda para cargar el recycler
    ArrayList<Usuario> arrayListUsuario;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Referencias a los widget
        txtNombre = findViewById(R.id.txtnombre);
        spGenero = findViewById(R.id.spGenero);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnCrear = findViewById(R.id.btnCrear);
        btnEliminar = findViewById(R.id.btnEliminar);
        recyclerUsuarios = findViewById(R.id.recyclerUsuarios);
        // Configurar recycler
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));
        // Inicializar ArrayList
        arrayListUsuario = new ArrayList<>();
        // Conexion con firebase
        conectarFirebase();
        cargarRecycler();

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Usuario").child(usuario.getId()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        mensajeToast("El usuario ha sido eliminado");
                    }
                });
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newNombre = txtNombre.getText().toString();
                String newGenero = spGenero.getSelectedItem().toString();
                usuario.setNombre(newNombre);
                usuario.setGenero(newGenero);
                reference.child("Usuario").child(usuario.getId()).setValue(usuario, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        mensajeToast("El usuario ha sido actualizado");
                    }
                });
            }
        });

        recyclerUsuarios.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View view = rv.findChildViewUnder(e.getX(), e.getY());
                if (view != null){
                    int position = rv.getChildAdapterPosition(view);
                    usuario = arrayListUsuario.get(position);
                    txtNombre.setText(usuario.getNombre());
                    if (usuario.getGenero().equals("Masculino")){
                        spGenero.setSelection(1);
                    }else if(usuario.getGenero().equals("Femenino")){
                        spGenero.setSelection(2);
                    }else{
                        spGenero.setSelection(3);
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {


            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Capturar datos de UI
                if (spGenero.getSelectedItemPosition() == 0) {
                    mensajeToast("Seleccione un genero");
                }else if (txtNombre.getText().toString() == null) {
                    mensajeToast("El nombre esta vacio");
                } else {
                    String id = UUID.randomUUID().toString();
                    String nombre = txtNombre.getText().toString().trim();
                    String genero = spGenero.getSelectedItem().toString();
                    // Generar objeto de la clase usuario
                    Usuario usuario = new Usuario(id, nombre, genero);
                    insertarUsuario(usuario);
                    limpiarFormulario();
                }
            }
        });
    }

    public void mensajeToast(String mensaje){
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
    }
    public void conectarFirebase(){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        if (reference != null){
            Toast.makeText(this, "Conectado a Firebase", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Error al conectar con Firebase", Toast.LENGTH_LONG).show();
            conectarFirebase();
        }
    }

    public void insertarUsuario(Usuario user){
        if (user != null){
            reference.child("Usuario").child(user.getId()).setValue(user, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Toast.makeText(getApplicationContext(), "Usuario creado", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void cargarRecycler(){
        reference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayListUsuario.clear();
                for (DataSnapshot dato : snapshot.getChildren()) {
                    usuario = dato.getValue(Usuario.class);
                    arrayListUsuario.add(usuario);
                }
                // Creacion del adaptar
                UsuariosAdapter adapter = new UsuariosAdapter(arrayListUsuario);
                // Establecer el adaptador en el recycler
                recyclerUsuarios.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void limpiarFormulario(){
        txtNombre.setText(null);
        spGenero.setSelection(0);
    }
}