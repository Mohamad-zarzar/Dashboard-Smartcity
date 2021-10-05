package com.app.base;

import io.swagger.client.ApiException;
import io.swagger.client.api.AirQualityMeasuresApi;
import io.swagger.client.api.ParkingMeasuresApi;
import io.swagger.client.api.TemperatureMeasuresApi;
import io.swagger.client.model.*;

public class API_Beispiel {

    public void test()  {

        try {

            AirQualityMeasuresApi airQualityMeasuresApi = new AirQualityMeasuresApi();
            PagedModelAirQuality airquality = airQualityMeasuresApi.all(null,null, null);
            PagedModelAirQualityEmbedded test = airquality.getEmbedded();
            System.out.println(test.getAirQualities());


            TemperatureMeasuresApi temperatureMeasuresApi = new TemperatureMeasuresApi();
            PagedModelTemperature temperature = temperatureMeasuresApi.all2(null,null, null);
            PagedModelTemperatureEmbedded test2 = temperature.getEmbedded();
            System.out.println(test2.getTemperatures());

            ParkingMeasuresApi parkingMeasuresApi = new ParkingMeasuresApi();
            PagedModelParkingGroup modelParkingGroup = parkingMeasuresApi.allGroups(null,null,null);
            PagedModelParkingGroupEmbedded test3 = modelParkingGroup.getEmbedded();
            System.out.println(test3.getParkingGroups());
        }
        catch (ApiException apiException)
        {
           apiException.toString();
        }
    }
}
