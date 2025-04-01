package br.edu.utfpr.daniellarodrigues.maratonar.persistencia;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrar_3_4 extends Migration {
    public Migrar_3_4() {
        super(3, 4);
    }
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {

        // Cria a tabale Anotacao
        database.execSQL("CREATE TABLE IF NOT EXISTS `Anotacao` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`idFilme` INTEGER NOT NULL, " +
                "`diaHoraCriacao` INTEGER NOT NULL, " +
                "`texto` TEXT NOT NULL, " +
                "FOREIGN KEY(`idFilme`) REFERENCES `Filme`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");

        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Anotacao_diaHoraCriacao` ON `Anotacao` (`diaHoraCriacao`)");
    }
}