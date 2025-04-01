package br.edu.utfpr.daniellarodrigues.maratonar.utils;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

public final class UtilsLocalDate {

    public static DateTimeFormatter formatter;
    private static Locale localeUsado;
    private UtilsLocalDate(){

    }

    public static void inicializaFormater(){

        localeUsado = Locale.getDefault();
        String formatPattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT,
                               null,
                                IsoChronology.INSTANCE,
                                localeUsado);

        formatPattern = formatPattern.replaceAll("\\byy\\b", "yyyy");
        formatPattern = formatPattern.replaceAll("\\bM\\b", "MM");
        formatPattern = formatPattern.replaceAll("\\bd\\b", "dd");

         formatter = DateTimeFormatter.ofPattern(formatPattern, Locale.getDefault());
    }

    // converte Localdate para String
    public static String formatLocalDate(LocalDate date){

        if (date == null){
            return  null;
        }

        if (localeUsado != Locale.getDefault()){
            inicializaFormater();
        }

        return date.format(formatter);
    }

    public static long toMillissegundos(LocalDate date){

        if (date == null){
            return 0;
        }

        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static int diferencaEmAnosParaHoje(LocalDate date){
        return diferencaEmAnos(date, LocalDate.now());
    }

    public static int diferencaEmAnos(LocalDate date1, LocalDate date2){

        if (date1 == null || date2 == null){
            return 0;
        }

        Period periodo = Period.between(date1, date2);

        return periodo.getYears();
    }
}