package br.edu.utfpr.daniellarodrigues.maratonar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.utfpr.daniellarodrigues.maratonar.modelo.Anotacao;
import br.edu.utfpr.daniellarodrigues.maratonar.utils.UtilsLocalDateTime;

public class AnotacaoAdapter extends BaseAdapter {

    private Context context;
    private List<Anotacao> listaAnotacoes;


    private static class AnotacaoHolder {
        public TextView txtVValorDiaHoraCriacao;
        public TextView txtVValorTexto;
    }

    public AnotacaoAdapter(Context context, List<Anotacao> listaAnotacoes) {
        this.context = context;
        this.listaAnotacoes = listaAnotacoes;
    }

    @Override
    public int getCount() {
        return listaAnotacoes.size();
    }

    @Override
    public Object getItem(int position) {
        return listaAnotacoes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AnotacaoHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.minha_lista_anotacoes, parent, false);


            holder = new AnotacaoHolder();

            holder.txtVValorDiaHoraCriacao = convertView.findViewById(R.id.txtVValorDiaHoraCriacao);
            holder.txtVValorTexto = convertView.findViewById(R.id.txtVValorTexto);

            convertView.setTag(holder);
        } else {
            holder = (AnotacaoHolder) convertView.getTag();
        }

        Anotacao anotacao = listaAnotacoes.get(position);

        holder.txtVValorDiaHoraCriacao.setText(UtilsLocalDateTime.formatLocalDateTime(anotacao.getDiaHoraCriacao()));
        holder.txtVValorTexto.setText(anotacao.getTexto());

        return convertView;
    }
}