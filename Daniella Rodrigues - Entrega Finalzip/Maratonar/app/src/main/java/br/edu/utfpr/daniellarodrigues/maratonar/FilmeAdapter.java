package br.edu.utfpr.daniellarodrigues.maratonar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.utfpr.daniellarodrigues.maratonar.modelo.Filme;
import br.edu.utfpr.daniellarodrigues.maratonar.utils.UtilsLocalDate;

public class FilmeAdapter extends BaseAdapter {
    private Context context;

    private List<Filme> listaFilmes;
    private String[] classificacao;

    private static class FilmeHolder{
        public TextView txtVValorTitulo;
        public TextView txtVValorCategoria;
        public TextView txtVValorDataAssistido;
        public TextView txtVValorPrioridade;
        public TextView txtVValorClassificacao;
        public TextView txtVValorStatus;
    }
    public FilmeAdapter(Context context, List<Filme> listaFilmes) {
        this.context = context;
        this.listaFilmes = listaFilmes;

        classificacao = context.getResources().getStringArray(R.array.classificacao);
    }

    @Override
    public int getCount() {
        return listaFilmes.size();
    }

    @Override
    public Object getItem(int position) {
        return listaFilmes.get(position);

    }

    @Override
    public long getItemId(int position) {
        return 0;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FilmeHolder holder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.minha_lista_filmes, parent, false);

            holder = new FilmeHolder();

            holder.txtVValorTitulo = convertView.findViewById(R.id.txtVValorTitulo);
            holder.txtVValorCategoria = convertView.findViewById(R.id.txtVValorCategoria);
            holder.txtVValorDataAssistido = convertView.findViewById(R.id.txtVValorDataAssistido);
            holder.txtVValorPrioridade = convertView.findViewById(R.id.txtVValorPrioridade);
            holder.txtVValorClassificacao = convertView.findViewById(R.id.txtValorClassificacao);
            holder.txtVValorStatus = convertView.findViewById(R.id.txtVValorStatus);

            convertView.setTag(holder);

        }else {
            holder = (FilmeHolder) convertView.getTag();
        }

        Filme filme = listaFilmes.get(position);

        holder.txtVValorTitulo.setText(filme.getTitulo());
        holder.txtVValorCategoria.setText(filme.getCategoria());
        holder.txtVValorDataAssistido.setText(UtilsLocalDate.formatLocalDate(filme.getDataAssistido()));
       if (filme.isPrioridade()){
           holder.txtVValorPrioridade.setText(R.string.prioridade);
       }else {
           holder.txtVValorPrioridade.setText(R.string.nao_prioridade);
       }
        holder.txtVValorClassificacao.setText(classificacao[filme.getClassificacao()]);
       switch (filme.getStatus()){
           case Assistido:
                holder.txtVValorStatus.setText(R.string.assistido);
               break;

           case Nao_Assistido:
                holder.txtVValorStatus.setText(R.string.nao_assistido);
               break;
       }
        return convertView;
    }
}
