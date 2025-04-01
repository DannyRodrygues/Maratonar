package br.edu.utfpr.daniellarodrigues.maratonar.persistencia;

import androidx.room.TypeConverter;

import br.edu.utfpr.daniellarodrigues.maratonar.modelo.Status;

public class ConverterStatus {

    public static Status[] statuss = Status.values();

    @TypeConverter
    public static int fromEnumToInt(Status status){

        if (status == null){
            return -1;
        }
        return status.ordinal();
    }

    @TypeConverter
    public static Status fromIntEnum(int ordinal){

        if (ordinal < 0){
            return null;
        }
        return statuss[ordinal];
    }
}
