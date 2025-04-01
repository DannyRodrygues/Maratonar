package br.edu.utfpr.daniellarodrigues.maratonar;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

import br.edu.utfpr.daniellarodrigues.maratonar.modelo.Filme;
import br.edu.utfpr.daniellarodrigues.maratonar.persistencia.FilmesDatabase;
import br.edu.utfpr.daniellarodrigues.maratonar.utils.UtilsAlert;


public class FilmesActivity extends AppCompatActivity {

    private ListView lstVFilmes;
    private List<Filme> listaFilmes;
    private FilmeAdapter adapterFilme;
    private int posicaoSelecionada = -1;
    private ActionMode actionMode;
    private View viewSelecionada;
    private Drawable backgroundDrawable;
    public static final String ARQUIVO_PREFERENCIAS = "br.edu.utfpr.daniellarodrigues.maratonar.PREFERENCIAS";
    private boolean ordenacaoAscendente = PADRAO_INICIAL_ORDENACAO_ASCENDENTE;
    public static final String KEY_ORDENACAO_ASCENDENTE = "ORDENACAO_ASCENDENTE";
    public static final boolean PADRAO_INICIAL_ORDENACAO_ASCENDENTE = true;
    private MenuItem menuItemOrdenacao;

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
                editarFilme();
                return true;

            } else if (idMenuItem == R.id.menuItemExcluir) {
                excluirFilme();
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

            lstVFilmes.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filmes);

        setTitle(getString(R.string.controle_de_filmes));

        lstVFilmes = findViewById(R.id.lstVFilmes);
        popularListaFilmes();

        // Configura o evento de clique nos itens da lista
        lstVFilmes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             Filme filme = (Filme) lstVFilmes.getItemAtPosition(position);
                // Obtém o array de classificações do XML
                String[] classificacoes = getResources().getStringArray(R.array.classificacao);
                // Converte a classificação numérica para o texto correspondente
                String classificacaoTexto = classificacoes[filme.getClassificacao()];

                String mensagem = getString(R.string.titulotoast) + filme.getTitulo() + "\n" +
                        getString(R.string.categoriatoast) + filme.getCategoria() + "\n" +
                        (filme.isPrioridade() ? getString(R.string.prioridade) : getString(R.string.nao_prioridade)) + "\n" +
                        getString(R.string.status_filme) + filme.getStatus() + "\n" +
                        getString(R.string.classificacao_etariatoast) + classificacaoTexto;

                Toast.makeText(FilmesActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
        registerForContextMenu(lstVFilmes);
        lerPreferencias();
        popularListaFilmes();
    }


   private void popularListaFilmes(){

        FilmesDatabase database = FilmesDatabase.getInstance(this);
            if (ordenacaoAscendente){
                listaFilmes = database.getFilmeDao().queryAllAscending();
            }else {
                listaFilmes = database.getFilmeDao().queryAllDownward();
            }

        adapterFilme = new FilmeAdapter(this, listaFilmes);

        lstVFilmes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posicaoSelecionada = position;
                editarFilme();
            }
        });
             lstVFilmes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                 @Override
                 public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                     if (actionMode != null) {
                         return false;
                     }
                     posicaoSelecionada = position;
                     viewSelecionada = view;
                     backgroundDrawable = view.getBackground();
                     view.setBackgroundColor(getColor(R.color.corSelecionada));

                     lstVFilmes.setEnabled(false);

                     actionMode = startSupportActionMode(actionCallback);

                     return true;
                 }
             });
                 lstVFilmes.setAdapter(adapterFilme);
            }


   public void abrirSobre(){ //intent explicita

       Intent intentAbertura = new Intent(this, SobreActivity.class);
       startActivity(intentAbertura);
   }

   ActivityResultLauncher<Intent> launcherNovoFilme = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
           new ActivityResultCallback<ActivityResult>() {
               @Override
               public void onActivityResult(ActivityResult result) {
                   if (result.getResultCode() == FilmesActivity.RESULT_OK){
                       Intent intent = result.getData();

                       Bundle bundle = intent.getExtras();
                        if (bundle != null){
                            long id = bundle.getLong(FilmeActivity.KEY_ID);

                            FilmesDatabase database = FilmesDatabase.getInstance(FilmesActivity.this);

                            Filme filme = database.getFilmeDao().queryForId(id);

                            listaFilmes.add(filme);

                            ordenarLista();
                        }

                   }
               }
           });
   public void abrirNovoFilme(){
       Intent intentAbertura = new Intent(this, FilmeActivity.class);

       intentAbertura.putExtra(FilmeActivity.KEY_MODO, FilmeActivity.MODO_NOVO);
       launcherNovoFilme.launch(intentAbertura);
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.filmes_opcoes, menu);
       menuItemOrdenacao = menu.findItem(R.id.menuItemOrdenacao);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
       atualizarIconeOrdenacao();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       int idMenuItem =  item.getItemId();

       if (idMenuItem == R.id.menuItemAdicionar){
            abrirNovoFilme();
            return true;
       } else if (idMenuItem == R.id.menuItemSobre) {
            abrirSobre();
            return true;
       }else
       if (idMenuItem == R.id.menuItemOrdenacao) {
           salvarPreferenciaOrdenacaoAscendente(!ordenacaoAscendente);
           atualizarIconeOrdenacao();
           ordenarLista();
            return true;
       }else
           if (idMenuItem == R.id.menuItemRestaurar){
               confirmarRestaurarPadroes();
               return true;
        }else{
               return super.onOptionsItemSelected(item);
       }
    }

    private void excluirFilme(){

      final Filme filme = listaFilmes.get(posicaoSelecionada);

       //String mensagem = getString(R.string.deseja_deletar) + filme.getTitulo() + "\"";

        String mensagem = getString(R.string.deseja_deletar, filme.getTitulo());

        DialogInterface.OnClickListener listenerSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FilmesDatabase database = FilmesDatabase.getInstance(FilmesActivity.this);

               int quantidadeAlterada = database.getFilmeDao().delete(filme);
                if (quantidadeAlterada != 1){
                   UtilsAlert.mostrarAviso(FilmesActivity.this, R.string.erro_ao_tentar_deletar);
                   return;
               }

                listaFilmes.remove(posicaoSelecionada);
                adapterFilme.notifyDataSetChanged();
                actionMode.finish();
            }
        };

        UtilsAlert.confirmarAcao(this, mensagem, listenerSim, null);

    }
    ActivityResultLauncher<Intent> launcherEditarFilme = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == FilmesActivity.RESULT_OK){
                        Intent intent = result.getData();

                        Bundle bundle = intent.getExtras();
                        if (bundle != null){

                            final Filme filmeOriginal = listaFilmes.get(posicaoSelecionada);

                            long id = bundle.getLong(FilmeActivity.KEY_ID);

                           final FilmesDatabase database = FilmesDatabase.getInstance(FilmesActivity.this);

                           final Filme filmeEditado = database.getFilmeDao().queryForId(id);

                           listaFilmes.set(posicaoSelecionada, filmeEditado);

                           ordenarLista();

                           final ConstraintLayout constraintLayout = findViewById(R.id.main);

                            Snackbar snackbar = Snackbar.make(constraintLayout,
                                                             R.string.alteracao_realizada,
                                                             Snackbar.LENGTH_LONG);

                            snackbar.setAction(R.string.desfazer, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    int quantidadeAlterada = database.getFilmeDao().update(filmeOriginal);
                                    if (quantidadeAlterada != 1){
                                        UtilsAlert.mostrarAviso(FilmesActivity.this, R.string.erro_ao_tentar_alterar);
                                        return;
                                    }

                                    listaFilmes.remove(filmeEditado);
                                    listaFilmes.add(filmeOriginal);

                                    ordenarLista();

                                }
                            });
                                snackbar.show();
                        }
                    }
                    posicaoSelecionada = -1;

                    if (actionMode !=null){
                        actionMode.finish();
                    }
                }
            });

    private void editarFilme(){

        Filme filme = listaFilmes.get(posicaoSelecionada);

        Intent intentAbertura = new Intent(this, FilmeActivity.class);

        intentAbertura.putExtra(FilmeActivity.KEY_MODO, FilmeActivity.MODO_EDITAR);

        intentAbertura.putExtra(FilmeActivity.KEY_ID, filme.getId());


        launcherEditarFilme.launch(intentAbertura);

    }
    private void ordenarLista(){
        if (ordenacaoAscendente){
            Collections.sort(listaFilmes, Filme.ordenacaoCrescente);
        }else{
            Collections.sort(listaFilmes, Filme.ordenacaoDecrescente);
        }
        adapterFilme.notifyDataSetChanged();
    }

    private void atualizarIconeOrdenacao(){
        if (ordenacaoAscendente){
            menuItemOrdenacao.setIcon(R.drawable.ic_action_ascending_order);
        }else{
            menuItemOrdenacao.setIcon(R.drawable.ic_action_descending_order);
        }
    }
    private void lerPreferencias(){
        SharedPreferences shared = getSharedPreferences(ARQUIVO_PREFERENCIAS, Context.MODE_PRIVATE);
            ordenacaoAscendente = shared.getBoolean(KEY_ORDENACAO_ASCENDENTE, ordenacaoAscendente);
    }

    private void salvarPreferenciaOrdenacaoAscendente(boolean novoValor){
        SharedPreferences shared = getSharedPreferences(ARQUIVO_PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(KEY_ORDENACAO_ASCENDENTE, novoValor);
        editor.commit();
        ordenacaoAscendente = novoValor;
    }

    private void confirmarRestaurarPadroes(){
        DialogInterface.OnClickListener listenerSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restaurarPadroes();
                atualizarIconeOrdenacao();
                ordenarLista();

                Toast.makeText(FilmesActivity.this,
                        R.string.as_configura_es_retornaram_para_o_padr_o_de_instala_o,
                        Toast.LENGTH_LONG).show();
            }
        };

        UtilsAlert.confirmarAcao(this, getString(R.string.deseja_retornar_padroes), listenerSim, null);
    }
    private void restaurarPadroes(){
        SharedPreferences shared = getSharedPreferences(ARQUIVO_PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.clear();
        editor.commit();

        ordenacaoAscendente = PADRAO_INICIAL_ORDENACAO_ASCENDENTE;
    }

}
