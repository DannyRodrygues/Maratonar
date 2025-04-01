package br.edu.utfpr.daniellarodrigues.maratonar;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


import br.edu.utfpr.daniellarodrigues.maratonar.modelo.Anotacao;
import br.edu.utfpr.daniellarodrigues.maratonar.modelo.Filme;
import br.edu.utfpr.daniellarodrigues.maratonar.persistencia.FilmesDatabase;
import br.edu.utfpr.daniellarodrigues.maratonar.utils.UtilsAlert;


public class AnotacoesActivity extends AppCompatActivity {

    public static final String KEY_ID_FILME = "KEY_ID_FILME";
    private ListView lstVAnotacoes;
    private List<Anotacao> listaAnotacoes;
    private AnotacaoAdapter adapterAnotacao;
    private int posicaoSelecionada = -1;
    private ActionMode actionMode;
    private View viewSelecionada;
    private Drawable backgroundDrawable;
    private Filme filme;

    private ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.item_selecionado, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int idMenuItem = item.getItemId();

            if (idMenuItem == R.id.menuItemEditar){
                editarAnotacao();
                return true;

            } else if (idMenuItem == R.id.menuItemExcluir) {
                excluirAnotacao();
                return true;

            }else{
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (viewSelecionada != null){
                viewSelecionada.setBackground(backgroundDrawable);
            }
            actionMode         = null;
            viewSelecionada    = null;
            backgroundDrawable = null;

            lstVAnotacoes.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotacoes);

        Intent intentAbertura = getIntent();
        Bundle bundle = intentAbertura.getExtras();

        if (bundle !=null){
            long id = bundle.getLong(KEY_ID_FILME);
            FilmesDatabase database = FilmesDatabase.getInstance(this);
            filme = database.getFilmeDao().queryForId(id);

            setTitle(getString(R.string.anotacoes_do_filme, filme.getTitulo()));
        }else {
            //falta patrametro da abertura da activity
        }


        lstVAnotacoes = findViewById(R.id.lstVAnotacoes);
        popularListaAnotacoes();

        // Configura o evento de clique nos itens da lista
        lstVAnotacoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Filme filme = (Filme) lstVAnotacoes.getItemAtPosition(position);
                // Obtém o array de classificações do XML
                String[] classificacoes = getResources().getStringArray(R.array.classificacao);
                // Converte a classificação numérica para o texto correspondente
                String classificacaoTexto = classificacoes[filme.getClassificacao()];

                String mensagem = getString(R.string.titulotoast) + filme.getTitulo() + "\n" +
                        getString(R.string.categoriatoast) + filme.getCategoria() + "\n" +
                        (filme.isPrioridade() ? getString(R.string.prioridade) : getString(R.string.nao_prioridade)) + "\n" +
                        getString(R.string.status_filme) + filme.getStatus() + "\n" +
                        getString(R.string.classificacao_etariatoast) + classificacaoTexto;

                Toast.makeText(AnotacoesActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
        registerForContextMenu(lstVAnotacoes);

        popularListaAnotacoes();
    }


    private void popularListaAnotacoes(){

        FilmesDatabase database = FilmesDatabase.getInstance(this);
        listaAnotacoes = database.getAnotacaoDao().queryForIdFilme(filme.getId());

        adapterAnotacao = new AnotacaoAdapter(this, listaAnotacoes);

        lstVAnotacoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posicaoSelecionada = position;
                editarAnotacao();
            }
        });
        lstVAnotacoes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (actionMode != null) {
                    return false;
                }
                posicaoSelecionada = position;
                viewSelecionada = view;
                backgroundDrawable = view.getBackground();
                view.setBackgroundColor(getColor(R.color.corSelecionada));

                lstVAnotacoes.setEnabled(false);

                actionMode = startSupportActionMode(actionCallback);

                return true;
            }
        });
        lstVAnotacoes.setAdapter(adapterAnotacao);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anotacoes_opcoes, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem =  item.getItemId();

        if (idMenuItem == R.id.menuItemAdicionar){
            novaAnotacao();
            return true;
        } else

        if (idMenuItem == android.R.id.home){
           finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void excluirAnotacao(){

        final Anotacao anotacao = listaAnotacoes.get(posicaoSelecionada);

        DialogInterface.OnClickListener listenerSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FilmesDatabase database = FilmesDatabase.getInstance(AnotacoesActivity.this);

                int quantidadeAlterada = database.getAnotacaoDao().delete(anotacao);
                if (quantidadeAlterada != 1){
                    UtilsAlert.mostrarAviso(AnotacoesActivity.this, R.string.erro_ao_tentar_deletar);
                    return;
                }

                listaAnotacoes.remove(posicaoSelecionada);
                adapterAnotacao.notifyDataSetChanged();
                posicaoSelecionada = -1;
                actionMode.finish();
            }
        };

        UtilsAlert.confirmarAcao(this, R.string.deseja_deletar_anotacao, listenerSim, null);

    }

    private void novaAnotacao(){

        UtilsAlert.OnTextEnteredListener listener = new UtilsAlert.OnTextEnteredListener() {

            @Override
            public void onTextEntered(String texto) {

                if (texto == null || texto.trim().isEmpty()){
                    UtilsAlert.mostrarAviso(AnotacoesActivity.this, R.string.texto_nao_pode_ser_vazio);
                    return;
                }

                Anotacao anotacao = new Anotacao(filme.getId(), LocalDateTime.now(), texto);

                FilmesDatabase database = FilmesDatabase.getInstance(AnotacoesActivity.this);

                long novoId = database.getAnotacaoDao().insert(anotacao);

                if (novoId <= 0){
                    UtilsAlert.mostrarAviso(AnotacoesActivity.this, R.string.erro_ao_tentar_inserir);
                    return;
                }

                anotacao.setId(novoId);

                listaAnotacoes.add(anotacao);

                Collections.sort(listaAnotacoes, Anotacao.ordenacaoDescrescente);
                adapterAnotacao.notifyDataSetChanged();
            }
        };

        UtilsAlert.lerTexto(this, R.string.nova_anotacao, R.layout.entrada_anotacoes, R.id.editTxtTexto, null, listener);
    }



    private void editarAnotacao(){

        final Anotacao anotacao = listaAnotacoes.get(posicaoSelecionada);

        UtilsAlert.OnTextEnteredListener listener = new UtilsAlert.OnTextEnteredListener() {

            @Override
            public void onTextEntered(String texto) {

                if (texto == null || texto.trim().isEmpty()){
                    UtilsAlert.mostrarAviso(AnotacoesActivity.this, R.string.texto_nao_pode_ser_vazio);
                    return;
                }

                if (texto.equalsIgnoreCase(anotacao.getTexto())){
                    // O texto é o mesmo, não faz nada
                    return;
                }

                anotacao.setTexto(texto);

                FilmesDatabase database = FilmesDatabase.getInstance(AnotacoesActivity.this);

                long novoId = database.getAnotacaoDao().update(anotacao);

                if (novoId <= 0){
                    UtilsAlert.mostrarAviso(AnotacoesActivity.this, R.string.erro_ao_tentar_alterar);
                    return;
                }

                adapterAnotacao.notifyDataSetChanged();

                posicaoSelecionada = -1;

                if (actionMode != null){
                    actionMode.finish();
                }
            }
        };

        UtilsAlert.lerTexto(this, R.string.edite_texto_anotacao, R.layout.entrada_anotacoes, R.id.editTxtTexto,
                anotacao.getTexto(), listener);

    }
}
