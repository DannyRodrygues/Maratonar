package br.edu.utfpr.daniellarodrigues.maratonar.utils;

import java.time.LocalDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

// classe para converter LocaDateTime para String
public final class UtilsLocalDateTime {

    public  static DateTimeFormatter formatter;
    private static Locale localeUsado;

    private UtilsLocalDateTime(){
        // evita que a classe seja instanciada
    }

    public static void inicializaFormatter(){

        localeUsado = Locale.getDefault();

        String formatPattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT,
                              null,
                               IsoChronology.INSTANCE,
                               localeUsado);

        formatPattern = formatPattern.replaceAll("\\byy\\b", "yyyy");
        formatPattern = formatPattern.replaceAll("\\bM\\b", "MM");
        formatPattern = formatPattern.replaceAll("\\bd\\b", "dd");
        formatPattern += " HH:mm:ss";

        formatter = DateTimeFormatter.ofPattern(formatPattern, Locale.getDefault());
    }

    public static String formatLocalDateTime(LocalDateTime dateTime){

        if (dateTime == null){
            return null;
        }

        if (localeUsado != Locale.getDefault()){
            inicializaFormatter();
        }

        return dateTime.format(formatter);
    }
}