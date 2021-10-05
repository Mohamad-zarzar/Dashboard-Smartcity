package com.app.model.service;

import com.app.base.OtherConstants;
import io.swagger.client.ApiException;
import io.swagger.client.api.SensorManagementApi;
import io.swagger.client.api.AirQualitiesAggregationsApi;
import io.swagger.client.api.AirQualityMeasuresApi;
import io.swagger.client.model.*;
import org.threeten.bp.*;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.*;

/**
 * LuftqualitaetService
 * greift über API-Schnittstelle die Luftqualitaetdaten ab
 * bietet vorwerarbeitete Methoden an
 */
public class LuftqualitaetService {

    /**
     * getAirAktuell
     * @return aktuellen durchschnittlichen Luftqualitaetwert
     */
    public Double getAirAktuell()
    {
        return getLatestAverage();
    }

    /**
     * getHoechsteAktuell
     * @return aktuellen hoechsten Tmperaturwert
     */
    public Integer getHoesteAktuell()
    {
        return getHoechsteAktuell();
    }

    /**
     * getHoechsteAir30
     * @return hoechsten Luftqualitaetwert der letzten 30 Tage
     */
    public AirQuality getHoechsteAir30()
    {
        OffsetDateTime offsetDateTimeEnd = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
        OffsetDateTime offsetDateTimeStart = offsetDateTimeEnd.minusDays(30);
        return getHoechsteAirZeitraum(offsetDateTimeStart, offsetDateTimeEnd);
    }

    /**
     * getAir7Tage
     * @return Map mit durchschnitt Luftqualitaetwerten der letzten sieben Tage
     */
    public Map<OffsetDateTime, Double> getAir7Tage() {
        return getAirZeitraumAverageMitAnzahlTagen(7);
    }

    /**
     * getLatestAverage
     * @return aktuellsten durchschnitts Luftqualitaetwert gerundet
     */
    private Double getLatestAverage()
    {
        Double air;

        PagedModelAirQualityEmbedded pagedModelAirQualityEmbedded = getLatest();

        List<AirQuality> airQualities = pagedModelAirQualityEmbedded.getAirQualities();

        air = getAverageOfAirs(airQualities);

        return air;

    }

    /**
     * getHoechsteAktuell
     * @return aktuell hoechsten durchschnitts Luftqualitaetwert gerundet
     */
    private Integer getHoechsteAktuell()
    {
        Integer air = 0;

        PagedModelAirQualityEmbedded pagedModelAirQualityEmbedded = getLatest();

        List<AirQuality> airQualities = pagedModelAirQualityEmbedded.getAirQualities();

        air = getHoechsteOfAir(airQualities);

        return air;

    }

    /**
     * getLatest
     * @return neusten Luftqualitaetwerte aller Sensoren
     */
    private PagedModelAirQualityEmbedded getLatest()
    {
        PagedModelAirQualityEmbedded pagedModelAirQualityEmbedded = new PagedModelAirQualityEmbedded();

        try {

            AirQualityMeasuresApi airQualityMeasuresApi = new AirQualityMeasuresApi();
            PagedModelSensor pagedModelSensor = getAirSensoren();
            for (Sensor s: pagedModelSensor.getEmbedded().getSensors()) {
                EntityModelAirQuality t = airQualityMeasuresApi.bySensorLatest(s.getId());
                AirQuality airQuality =  new AirQuality();
                airQuality.setId(t.getId());
                airQuality.setValue(t.getValue());
                airQuality.setTime(t.getTime());
                pagedModelAirQualityEmbedded.addAirQualitiesItem(airQuality);
            }


        }
        catch (ApiException apiException)
        {
            System.out.println(apiException.toString());
        }

        //To-Do
        AirQuality temporary = new AirQuality();
        temporary.setId(34l);
        temporary.setValue(200);
        temporary.setTime(OffsetDateTime.now());
        pagedModelAirQualityEmbedded.addAirQualitiesItem(temporary);

        return pagedModelAirQualityEmbedded;
    }

    /**
     * getHoechsteAirZeitraum
     * hoechste Luftqualitaet über einen Zeitraum
     * @param start Zeitraum beginn
     * @param end Zeitraum ende
     * @return hoechste Luftqualitaet in diesem Zeitraum
     */
    public AirQuality getHoechsteAirZeitraum(OffsetDateTime start, OffsetDateTime end)
    {
        AirQuality airQualityHoechste =  new AirQuality();
        airQualityHoechste.setValue(-900);

        Map<OffsetDateTime, Integer> airQualities = getAirZeitraum(start, end);
        Iterator<OffsetDateTime> it = airQualities.keySet().iterator();

        while (it.hasNext())
        {
            OffsetDateTime key = it.next();

            if(airQualities.get(key) > airQualityHoechste.getValue()){
                airQualityHoechste.setValue(airQualities.get(key));
            }
        }

        return airQualityHoechste;
    }

    /**
     * getAirZeitraumHoechsteMitAnzahlTagen
     * @param anzahlTage ab jetzigem Tag rückwärts gerechnet
     * @return alle Luftqualitaetwerte für jeden Tag
     * z.B. letzten Sieben Tage, lezten 30 Tage
     */
    public AirQuality geHoechstetAirZeitraumMitAnzahlTagen(int anzahlTage)
    {
        OffsetDateTime offsetDateTimeEnd = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
        OffsetDateTime offsetDateTimeStart = offsetDateTimeEnd.minusDays(anzahlTage);

        return getHoechsteAirZeitraum(offsetDateTimeStart, offsetDateTimeEnd);
    }

    /**
     * getAirZeitraumAverageMitAnzahlTagen
     * @param anzahlTage ab jetzigem Tag rückwärts gerechnet
     * @return alle Luftqualitaetwerte für jeden Tag
     * z.B. letzten Sieben Tage, lezten 30 Tage
     */
    private Map<OffsetDateTime, Integer> getAirZeitraumMitAnzahlTagen(int anzahlTage)
    {
        OffsetDateTime offsetDateTimeEnd = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
        OffsetDateTime offsetDateTimeStart = offsetDateTimeEnd.minusDays(anzahlTage);

        return getAirZeitraum(offsetDateTimeStart, offsetDateTimeEnd);
    }

    /**
     * getAirZeitraumAverageMitAnzahlTagen
     * @param anzahlTage ab jetzigem Tag rückwärts gerechnet
     * @return Durchschnitts Luftqualität für jeden Tag
     * z.B. letzten Sieben Tage, lezten 30 Tage
     */
    public Map<OffsetDateTime, Double> getAirZeitraumAverageMitAnzahlTagen(int anzahlTage) throws NullPointerException
    {
        OffsetDateTime offsetDateTimeEnd = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
        OffsetDateTime offsetDateTimeStart = offsetDateTimeEnd.minusDays(anzahlTage);

        return getAirZeitraumAvereageDay(offsetDateTimeStart, offsetDateTimeEnd);
    }

    /**
     * getTempZeitraumAverageMitAnzahlTagen
     * @param anzahlTage ab jetzigem Tag rückwärts gerechnet
     * @return Durchschnitts Luftqualität des gesamten Zeitraums
     * z.B. letzten Sieben Tage, lezten 30 Tage
     */
    public Double getAirAverageMitAnzahlTagen(int anzahlTage)
    {
        OffsetDateTime offsetDateTimeEnd = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
        OffsetDateTime offsetDateTimeStart = offsetDateTimeEnd.minusDays(anzahlTage);

        Double average = getAirAverageMitZeitraum(offsetDateTimeStart, offsetDateTimeEnd);
        return average;
    }

    /**
     * getTempZeitraumAverageMitAnzahlTagen
     * @param start start Datum
     * @param end end Datum
     * @return Durchschnitts Luftqualität des gesamten Zeitraums
     * z.B. letzten Sieben Tage, lezten 30 Tage
     */
    public Double getAirAverageMitZeitraum(OffsetDateTime start, OffsetDateTime end)
    {
        List<Double> temperatureList = new LinkedList<>();

        Map<OffsetDateTime, Double>  airQualityMap = getAirZeitraumAvereageDay(start, end);
        Iterator<OffsetDateTime> it  = airQualityMap.keySet().iterator();
        while (it.hasNext())
        {
            OffsetDateTime key = it.next();
            temperatureList.add(airQualityMap.get(key));
        }

        Double average = getAverageOfAirsDouble(temperatureList);
        return round2DecimalPlace(average);
    }

    /**
     * getAirZeitraumAvereageDay
     * @param start Zeitraum
     * @param end Zeitraum
     * @return Durchschnitts LuftQualität für jeden Tag in gegebenen Zeitraum
     * @throws NullPointerException wenn keine Werte in dem Zeitraum vorhanden sind
     */
    public Map<OffsetDateTime, Double> getAirZeitraumAvereageDay(OffsetDateTime start, OffsetDateTime end) throws NullPointerException
    {
        Map<OffsetDateTime, Double> airs = new LinkedHashMap<>();

        long anzahlTage = ChronoUnit.DAYS.between(start, end);

        OffsetDateTime currentDay = start;

        try {
            AirQualityMeasuresApi airQualityMeasuresApi = new AirQualityMeasuresApi();
            PagedModelAirQuality pagedModelAirQuality = airQualityMeasuresApi.between(start, end, null, Integer.MAX_VALUE, null);
            PagedModelAirQualityEmbedded pagedModelAirQualityEmbedded = pagedModelAirQuality.getEmbedded();

            for (int i = 0; i < anzahlTage; i++) {
                currentDay = currentDay.plusDays(1);
                List<AirQuality> airQualities = new ArrayList<>();

                for (AirQuality airQuality: pagedModelAirQualityEmbedded.getAirQualities())
                {
                    if(airQuality.getTime().getMonth() == currentDay.getMonth() && airQuality.getTime().getDayOfMonth() == currentDay.getDayOfMonth())
                    {
                        airQualities.add(airQuality);
                    }
                }
                if(!airQualities.isEmpty()){
                    if(currentDay.getMonth() == airQualities.get(0).getTime().getMonth() && currentDay.getDayOfMonth() == airQualities.get(0).getTime().getDayOfMonth()) {
                        airs.put(currentDay, getAverageOfAirs(airQualities));
                    }
                    else {
                        airs.put(currentDay, 0.0);
                    }
                }
                else {
                    airs.put(currentDay, 0.0);
                }
            }
        }
        catch (ApiException apiException)
        {
            System.out.println(apiException);
        }

        return airs;
    }

    /**
     * getAirDailyAverage
     */
    public void getAirDailyAverage(OffsetDateTime day)
    {
        AirQualitiesAggregationsApi aggregationsApi = new AirQualitiesAggregationsApi();
        PagedModelSensor pagedModelSensor = getAirSensoren();

        for (Sensor s: pagedModelSensor.getEmbedded().getSensors()) {
            try {
                EntityModelAirQualityAverageDaily a = aggregationsApi.getDailyAverage(s.getId(), day);

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * getAirWeeklyAverage
     * @return Durchschnittswert der angegebenen Woche
     */
    public Double getAirWeeklyAverage(OffsetDateTime week) {
        AirQualitiesAggregationsApi aggregationsApi = new AirQualitiesAggregationsApi();
        PagedModelSensor pagedModelSensor = getAirSensoren();
        List<Double> weeklyAvergaesBySensor = new LinkedList<>();

        for (Sensor s : pagedModelSensor.getEmbedded().getSensors()) {
            try {
                EntityModelAirQualityAverageWeekly a = aggregationsApi.getWeeklyAverage(s.getId(), week);
                weeklyAvergaesBySensor.add(a.getValue());

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }

        return getAverageOfAirsDouble(weeklyAvergaesBySensor);
    }

    /**
     * getAirHourly
     * @return Map mit stündlcihen Luftqualitaetwerten vom selbigen Tag
     */
    public Map<OffsetDateTime, Integer> getAirHourly() throws NullPointerException
    {
        OffsetDateTime now = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
        OffsetDateTime start = now.minusMinutes(now.getMinute()).minusHours(now.getHour()).minusSeconds(now.getSecond()).minusNanos(now.getNano());
        OffsetDateTime end = start.plusHours(23);

        Map<OffsetDateTime, Integer> airQualities = new LinkedHashMap<>();
        Map<OffsetDateTime, Integer> airIntMap = getAirZeitraum(start, end);

        for (int i = 0; i < 24; i++) {
            Iterator<OffsetDateTime> it = airIntMap.keySet().iterator();
            while (it.hasNext())
            {
                OffsetDateTime key = it.next();
                if(key.isBefore(start.plusMinutes(1)) && key.isAfter(start.minusMinutes(1)))
                {
                    airQualities.put(start, airIntMap.get(key));
                }
            }
            start = start.plusHours(1);
        }

        return airQualities;
    }

    /**
     * getAirZeitraum
     * @param start Zeitraum
     * @param ende Zeitraum
     * @return alle AirQualityn in gegebenen Zietraum
     */
    public Map<OffsetDateTime, Integer> getAirZeitraum(OffsetDateTime start, OffsetDateTime ende) throws NullPointerException
    {
        Map<OffsetDateTime, Integer> airZeitraum = new LinkedHashMap<>();

        try {

            AirQualityMeasuresApi airQualityMeasuresApi = new AirQualityMeasuresApi();
            PagedModelAirQuality pagedModelAirQuality = airQualityMeasuresApi.between(start, ende,null, Integer.MAX_VALUE, null);
            PagedModelAirQualityEmbedded pagedModelAirQualityEmbedded = pagedModelAirQuality.getEmbedded();

            for (AirQuality t: pagedModelAirQualityEmbedded.getAirQualities()) {

                airZeitraum.put(t.getTime(), t.getValue());
            }

        }
        catch (ApiException apiException)
        {
            System.out.println(apiException);
        }

        return airZeitraum;
    }

    /**
     * getPagedAirQualityPerSensor
     * @return Map mit allen Sensordaten als Page gruppiert bei Sensor
     */
    public Map<Sensor, PagedModelAirQuality> getPagedAirQualityPerSensor()
    {
        Map<Sensor, PagedModelAirQuality> airQualityMap = new LinkedHashMap<>();

        AirQualityMeasuresApi airQualityMeasuresApi = new AirQualityMeasuresApi();
        PagedModelSensor sensoren = getAirSensoren();

        try {
            for (Sensor s: sensoren.getEmbedded().getSensors()) {
                PagedModelAirQuality pagedModelAirQuality = airQualityMeasuresApi.bySensor(s.getId());
                airQualityMap.put(s, pagedModelAirQuality);
            }
        }
        catch (ApiException apiException)
        {
            System.out.println(apiException);
        }

        return airQualityMap;
    }

    /**
     * getAverageOfAirs
     * @param werte Liste von AirQualityn
     * @return Durchschnitt Luftqualitaet aus Liste
     * bestimmt die Durchschnitts Luftqualitaet aus gegebener Liste udn gibt diese zurück
     */
    private Double getAverageOfAirs(List<AirQuality> werte)
    {
        Double ergebnis = 0.0;

        for (AirQuality airQuality: werte) {
            ergebnis = ergebnis + airQuality.getValue();
        }

        if(werte.size() == 0)
        {
            return 0.0;
        }

        return ergebnis / werte.size();
    }

    /**
     * getAverageOfAirsDouble
     * @param werte Liste von AirQualityn
     * @return Durchschnitt Luftqualitaet aus Liste
     * bestimmt die Durchschnitts Luftqualitaet aus gegebener Liste udn gibt diese zurück
     */
    private Double getAverageOfAirsDouble(List<Double> werte)
    {
        double ergebnis = 0.0;

        for (Double d: werte) {
            ergebnis = ergebnis + d;
        }

        return ergebnis / werte.size();
    }

    /**
     * getHoechsteOfAir
     * @param werte Liste von AirQualityn
     * @return hoechste Luftqualitaet aus Liste
     * bestimmt die hoechste Luftqualitaet aus gegebener Liste udn gibt diese zurück
     */
    private Integer getHoechsteOfAir(List<AirQuality> werte)
    {
        Integer ergebnis = 0;

        for (AirQuality airQuality: werte) {
            if(airQuality.getValue() > ergebnis)
            {
                ergebnis = airQuality.getValue();
            }
        }

        return ergebnis;
    }

    /**
     * getAirSenoren
     * @return PagedModelSensoren mit allen Luftqualitaet Sensoren
     */
    private PagedModelSensor getAirSensoren()
    {
        SensorManagementApi sensorManagementApi = new SensorManagementApi();
        PagedModelSensor pagedModelSensor = null;
        try {
            pagedModelSensor = sensorManagementApi.byType(OtherConstants.SENSOR_TYPE_AIR_QUALITY, null, Integer.MAX_VALUE, null);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return pagedModelSensor;
    }

    /**
     * round1DecimalPlace
     * @param temp Temperatur
     * @return gerundet auf eine Nachkommastelle
     * rundet Temperaturwert auf zwei Nachkommastelle
     */
    private double round2DecimalPlace(double temp)
    {
        long factor = (long) Math.pow(100, 1);
        temp = temp * factor;
        long tmp = Math.round(temp);
        return (double) tmp / factor;
    }

    /**
     * getAirQualityRating
     * @param wert zu testender Wert
     * @return einstufung der Qualität
     * nimmt einen Luftqualitätswert entgegen und stuft diesen ein
     */
    public String getAirQualityRating(Double wert)
    {
        if(wert <= 50)
        {
            return OtherConstants.QUALITY_GOOD;
        }
        else if(wert <= 100&& wert >= 51)
        {
            return OtherConstants.QUALITY_MODERATE;
        }
        else if(wert <= 150&& wert >= 101)
        {
            return OtherConstants.QUALITY_UNHEALTHY_SENSITIVE;
        }
        else if(wert <= 200&& wert >= 151)
        {
            return OtherConstants.QUALITY_UNHEALTHY;
        }
        else if(wert <= 300&& wert >= 201)
        {
            return OtherConstants.QUALITY_VERY_UNHEALTHY;
        }
        else
        {
            return OtherConstants.QUALITY_HAZARDOUS;
        }
    }
}
