package br.edu.utfpr.daniellarodrigues.maratonar.persistencia;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrar_1_2 extends Migration {

    public Migrar_1_2(){
        super(1,2);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database){
        //CRIANDO A TABELA
        database.execSQL("CREATE TABLE IF NOT EXISTS `Filme_provisorio` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`titulo` TEXT NOT NULL, " +
                "`categoria` TEXT, " +
                "`prioridade` INTEGER NOT NULL, " +
                "`classificacao` INTEGER NOT NULL, " +
                "`status` INTEGER)");


        // copia e converte os dados da tabela antiga para a tabela nova
        database.execSQL("INSERT INTO Filme_provisorio (id, titulo, categoria, prioridade, classificacao, status) " +
                "SELECT id, titulo, categoria, prioridade, classificacao, " +
                "CASE " +
                "WHEN status = 'Assistido'  THEN 0 " +
                "WHEN status = 'Nao_Assistido' THEN 1 " +
                "ELSE -1 " +
                "END " +
                "FROM Filme");

        // remove a tabela antiga
        database.execSQL("DROP TABLE Filme");

        // renomeia a nova tabela para o nome da tabela antiga
        database.execSQL("ALTER TABLE Filme_provisorio RENAME TO Filme");

        // cria o índice para o campo titulo, como existia na tabela antiga
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Filme_titulo` ON `Filme` (`titulo`)");
    }

}
