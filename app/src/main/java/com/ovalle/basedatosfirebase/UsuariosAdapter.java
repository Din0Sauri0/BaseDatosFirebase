package com.ovalle.basedatosfirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ovalle.basedatosfirebase.entidades.Usuario;

import java.util.ArrayList;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ViewHolderDatos> {
    ArrayList<Usuario> usuarios;
    Usuario usuario;

    public UsuariosAdapter(ArrayList<Usuario> usuarios){
        this.usuarios = usuarios;
    }

    @NonNull
    @Override
    public UsuariosAdapter.ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuario_item, parent, false);
        ViewHolderDatos vhd = new ViewHolderDatos(view);
        return vhd;
    }

    @Override
    public void onBindViewHolder(@NonNull UsuariosAdapter.ViewHolderDatos holder, int position) {
        holder.cargarUsuario(usuarios.get(position));
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        ImageView imgUsuario;
        TextView txtNombreUsuario;
        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            // Referencias
            imgUsuario = itemView.findViewById(R.id.imgUsuario);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
        }
        public void cargarUsuario(Usuario usuario){
            txtNombreUsuario.setText(usuario.getNombre());
        }
    }
}
