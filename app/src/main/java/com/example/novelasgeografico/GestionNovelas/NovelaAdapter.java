package com.example.novelasgeografico.GestionNovelas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.novelasgeografico.R;

import java.util.List;

//Clase NovelaAdapter que extiende BaseAdapter y se utiliza para mostrar la lista de novelas
public class NovelaAdapter extends RecyclerView.Adapter<NovelaAdapter.NovelaViewHolder> {

    //Atributos
    private Context context;
    private List<Novela> novelas;
    private OnItemClickListener listener;

    //Interfaz OnItemClickListener para manejar el evento de clic en un elemento de la lista
    public interface OnItemClickListener {
        void onItemClick(Novela novela);
    }

    //Constructor
    public NovelaAdapter(Context context, List<Novela> novelas, OnItemClickListener listener) {
        this.context = context;
        this.novelas = novelas;
        this.listener = listener;
    }

    //Metodo para crear una nueva vista
    @NonNull
    @Override
    public NovelaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_novela, parent, false);
        return new NovelaViewHolder(view);
    }

    //Metodo para enlazar los datos de la lista con los elementos de la vista
    @Override
    public void onBindViewHolder(@NonNull NovelaViewHolder holder, int position) {
        Novela novela = novelas.get(position);
        holder.textViewTitulo.setText(novela.getTitulo());
        holder.textViewAutor.setText(novela.getAutor());
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onItemClick(novela));
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    //Metodo para obtener el número de elementos en la lista
    @Override
    public int getItemCount() {
        return novelas.size();
    }

    //Clase NovelaViewHolder que extiende RecyclerView.ViewHolder y se utiliza para mantener las referencias de los elementos de la vista
    public static class NovelaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo;
        TextView textViewAutor;

        public NovelaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewAutor = itemView.findViewById(R.id.textViewAutor);
        }
    }

    //Getters & Setters
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }
    public List<Novela> getNovelas() {
        return novelas;
    }
    public void setNovelas(List<Novela> novelas) {
        this.novelas = novelas;
    }
    public OnItemClickListener getListener() {
        return listener;
    }
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}