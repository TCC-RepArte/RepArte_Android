package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<AdicionarAmigoActivity.Usuario> usuarios;
    private AdicionarAmigoActivity.OnAddFriendClickListener listener;

    public UsuarioAdapter(List<AdicionarAmigoActivity.Usuario> usuarios, AdicionarAmigoActivity.OnAddFriendClickListener listener) {
        this.usuarios = usuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        AdicionarAmigoActivity.Usuario usuario = usuarios.get(position);
        holder.bind(usuario);
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public void updateUsers(List<AdicionarAmigoActivity.Usuario> newUsers) {
        this.usuarios = newUsers;
        notifyDataSetChanged();
    }

    class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private ImageView userAvatar;
        private TextView userName;
        private TextView userInterests;
        private Button btnAdd;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.user_avatar);
            userName = itemView.findViewById(R.id.user_name);
            userInterests = itemView.findViewById(R.id.user_interests);
            btnAdd = itemView.findViewById(R.id.btn_add);
        }

        public void bind(AdicionarAmigoActivity.Usuario usuario) {
            userName.setText(usuario.getNome());
            userInterests.setText(usuario.getInteresses());
            userAvatar.setImageResource(usuario.getAvatarResId());

            btnAdd.setOnClickListener(v -> {
                AppAnimationUtils.animateButtonClick(btnAdd, () -> {
                    if (listener != null) {
                        listener.onAddFriendClick(usuario);
                    }
                });
            });
        }
    }
} 