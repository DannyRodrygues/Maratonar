package br.edu.utfpr.daniellarodrigues.maratonar.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.edu.utfpr.daniellarodrigues.maratonar.modelo.Anotacao;

@Dao
public interface AnotacaoDao {
    @Insert
    long insert(Anotacao anotacao);

    @Delete
    int delete(Anotacao anotacao);

    @Update
    int update(Anotacao anotacao);



    @Query("SELECT * FROM Anotacao WHERE id=:id" )
            Anotacao queryForId(long id);

    @Query("SELECT * FROM Anotacao WHERE idFilme = :idFilme ORDER BY diaHoraCriacao DESC")
    List<Anotacao> queryForIdFilme(long idFilme);

    @Query("SELECT count(*) FROM Anotacao WHERE idFilme = :idFilme ")
    int totalIdFilme(long idFilme);

}
