package br.edu.utfpr.daniellarodrigues.maratonar.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.edu.utfpr.daniellarodrigues.maratonar.modelo.Filme;

@Dao
public interface FilmeDao {
    @Insert
    long insert(Filme filme);

    @Delete
    int delete(Filme filme);

    @Update
    int update(Filme filme);

    @Query("SELECT*FROM filme WHERE id=:id")
    Filme queryForId(long id);

    @Query("SELECT*FROM filme ORDER BY titulo ASC")
    List<Filme> queryAllAscending();

    @Query("SELECT*FROM filme ORDER BY titulo DESC")
    List<Filme> queryAllDownward();
}
