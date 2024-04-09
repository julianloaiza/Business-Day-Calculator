package com.bolivar.healthinsurance.treasury.utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumSet;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * Utilidad para calcular fechas de pagos y días festivos.
 * @author <a href="mailto:julian.loaiza@segurosbolivar.com">Julian Loaiza</a>
 * @project aseguramiento-salud-tesoreria-ms
 * @class BusinessDayCalculator
 * @date 24/01/2024
 */

@Component
@Slf4j
public class BusinessDayCalculator {

    private final CatalogsUtil catalogsUtil;

    private BusinessDayCalculator(CatalogsUtil catalogsUtil) {
        this.catalogsUtil = catalogsUtil;
    }

    /**
     * Calcula los días festivos para el año dado.
     * @param year El año para el cual calcular los festivos.
     * @return Conjunto de días festivos.
     */
    public Set<LocalDate> calculateHolidays(int year) {
        Set<LocalDate> calculatedHolidays = new HashSet<>();

        // Días festivos fijos
        calculatedHolidays.add(LocalDate.of(year, 1, 1));  // Año Nuevo
        calculatedHolidays.add(LocalDate.of(year, 5, 1));  // Día del Trabajo
        calculatedHolidays.add(LocalDate.of(year, 7, 20)); // Día de la Independencia
        calculatedHolidays.add(LocalDate.of(year, 8, 7));  // Batalla de Boyacá
        calculatedHolidays.add(LocalDate.of(year, 12, 8)); // Inmaculada Concepción
        calculatedHolidays.add(LocalDate.of(year, 12, 25));// Navidad

        // Añadir festivos que se mueven al lunes más cercano (Ley de Emiliani)
        calculatedHolidays.addAll(calculateEmiliani(LocalDate.of(year, 1, 6)));   // Reyes Magos
        calculatedHolidays.addAll(calculateEmiliani(LocalDate.of(year, 3, 19)));  // San José
        calculatedHolidays.addAll(calculateEmiliani(LocalDate.of(year, 1, 6)));   // Reyes magos 6 de enero
        calculatedHolidays.addAll(calculateEmiliani(LocalDate.of(year, 3, 19)));  //San jose 19 de marzo
        calculatedHolidays.addAll(calculateEmiliani(LocalDate.of(year, 6, 29)));  //San pedro y san pablo 29 de junio
        calculatedHolidays.addAll(calculateEmiliani(LocalDate.of(year, 8, 15)));  //Asuncion 15 de agosto
        calculatedHolidays.addAll(calculateEmiliani(LocalDate.of(year, 10, 12))); //Descubrimiento de america 12 de octubre
        calculatedHolidays.addAll(calculateEmiliani(LocalDate.of(year, 11, 1)));  //Todos los santos 1 de noviembre
        calculatedHolidays.addAll(calculateEmiliani(LocalDate.of(year, 11, 11))); //Independencia de cartagena 11 de noviembre

        // Calculamos la fecha de Pascua y los días festivos relacionados
        LocalDate easterDate = calculateEasterDate(year);
        calculatedHolidays.addAll(calculateEasterRelatedHolidays(easterDate));

        return calculatedHolidays;
    }

    /**
     * Calcula la fecha de Pascua para el año dado usando el Algoritmo de Meeus/Jones/Butcher.
     *
     * @param year El año para el cual calcular la fecha de Pascua.
     * @return La fecha de Pascua para el año dado.
     */
    private LocalDate calculateEasterDate(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(year, month, day);
    }

    /**
     * Calcula los festivos relativo a la fecha de Pascua.
     *
     * @param easterDate La fecha de Pascua.
     * @return Conjunto de los días relativos a la fecha de Pascua.
     */
    private Set<LocalDate> calculateEasterRelatedHolidays(LocalDate easterDate) {
        Set<LocalDate> easterHolidays = new HashSet<>();

        // Jueves Santo (el jueves antes de Pascua)
        easterHolidays.add(easterDate.minusDays(3));

        // Viernes Santo (el viernes antes de Pascua)
        easterHolidays.add(easterDate.minusDays(2));

        // Ascensión del Señor (40 días después de Pascua, trasladado al siguiente lunes)
        easterHolidays.addAll(calculateEmiliani(easterDate.plusDays(40)));

        // Corpus Christi (60 días después de Pascua, trasladado al siguiente lunes)
        easterHolidays.addAll(calculateEmiliani(easterDate.plusDays(60)));

        // Sagrado Corazón (68 días después de Pascua, trasladado al siguiente lunes)
        easterHolidays.addAll(calculateEmiliani(easterDate.plusDays(68)));


        return easterHolidays;
    }


    /**
     * Ajusta un día festivo según la ley de Emiliani (Correr al siguiente Lunes).
     *
     * @param holiday El día que se quiere ajustar.
     * @return Día corregido al lunes más próximo.
     */
    private Set<LocalDate> calculateEmiliani(LocalDate holiday) {
        Set<LocalDate> adjustedHoliday = new HashSet<>();
        // Si el festivo no es lunes, se mueve al siguiente lunes
        if (holiday.getDayOfWeek() != DayOfWeek.MONDAY) {
            holiday = holiday.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        }
        adjustedHoliday.add(holiday);
        return adjustedHoliday;
    }


    /**
     * Obtiene los fechas especiales que no se deben tener en cuenta.
     * @return Conjunto de fechas especiales.
     */
    public Set<LocalDate> getSpecialDates(int year) {
        Set<LocalDate> specialDates = new HashSet<>();

        // Obtener la lista de fechas especiales desde catalogsUtil
        List<Map<String, String>> fechasEspeciales = catalogsUtil.all("FECHAS_ESPECIALES");

        // Procesar y agregar las fechas especiales al conjunto
        for (Map<String, String> fechaEspecial : fechasEspeciales) {
            int mes = Integer.parseInt(fechaEspecial.get("Mes"));
            int dia = Integer.parseInt(fechaEspecial.get("Dia"));
            LocalDate fecha = LocalDate.of(year, mes, dia);
            specialDates.add(fecha);
        }

        return specialDates;
    }

    /**
     * Obtiene los días de la semana especiales que no se deben tener en cuenta.
     * @return Un conjunto inmutable de días de la semana.
     */
    public Set<DayOfWeek> getSpecialDaysOfWeek() {
        Set<DayOfWeek> specialDaysOfWeek = EnumSet.noneOf(DayOfWeek.class);

        // Obtener la lista de días especiales desde catalogsUtil
        List<Map<String, String>> diasEspecialesList = catalogsUtil.all("DIAS_ESPECIALES");

        // Procesar y agregar los días especiales al conjunto
        for (Map<String, String> diaEspecial : diasEspecialesList) {
            String dia = diaEspecial.get("Dia");
            try {
                // Convertir la cadena del día a un objeto DayOfWeek
                DayOfWeek dayOfWeek = DayOfWeek.valueOf(dia.toUpperCase());
                specialDaysOfWeek.add(dayOfWeek);
            } catch (IllegalArgumentException e) {
                System.err.println("Día inválido: " + dia);
            }
        }

        return specialDaysOfWeek;
    }

    /**
     * Obtiene la hora límite en la cual debe enviarse a pagar una factura.
     * @return La hora límite del pago de la factura.
     */
    public LocalTime getHourLimit() {
        // Obtener la lista de la hora límite desde catalogsUtil
        List<Map<String, String>> horaLimiteList = catalogsUtil.all("HORA_LIMITE");
        Map<String, String> horaLimite = horaLimiteList.get(0);

        // Convertir la hora límite a un objeto LocalTime

        return LocalTime.parse(horaLimite.get("Hora"));
    }


    /**
     * Obtiene el siguiente día hábil a partir de la fecha y hora dada.
     * @param dateTime La fecha y hora a partir de la cual buscar el siguiente día hábil.
     * @return La fecha y hora del siguiente día hábil.
     */
    public LocalDateTime getNextBusinessDay(LocalDateTime dateTime) {

        LocalDateTime newDateTime = LocalDateTime.of(
                dateTime.getYear(),
                dateTime.getMonth(),
                dateTime.getDayOfMonth(),
                dateTime.getHour(),
                dateTime.getMinute(),
                dateTime.getSecond(),
                dateTime.getNano()
        );

        Set<LocalDate> holidays = calculateHolidays(newDateTime.getYear());
        Set<LocalDate> specialDates = getSpecialDates(newDateTime.getYear());
        Set<DayOfWeek> specialDaysOfWeek = getSpecialDaysOfWeek();
        LocalTime hourLimit = getHourLimit();

        log.info("Holidays for the year {}: {}", newDateTime.getYear(), holidays);
        log.info("Special dates for the year {}: {}", newDateTime.getYear(), specialDates);
        log.info("Special days of the week: {}", specialDaysOfWeek);
        log.info("Hour limit for business day calculation: {}", hourLimit);

        // Si la hora supera el límite avanzamos al siguiente día.
        if (newDateTime.toLocalTime().isAfter(hourLimit) || newDateTime.toLocalTime().equals(hourLimit)) {
            newDateTime = newDateTime.plusDays(1);

            // Si al avanzar al siguiente día cambiamos de año, recalcular los días festivos
            if (newDateTime.getYear() != dateTime.getYear()) {
                holidays = calculateHolidays(newDateTime.getYear());
                specialDates = getSpecialDates(newDateTime.getYear());
            }
        }

        // Configuramos la hora de pago a las 00:00 del día encontrado.
        newDateTime = newDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

        // Verificamos si la fecha actualizada es un día hábil.
        while (holidays.contains(newDateTime.toLocalDate()) ||
                specialDates.contains(newDateTime.toLocalDate()) ||
                specialDaysOfWeek.contains(newDateTime.getDayOfWeek())) {
            // Si no es un día hábil, avanzamos al siguiente día.
            newDateTime = newDateTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);


            // Si al avanzar al siguiente día cambiamos de año, recalcular los días festivos
            if (newDateTime.getYear() != dateTime.getYear()) {
                holidays = calculateHolidays(newDateTime.getYear());
                specialDates = getSpecialDates(newDateTime.getYear());
            }
        }

        return newDateTime;
    }
}
