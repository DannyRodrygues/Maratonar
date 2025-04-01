package br.edu.utfpr.daniellarodrigues.maratonar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;

import br.edu.utfpr.daniellarodrigues.maratonar.modelo.Filme;
import br.edu.utfpr.daniellarodrigues.maratonar.modelo.Status;
import br.edu.utfpr.daniellarodrigues.maratonar.persistencia.FilmesDatabase;
import br.edu.utfpr.daniellarodrigues.maratonar.utils.UtilsAlert;
import br.edu.utfpr.daniellarodrigues.maratonar.utils.UtilsLocalDate;

public class FilmeActivity extends AppCompatActivity {

    public static final String KEY_ID = "ID";
    public static final String KEY_MODO = "MODO";
    public static final String KEY_SUGERIR_CLASSIFICACAO = "SUGERIR_CLASSIFICACAO";
    public static final String KEY_ULTIMA_CLASSIFICACAO = "ULTIMA_CLASSIFICACAO";
    public static final int MODO_NOVO = 0;
    public static final int MODO_EDITAR = 1;
    private EditText txtEditTitulo, txtEditCategoria, txtEditDataAssistido;
    private CheckBox ckBoxPrioridade;
    private RadioGroup rdBtnMaoUsada;
    private Spinner spinnerClassificacao;
    private int modo;
    private RadioButton radioButtonAssistido, radioButtonNaoAssistido;
    private Filme filmeOriginal;
    private boolean sugerirClassificacao = false;
    private int ultimaClassificacao = 0;
    private LocalDate dataAssistido;

    private int anosParaTras;
    private Button buttonAnotacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filme);

        txtEditTitulo = findViewById(R.id.txtEditTitulo);
        txtEditCategoria = findViewById(R.id.txtEditCategoria);
        txtEditDataAssistido = findViewById(R.id.edtxtDataEstreia);
        ckBoxPrioridade = findViewById(R.id.ckBoxPrioridade);
        rdBtnMaoUsada = findViewById(R.id.rdBtnMaoUsada);
        spinnerClassificacao = findViewById(R.id.spinnerClassificacao);
        radioButtonAssistido = findViewById(R.id.rdBtnAssistido);
        radioButtonNaoAssistido = findViewById(R.id.rdBtnNaoAssistido);
        buttonAnotacoes = findViewById(R.id.btnAnotacoes);

        txtEditDataAssistido.setFocusable(false);
        txtEditDataAssistido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialog();
            }
        });

        lerPreferencias();

        anosParaTras = getResources().getInteger(R.integer.anos_para_tras);

        dataAssistido = LocalDate.now().minusYears(anosParaTras);

        Intent intentAbertura = getIntent();
        Bundle bundle = intentAbertura.getExtras();
        if (bundle != null) {
            modo = bundle.getInt(KEY_MODO);
            if (modo == MODO_NOVO) {
                setTitle(getString(R.string.novo_filme));

                if (sugerirClassificacao) {
                    spinnerClassificacao.setSelection(ultimaClassificacao);
                }
                ;
                buttonAnotacoes.setVisibility(View.INVISIBLE);

            } else {
                setTitle(getString(R.string.editar_filme));

                long id = bundle.getLong(KEY_ID);
                FilmesDatabase database = FilmesDatabase.getInstance(this);
                filmeOriginal = database.getFilmeDao().queryForId(id);


                txtEditTitulo.setText(filmeOriginal.getTitulo());
                txtEditCategoria.setText(filmeOriginal.getCategoria());

                dataAssistido = filmeOriginal.getDataAssistido();
                if (dataAssistido != null) {
                    //dataAssistido = filmeOriginal.getDataAssistido();
                    txtEditDataAssistido.setText(UtilsLocalDate.formatLocalDate(dataAssistido));
                }else{
                    txtEditDataAssistido.setText("");
                }

                //txtEditDataAssistido.setText(UtilsLocalDate.formatLocalDate(dataAssistido));

                ckBoxPrioridade.setChecked(filmeOriginal.isPrioridade());
                spinnerClassificacao.setSelection(filmeOriginal.getClassificacao());

                Status status = filmeOriginal.getStatus();

                if (status == Status.Assistido) {
                    radioButtonAssistido.setChecked(true);
                } else if (status == Status.Nao_Assistido) {
                    radioButtonNaoAssistido.setChecked(true);
                }

                txtEditTitulo.requestFocus();
                txtEditTitulo.setSelection(txtEditTitulo.getText().length());

                int totalAnotacoes = database.getAnotacaoDao().totalIdFilme(filmeOriginal.getId());

                buttonAnotacoes.setText(getString(R.string.anotacoes, totalAnotacoes));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!txtEditDataAssistido.getText().toString().isEmpty()){
            txtEditDataAssistido.setText(UtilsLocalDate.formatLocalDate(dataAssistido));
        }
    }

    private void mostrarDatePickerDialog() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dataAssistido = LocalDate.of(year, month + 1, dayOfMonth);
                txtEditDataAssistido.setText(UtilsLocalDate.formatLocalDate(dataAssistido));
            }
        };
        LocalDate dataParaPicker = dataAssistido != null ? dataAssistido : LocalDate.now();
        DatePickerDialog picker = new DatePickerDialog(this,
                listener,
                dataParaPicker.getYear(),
                dataParaPicker.getMonthValue() - 1,
                dataParaPicker.getDayOfMonth());
                //dataAssistido.getYear(),
               // dataAssistido.getMonthValue() - 1,
                //dataAssistido.getDayOfMonth());

        picker.show();
    }

    public void limparCampos() {

        final String titulo = txtEditTitulo.getText().toString();
        final String categoria = txtEditCategoria.getText().toString();
        final LocalDate dataEstreiaAnterior = dataAssistido;
        final boolean prioridade = ckBoxPrioridade.isChecked();
        final int classificacaoEtaria = spinnerClassificacao.getSelectedItemPosition();
        final int radioButtonId = rdBtnMaoUsada.getCheckedRadioButtonId();

        final ScrollView scrollView = findViewById(R.id.main);
        final View viewComFoco = scrollView.findFocus();

        txtEditTitulo.setText(null);
        txtEditCategoria.setText(null);
        txtEditDataAssistido.setText(null);
        dataAssistido = LocalDate.now().minusYears(anosParaTras);
        ckBoxPrioridade.setChecked(false);
        rdBtnMaoUsada.clearCheck();
        spinnerClassificacao.setSelection(0);

        txtEditTitulo.requestFocus();

        Snackbar snackbar = Snackbar.make(scrollView,
                R.string.as_entradas_foram_apagadas,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.desfazer, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEditTitulo.setText(titulo);
                txtEditCategoria.setText(categoria);
                dataAssistido = dataEstreiaAnterior;
                txtEditDataAssistido.setText(UtilsLocalDate.formatLocalDate(dataAssistido));
                ckBoxPrioridade.setChecked(prioridade);
                if (radioButtonId == R.id.rdBtnAssistido) {
                    radioButtonAssistido.setChecked(true);
                } else if (radioButtonId == R.id.rdBtnNaoAssistido) {
                    radioButtonNaoAssistido.setChecked(true);
                }

                rdBtnMaoUsada.clearCheck();

                spinnerClassificacao.setSelection(classificacaoEtaria);

                if (viewComFoco != null) {
                    viewComFoco.requestFocus();
                }
            }
        });

        snackbar.show();
    }

    public void salvarCampos() {
        String titulo = txtEditTitulo.getText().toString();
        if (titulo == null || titulo.trim().isEmpty()) {
            UtilsAlert.mostrarAviso(this, R.string.faltou_informar_o_titulo);
            txtEditTitulo.requestFocus();
            return;
        }
        if (!titulo.matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            UtilsAlert.mostrarAviso(this, R.string.o_titulo_deve_conter_apenas_letras);
            txtEditTitulo.requestFocus();
            txtEditTitulo.setSelection(0, txtEditTitulo.getText().toString().length());
            return;
        }

        String categoria = txtEditCategoria.getText().toString();
        if (categoria == null || categoria.trim().isEmpty()) {
            UtilsAlert.mostrarAviso(this, R.string.faltou_informar_a_categoria);
            txtEditCategoria.requestFocus();
            return;
        }
        if (!categoria.matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            UtilsAlert.mostrarAviso(this, R.string.a_categoria_deve_conter_apenas_letras);
            txtEditCategoria.requestFocus();
            txtEditCategoria.setSelection(0, txtEditCategoria.getText().toString().length());
            return;
        }

        String dataEstreiaString = txtEditDataAssistido.getText().toString();
        if (dataEstreiaString == null || dataEstreiaString.trim().isEmpty()) {
            UtilsAlert.mostrarAviso(this, R.string.faltou_informar_a_data_visualizada);
            txtEditDataAssistido.requestFocus(); // Retorna o foco ao campo
            return;
        }


        int radioButtonId = rdBtnMaoUsada.getCheckedRadioButtonId();
        Status status;
        if (radioButtonId == R.id.rdBtnAssistido) {
            status = Status.Assistido;
        } else if (radioButtonId == R.id.rdBtnNaoAssistido) {
            status = Status.Nao_Assistido;
        } else {
            UtilsAlert.mostrarAviso(this, R.string.faltou_preencher_o_status);
            return;
        }

        int classificacao = spinnerClassificacao.getSelectedItemPosition();
        if (classificacao == AdapterView.INVALID_POSITION) {
            UtilsAlert.mostrarAviso(this, R.string.selecione_a_classificacao_etaria);
            return;
        }

        boolean prioridade = ckBoxPrioridade.isChecked();

        Filme filme = new Filme(status, classificacao, prioridade, categoria, titulo, dataAssistido);

        if (filme.equals(filmeOriginal)) {
            setResult(FilmeActivity.RESULT_CANCELED);
            finish();
            return;
        }
        Intent intentResposta = new Intent();

        FilmesDatabase database = FilmesDatabase.getInstance(this);

        if (modo == MODO_NOVO) {
            long novoId = database.getFilmeDao().insert(filme);
            if (novoId <= 0) {
                UtilsAlert.mostrarAviso(this, R.string.erro_ao_tentar_inserir);
                return;
            }
            filme.setId(novoId);

        } else {
            filme.setId(filmeOriginal.getId());
            int quantidadeAlterada = database.getFilmeDao().update(filme);
            if (quantidadeAlterada != 1) {
                UtilsAlert.mostrarAviso(this, R.string.erro_ao_tentar_alterar);
                return;
            }
        }

        salvarUltimaClassificacao(classificacao);

        intentResposta.putExtra(KEY_ID, filme.getId());

        setResult(FilmeActivity.RESULT_OK, intentResposta);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filme_opcoes, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuItemSugerirClassificacao);
        item.setChecked(sugerirClassificacao);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();

        if (idMenuItem == R.id.menuItemSalvar) {
            salvarCampos();
            return true;
        } else if (idMenuItem == R.id.menuItemLimpar) {
            limparCampos();
            return true;
        } else {
            if (idMenuItem == R.id.menuItemSugerirClassificacao) {
                boolean valor = !item.isChecked();
                salvarSugerirClassificacao(valor);
                item.setChecked(valor);

                if (sugerirClassificacao) {
                    spinnerClassificacao.setSelection(ultimaClassificacao);
                }

                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        }
    }


    private void lerPreferencias() {
        SharedPreferences shared = getSharedPreferences(FilmesActivity.ARQUIVO_PREFERENCIAS, Context.MODE_PRIVATE);
        sugerirClassificacao = shared.getBoolean(KEY_SUGERIR_CLASSIFICACAO, sugerirClassificacao);
        ultimaClassificacao = shared.getInt(KEY_ULTIMA_CLASSIFICACAO, ultimaClassificacao);
    }

    private void salvarSugerirClassificacao(boolean novoValor) {
        SharedPreferences shared = getSharedPreferences(FilmesActivity.ARQUIVO_PREFERENCIAS, Context.MODE_PRIVATE); // Corrigido "ARQUVO" para "ARQUIVO"
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(KEY_SUGERIR_CLASSIFICACAO, novoValor);
        editor.commit();
        sugerirClassificacao = novoValor;
    }

    private void salvarUltimaClassificacao(int novoValor) {
        SharedPreferences shared = getSharedPreferences(FilmesActivity.ARQUIVO_PREFERENCIAS, Context.MODE_PRIVATE); // Corrigido "ARQUVO" para "ARQUIVO"
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(KEY_ULTIMA_CLASSIFICACAO, novoValor);
        editor.commit();
        ultimaClassificacao = novoValor;
    }

    ActivityResultLauncher<Intent> launcherAnotacoes = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult o) {

                    FilmesDatabase database = FilmesDatabase.getInstance(FilmeActivity.this);

                    int totalAnotacoes = database.getAnotacaoDao().totalIdFilme(filmeOriginal.getId());

                    buttonAnotacoes.setText(getString(R.string.anotacoes, totalAnotacoes));
                }
            });

    public void abrirAnotacoes(View view) {

        Intent intentAbertura = new Intent(this, AnotacoesActivity.class);

        intentAbertura.putExtra(AnotacoesActivity.KEY_ID_FILME, filmeOriginal.getId());

        launcherAnotacoes.launch(intentAbertura);
    }
}