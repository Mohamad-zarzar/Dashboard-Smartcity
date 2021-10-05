package com.app.model.service;

import com.app.base.OtherConstants;
import io.swagger.client.ApiException;
import io.swagger.client.api.SensorManagementApi;
import io.swagger.client.api.TemperatureAggregationsApi;
import io.swagger.client.api.TemperatureMeasuresApi;
import io.swagger.client.model.*;
import javafx.scene.control.Alert;
import org.threeten.bp.*;
import org.threeten.bp.temporal.ChronoUnit;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.parser.Entity;
import java.util.*;

/**
 * TemperatureService
 * greift über API-Schnittstelle die Temperaturdaten ab
 * bietet vorwerarbeitete Methoden an
 */
public class TemperatureService {

    /**
     * getTempAktuell
     * @return aktuellen durchschnittlichen Temperaturwert
     */
    public Double getTempAktuell()
    {
        return getLatestAverage();
    }

    /**
     * getHoechsteAktuell
     * @return aktuellen hoechsten Tmperaturwert
     */
    public Double getHoesteAktuell()
    {
        return getHoechsteAktuell();
    }

    /**
     * getHoechsteTemp30
     * @return hoechsten Temperaturwert der letzten 30 Tage
     */
    public Temperature getHoechsteTemp30()
    {
        OffsetDateTime offsetDateTimeEnd = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
        OffsetDateTime offsetDateTimeStart = offsetDateTimeEnd.minusDays(30);
        return getHoechsteTempZeitraum(offsetDateTimeStart, offsetDateTimeEnd);
    }

    /**
     * getTemp7Tage
     * @return Map mit durchschnitt Temperaturwerten der letzten sieben Tage
     */
    public Map<OffsetDateTime, Double> getTemp7Tage() {
        return getTempZeitraumAverageMitAnzahlTagen(7);
    }

    /**
     * getLatestAverage
     * @return aktuellsten durchschnitts Temperaturwert gerundet
     */
    private Double getLatestAverage()
    {
        Double temp = 0.0;

        PagedModelTemperatureEmbedded pagedModelTemperatureEmbedded = getLatest();

        List<Temperature> temperatures = pagedModelTemperatureEmbedded.getTemperatures();

        temp = getAverageOfTemps(temperatures);

       return round1DecimalPlace(temp);

    }

    /**
     * getHoechsteAktuell
     * @return aktuell hoechsten durchschnitts Temperaturwert gerundet
     */
    private Double getHoechsteAktuell()
    {
        Double temp;

        PagedModelTemperatureEmbedded pagedModelTemperatureEmbedded = getLatest();

        List<Temperature> temperatures = pagedModelTemperatureEmbedded.getTemperatures();

        temp = getHoechsteOftemps(temperatures);

        return round1DecimalPlace(temp);

    }

    /**
     * getLatest
     * @return neusten Temperaturwerte aller Sensoren
     */
    private PagedModelTemperatureEmbedded getLatest()
    {
        PagedModelTemperatureEmbedded pagedModelTemperatureEmbedded = new PagedModelTemperatureEmbedded();

        try {

            TemperatureMeasuresApi temperatureMeasuresApi = new TemperatureMeasuresApi();
            PagedModelSensor pagedModelSensor = getTempSensoren();
            for (Sensor s: pagedModelSensor.getEmbedded().getSensors()) {
                EntityModelTemperature t = temperatureMeasuresApi.bySensorLatest2(s.getId());
                Temperature temperature = new Temperature();
                temperature.setId(t.getId());
                temperature.setValue(t.getValue());
                temperature.setTime(t.getTime());
                pagedModelTemperatureEmbedded.addTemperaturesItem(temperature);
            }


        }
        catch (ApiException apiException)
        {
            System.out.println(apiException.toString());
        }

        return pagedModelTemperatureEmbedded;
    }

    /**
     * getHoechsteTempZeitraum
     * hoechste Temperatur über einen Zeitraum
     * @param start Zeitraum beginn
     * @param end Zeitraum ende
     * @return hoechste Temperatur in diesem Zeitraum
     */
    public Temperature getHoechsteTempZeitraum(OffsetDateTime start, OffsetDateTime end)
    {
        Temperature temperatureHoechste =  new Temperature();
        temperatureHoechste.setValue(-900.0);

        Map<OffsetDateTime, Double> temperatures = getTempZeitraum(start, end);
        Iterator<OffsetDateTime> it = temperatures.keySet().iterator();

        while (it.hasNext())
        {
            OffsetDateTime key = it.next();

            if(temperatures.get(key) > temperatureHoechste.getValue()){
                temperatureHoechste.setValue(round1DecimalPlace(temperatures.get(key)));
            }
        }

        return temperatureHoechste;
    }

    /**
     * getTempZeitraumHoechsteMitAnzahlTagen
     * @param anzahlTage ab jetzigem Tag rückwärts gerechnet
     * @return alle Temperaturwerte für jeden Tag
     * z.B. letzten Sieben Tage, lezten 30 Tage
     */
    public Temperature geHoechstetTempZeitraumMitAnzahlTagen(int anzahlTage)
    {
        OffsetDateTime offsetDateTimeEnd = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
        OffsetDateTime offsetDateTimeStart = offsetDateTimeEnd.minusDays(anzahlTage);

        return getHoechsteTempZeitraum(offsetDateTimeStart, offsetDateTimeEnd);
    }

    /**
     * getTempZeitraumAverageMitAnzahlTagen
     * @param anzahlTage ab jetzigem Tag rückwärts gerechnet
     * @return alle Temperaturwerte für jeden Tag
     * z.B. letzten Sieben Tage, lezten 30 Tage
     */
    private Map<OffsetDateTime, Double> getTempZeitraumMitAnzahlTagen(int anzahlTage)
    {
        OffsetDateTime offsetDateTimeEnd = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
        OffsetDateTime offsetDateTimeStart = offsetDateTimeEnd.minusDays(anzahlTage);

        return getTempZeitraum(offsetDateTimeStart, offsetDateTimeEnd);
    }

    /**
     * getTempZeitraumAverageMitAnzahlTagen
     * @param anzahlTage ab jetzigem Tag rückwärts gerechnet
     * @return Durchschnittstemperatur für jeden Tag
     * z.B. letzten Sieben Tage, lezten 30 Tage
     */
    public Map<OffsetDateTime, Double> getTempZeitraumAverageMitAnzahlTagen(int anzahlTage) throws NullPointerException
    {
        OffsetDateTime offsetDateTimeEnd = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
        OffsetDateTime offsetDateTimeStart = offsetDateTimeEnd.minusDays(anzahlTage);

        return getTempZeitraumAvereageDay(offsetDateTimeStart, offsetDateTimeEnd);
    }

    /**
     * getTempZeitraumAverageMitAnzahlTagen
     * @param anzahlTage ab jetzigem Tag rückwärts gerechnet
     * @return Durchschnittstemperatur des gesamten Zeitraums
     * z.B. letzten Sieben Tage, lezten 30 Tage
     */
    public Double getTempAverageMitAnzahlTagen(int anzahlTage)
    {
        OffsetDateTime end = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
        OffsetDateTime start = end.minusDays(anzahlTage);
        List<Temperature> temperatureList = new LinkedList<>();

        Double average = getAirAverageMitZeitraum(start, end);

        return average;
    }

    /**
     * getTempZeitraumAverageMitZeitraum
     * @param start start Datum
     * @param end end Datum
     * @return Durchschnittstemperatur des gesamten Zeitraums
     * z.B. letzten Sieben Tage, lezten 30 Tage
     */
    public Double getAirAverageMitZeitraum(OffsetDateTime start, OffsetDateTime end)
    {
        List<Temperature> temperatureList = new LinkedList<>();

        Map<OffsetDateTime, Double>  tempAvgMap = getTempZeitraumAvereageDay(start, end);
        Iterator<OffsetDateTime> it  = tempAvgMap.keySet().iterator();
        while (it.hasNext())
        {
            OffsetDateTime key = it.next();
            Temperature t =  new Temperature();
            t.setValue(tempAvgMap.get(key));
            temperatureList.add(t);
        }

        Double average = getAverageOfTemps(temperatureList);
        return round1DecimalPlace(average);
    }

    /**
     * getTempZeitraumAvereageDay
     * @param start Zeitraum
     * @param end Zeitraum
     * @return Durchschnittstemperatur für jeden Tag in gegebenen Zeitraum
     * @throws NullPointerException wenn keine Werte in dem Zeitraum vorhanden sind
     */
    public Map<OffsetDateTime, Double> getTempZeitraumAvereageDay(OffsetDateTime start, OffsetDateTime end) throws NullPointerException
    {
        Map<OffsetDateTime, Double> temps = new LinkedHashMap<>();

        long anzahlTage = ChronoUnit.DAYS.between(start, end);

        OffsetDateTime currentDay = start;

        try {
            TemperatureMeasuresApi temperatureMeasuresApi = new TemperatureMeasuresApi();
            PagedModelTemperature pagedModelTemperature = temperatureMeasuresApi.between1(start, end, null, Integer.MAX_VALUE, null);
            PagedModelTemperatureEmbedded pagedModelTemperatureEmbedded = pagedModelTemperature.getEmbedded();

            for (int i = 0; i < anzahlTage; i++) {
                currentDay = currentDay.plusDays(1);
                List<Temperature> tempZeitraum = new ArrayList<>();

                for (Temperature temperature: pagedModelTemperatureEmbedded.getTemperatures())
                {
                    if(temperature.getTime().getMonth() == currentDay.getMonth() && temperature.getTime().getDayOfMonth() == currentDay.getDayOfMonth())
                    {
                        tempZeitraum.add(temperature);
                    }
                }
                if(!tempZeitraum.isEmpty()){
                    if(currentDay.getMonth() == tempZeitraum.get(0).getTime().getMonth() && currentDay.getDayOfMonth() == tempZeitraum.get(0).getTime().getDayOfMonth()) {
                        temps.put(currentDay, round1DecimalPlace(getAverageOfTemps(tempZeitraum)));
                    }
                    else {
                        temps.put(currentDay, 0.0);
                    }
                }
                else {
                    temps.put(currentDay, 0.0);
                }
            }
        }
        catch (ApiException apiException)
        {
            System.out.println(apiException);
        }

        return temps;
    }

    /**
     * getTempDailyAverage
     */
    public void getTempDailyAverage(OffsetDateTime day)
    {
        TemperatureAggregationsApi aggregationsApi = new TemperatureAggregationsApi();
        PagedModelSensor pagedModelSensor = getTempSensoren();

        for (Sensor s: pagedModelSensor.getEmbedded().getSensors()) {
            try {
                EntityModelTemperatureAverageDaily a = aggregationsApi.getDailyAverage1(s.getId(), day);
                a.getValue();

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * getTempWeeklyAverage
     * @return Durchschnittswert der angegebenen Woche
     */
    public Double getTempWeeklyAverage(OffsetDateTime week) {
        TemperatureAggregationsApi aggregationsApi = new TemperatureAggregationsApi();
        PagedModelSensor pagedModelSensor = getTempSensoren();
        List<Temperature> weeklyAvergaesBySensor = new LinkedList<>();

        for (Sensor s : pagedModelSensor.getEmbedded().getSensors()) {
            try {
                EntityModelTemperatureAverageWeekly a = aggregationsApi.getWeeklyAverage1(s.getId(), week);
                Temperature t = new Temperature();
                t.setValue(a.getValue());
                weeklyAvergaesBySensor.add(t);

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }

        return getAverageOfTemps(weeklyAvergaesBySensor);
    }

    /**
     * getTempHourly
     * @return Map mit stündlcihen Temperaturwerten vom selbigen Tag
     */
    public Map<OffsetDateTime, Double> getTempHourly() throws NullPointerException
    {
        OffsetDateTime now = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
        OffsetDateTime start = now.minusMinutes(now.getMinute()).minusHours(now.getHour()).minusSeconds(now.getSecond()).minusNanos(now.getNano());
        OffsetDateTime end = start.plusHours(23);

        Map<OffsetDateTime, Double> temperatures = new LinkedHashMap<>();
        Map<OffsetDateTime, Double> timeDoubleMap = getTempZeitraum(start, end);

        for (int i = 0; i < 24; i++) {
            Iterator<OffsetDateTime> it = timeDoubleMap.keySet().iterator();
            while (it.hasNext())
            {
                OffsetDateTime key = it.next();
                if(key.isBefore(start.plusMinutes(1)) && key.isAfter(start.minusMinutes(1)))
                {
                    temperatures.put(start, timeDoubleMap.get(key));
                }
            }
            start = start.plusHours(1);
        }

        return temperatures;
    }

    /**
     * getTempZeitraum
     * @param start Zeitraum
     * @param ende Zeitraum
     * @return alle Temperaturen in gegebenen Zietraum
     */
    public Map<OffsetDateTime, Double> getTempZeitraum(OffsetDateTime start, OffsetDateTime ende) throws NullPointerException
    {
        Map<OffsetDateTime, Double> tempZeitraum = new LinkedHashMap<>();

        try {

            TemperatureMeasuresApi temperatureMeasuresApi = new TemperatureMeasuresApi();
            PagedModelTemperature pagedModelTemperature = temperatureMeasuresApi.between1(start, ende,null, Integer.MAX_VALUE, null);
            PagedModelTemperatureEmbedded pagedModelTemperatureEmbedded = pagedModelTemperature.getEmbedded();

            for (Temperature t: pagedModelTemperatureEmbedded.getTemperatures()) {

                tempZeitraum.put(t.getTime(), t.getValue());
            }

        }
        catch (ApiException apiException)
        {
            System.out.println(apiException);
        }

        return tempZeitraum;
    }

    /**
     * getPagedTemperaturePerSensor
     * @return Map mit allen Sensordaten als Page gruppiert bei Sensor
     */
    public Map<Sensor, PagedModelTemperature> getPagedTemperaturePerSensor()
    {
        Map<Sensor, PagedModelTemperature> temperatureMap = new LinkedHashMap<>();

        TemperatureMeasuresApi temperatureMeasuresApi = new TemperatureMeasuresApi();
        PagedModelSensor sensoren = getTempSensoren();

        try {
            for (Sensor s: sensoren.getEmbedded().getSensors()) {
                PagedModelTemperature temperature = temperatureMeasuresApi.bySensor2(s.getId());
                temperatureMap.put(s, temperature);
            }
        }
        catch (ApiException apiException)
        {
            System.out.println(apiException);
        }

        return temperatureMap;
    }

    /**
     * round1DecimalPlace
     * @param temp Temperatur
     * @return gerundet auf eine Nachkommastelle
     * rundet Temperaturwert auf eine Nachkommastelle
     */
    private double round1DecimalPlace(double temp)
    {
        long factor = (long) Math.pow(10, 1);
        temp = temp * factor;
        long tmp = Math.round(temp);
        return (double) tmp / factor;
    }

    /**
     * getHoechsteOftemps
     * @param werte Liste von Temperaturen
     * @return Durchschnitt Temperatur aus Liste
     * bestimmt die Durchschnitts Temperatur aus gegebener Liste udn gibt diese zurück
     */
    private double getAverageOfTemps(List<Temperature> werte)
    {
        double ergebnis = 0.0;

        for (Temperature temperature: werte) {
            ergebnis = ergebnis + temperature.getValue();
        }

        return ergebnis / werte.size();
    }

    /**
     * getHoechsteOftemps
     * @param werte Liste von Temperaturen
     * @return hoechste Temperatur aus Liste
     * bestimmt die hoechste Temperatur aus gegebener Liste udn gibt diese zurück
     */
    private double getHoechsteOftemps(List<Temperature> werte)
    {
        double ergebnis = -900.0;

        for (Temperature temperature: werte) {
            if(temperature.getValue() > ergebnis)
            {
                ergebnis = temperature.getValue();
            }
        }

        return ergebnis;
    }

    /**
     * getTempSenoren
     * @return PagedModelSensoren mit allen Temperatur Sensoren
     */
    private PagedModelSensor getTempSensoren()
    {
        SensorManagementApi sensorManagementApi = new SensorManagementApi();
        PagedModelSensor pagedModelSensor = null;
        try {
            pagedModelSensor = sensorManagementApi.byType(OtherConstants.SENSOR_TYPE_TEMPERATURE, null, Integer.MAX_VALUE, null);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return pagedModelSensor;
    }
}

