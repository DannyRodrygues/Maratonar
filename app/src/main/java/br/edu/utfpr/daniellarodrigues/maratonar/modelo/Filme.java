package br.edu.utfpr.daniellarodrigues.maratonar.modelo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

@Entity
public class Filme implements Cloneable{
    public static Comparator<Filme> ordenacaoCrescente = new Comparator<Filme>() {

        @Override
        public int compare(Filme f1, Filme f2) {
            return f1.getTitulo().compareToIgnoreCase(f2.getTitulo());
        }
    };
    public static Comparator<Filme> ordenacaoDecrescente = new Comparator<Filme>() {

        @Override
        public int compare(Filme f1, Filme f2) {
            return -1 * f1.getTitulo().compareToIgnoreCase(f2.getTitulo());
        }
    };
    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    @ColumnInfo(index = true)
    private String titulo;
    private String categoria;
    private boolean prioridade;
    private int classificacao;

    private Status status;
    private LocalDate dataAssistido;
    public Filme(Status status, int classificacao, boolean prioridade, String categoria, String titulo, LocalDate dataAssistido) {
        this.status = status;
        this.classificacao = classificacao;
        this.prioridade = prioridade;
        this.categoria = categoria;
        this.titulo = titulo;
        this.dataAssistido = dataAssistido;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {

        this.id = id;
    }

    public String getTitulo() {

        return titulo;
    }

    public void setTitulo(String titulo) {

        this.titulo = titulo;
    }

    public String getCategoria() {

        return categoria;
    }

    public void setCategoria(String categoria) {

        this.categoria = categoria;
    }

    public boolean isPrioridade() {

        return prioridade;
    }

    public void setPrioridade(boolean prioridade) {

        this.prioridade = prioridade;
    }

    public int getClassificacao() {

        return classificacao;
    }

    public void setClassificacao(int classificacao) {

        this.classificacao = classificacao;
    }

    public Status getStatus() {

        return status;
    }

    public void setStatus(Status status) {

        this.status = status;
    }

    public LocalDate getDataAssistido() {
        return dataAssistido;
    }

    public void setDataAssistido(LocalDate dataAssistido) {
        this.dataAssistido = dataAssistido;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        /*
        Classe somente com atributos primitivos e imutaveis.
        por este motivo  metodo clone da classe pai ja resolve.
         */
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Filme filme = (Filme) o;

        if (dataAssistido == null && filme.dataAssistido != null){
            return false;
        }

        if (dataAssistido != null && dataAssistido.equals(filme.dataAssistido) == false){
            return false;
        }

        return prioridade == filme.prioridade
                && classificacao == filme.classificacao
                && titulo.equals(filme.titulo)
                && categoria.equals(filme.categoria)
                && status == filme.status;

    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, categoria, prioridade, classificacao, status, dataAssistido);
    }

    @Override
    public String toString() {
        return titulo + "\n" +
                categoria + "\n" +
                prioridade + "\n" +
                classificacao + "\n" +
                status+ "\n" +
                dataAssistido;
    }
}
