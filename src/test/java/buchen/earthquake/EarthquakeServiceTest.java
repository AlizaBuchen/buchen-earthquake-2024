package buchen.earthquake;


import buchen.earthquake.json.FeatureCollection;
import buchen.earthquake.json.Properties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EarthquakeServiceTest
{
    @Test
    void oneHour()
    {
        //given
        EarthquakeService service = new EarthquakeServiceFactory().getService();

        //when
        FeatureCollection collection = service.oneHour().blockingFirst(); //only use blockingGet in tests

        //then
        Properties properties = collection.features[0].properties;
        assertNotNull(properties.place);
        assertNotEquals(0, properties.mag);
        assertNotEquals(0, properties.time);
    }

    @Test
    void oneMonth()
    {
        //given
        EarthquakeService service = new EarthquakeServiceFactory().getService();

        //when
        FeatureCollection collection = service.significant30().blockingFirst(); //only use blockingGet in tests

        //then
        Properties properties = collection.features[0].properties;
        assertNotNull(properties.place);
        assertNotEquals(0, properties.mag);
        assertNotEquals(0, properties.time);
    }
}