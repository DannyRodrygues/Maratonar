package br.edu.utfpr.daniellarodrigues.maratonar.persistencia;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.edu.utfpr.daniellarodrigues.maratonar.modelo.Anotacao;
import br.edu.utfpr.daniellarodrigues.maratonar.modelo.Filme;

@Database(entities = {Filme.class, Anotacao.class}, version = 4)
@TypeConverters({ConverterStatus.class, ConverterLocalDate.class, ConverterLocalDateTime.class})
public abstract class FilmesDatabase extends RoomDatabase {

    public abstract FilmeDao getFilmeDao();
    public abstract AnotacaoDao getAnotacaoDao();


    private static FilmesDatabase INSTANCE;

    public static FilmesDatabase getInstance(final Context context){
        if(INSTANCE == null){
            synchronized (FilmesDatabase.class){
               if(INSTANCE == null){
                    /*INSTANCE = Room.databaseBuilder(context,
                                                   FilmesDatabase.class,
                                                   "filmes.db").allowMainThreadQueries().build();*/



                        Builder builder = Room.databaseBuilder(context, FilmesDatabase.class, "filmes.db");

                        builder.allowMainThreadQueries();
                        //builder.fallbackToDestructiveMigration();
                        builder.addMigrations(new Migrar_1_2());
                        builder.addMigrations(new Migrar_2_3());
                        builder.addMigrations(new Migrar_3_4());
                        //builder.build();
                        INSTANCE = (FilmesDatabase) builder.build();
                }
            }
        }
        return INSTANCE;
    }
}
