package br.edu.utfpr.daniellarodrigues.maratonar.modelo;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.Comparator;

@Entity(foreignKeys = {@ForeignKey(entity = Filme.class,
                                  parentColumns = "id",
                                  childColumns = "idFilme",
                                  onDelete = CASCADE)})
public class Anotacao {

    public static Comparator<Anotacao> ordenacaoDescrescente = new Comparator<Anotacao>() {
        @Override
        public int compare(Anotacao anotacao1, Anotacao anotacao2) {
            return -1 * anotacao1.getDiaHoraCriacao().compareTo(anotacao2.getDiaHoraCriacao());
        }
    };

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long idFilme;

    @NonNull
    @ColumnInfo(index = true)
    private LocalDateTime diaHoraCriacao;

    @NonNull
    private String texto;

    public Anotacao(long idFilme, @NonNull LocalDateTime diaHoraCriacao, @NonNull String texto) {
        this.idFilme = idFilme;
        this.diaHoraCriacao = diaHoraCriacao;
        this.texto = texto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdFilme() {
        return idFilme;
    }

    public void setIdFilme(long idFilme) {
        this.idFilme = idFilme;
    }

    @NonNull
    public LocalDateTime getDiaHoraCriacao() {
        return diaHoraCriacao;
    }

    public void setDiaHoraCriacao(@NonNull LocalDateTime diaHoraCriacao) {
        this.diaHoraCriacao = diaHoraCriacao;
    }

    @NonNull
    public String getTexto() {
        return texto;
    }

    public void setTexto(@NonNull String texto) {
        this.texto = texto;
    }
}
