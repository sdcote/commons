package coyote.commons.rtw;

import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransformEngineFactoryTest {

    @Test
    public void testArrayBasedPreProcessConfig() {
        String json = "{\n" +
                "    \"preprocess\": [\n" +
                "      {\n" +
                "        \"class\":\"WebGet\",\n" +
                "         \"source\": \"https://api.open-meteo.com/v1/forecast?latitude=40.0205&longitude=-83.1775&current=temperature_2m,weather_code&temperature_unit=fahrenheit\",\n" +
                "         \"target\": \"weather_data.json\"\n" +
                "      }\n" +
                "    ]\n" +
                "}";

        DataFrame cfg = JSONMarshaler.marshal(json).get(0);
        TransformEngine engine = TransformEngineFactory.getInstance(cfg);

        assertNotNull(engine);
        assertTrue(engine.getPreProcessTasks().size() > 0, "Engine should have at least one preprocess task");
    }
    @Test
    public void testMapBasedPreProcessConfig() {
        String json = "{\n" +
                "    \"preprocess\": {\n" +
                "      \"WebGet\": {\n" +
                "         \"source\": \"https://api.open-meteo.com/v1/forecast?latitude=40.0205&longitude=-83.1775&current=temperature_2m,weather_code&temperature_unit=fahrenheit\",\n" +
                "         \"target\": \"weather_data.json\"\n" +
                "      }\n" +
                "    }\n" +
                "}";

        DataFrame cfg = JSONMarshaler.marshal(json).get(0);
        TransformEngine engine = TransformEngineFactory.getInstance(cfg);

        assertNotNull(engine);
        assertTrue(engine.getPreProcessTasks().size() > 0, "Engine should have at least one preprocess task");
    }


    @Test
    public void testArrayBasedPostProcessConfig() {
        String json = "{\n" +
                "    \"postprocess\": [\n" +
                "      {\n" +
                "        \"class\":\"WebGet\",\n" +
                "         \"source\": \"https://api.open-meteo.com/v1/forecast?latitude=40.0205&longitude=-83.1775&current=temperature_2m,weather_code&temperature_unit=fahrenheit\",\n" +
                "         \"target\": \"weather_data.json\"\n" +
                "      }\n" +
                "    ]\n" +
                "}";

        DataFrame cfg = JSONMarshaler.marshal(json).get(0);
        TransformEngine engine = TransformEngineFactory.getInstance(cfg);

        assertNotNull(engine);
        assertTrue(engine.getPostProcessTasks().size() > 0, "Engine should have at least one postprocess task");
    }

    @Test
    public void testArrayBasedTransformConfig() {
        String json = "{\"transform\": [{\"class\":\"FileClassifier\"}]}";

        DataFrame cfg = JSONMarshaler.marshal(json).get(0);
        TransformEngine engine = TransformEngineFactory.getInstance(cfg);

        assertNotNull(engine);
        assertTrue(engine.getTransformers().size() > 0, "Engine should have at least one transformer");
    }

    @Test
    public void testMapBasedTransformConfig() {
        String json = "{\"transform\": {\"FileClassifier\": {}}}";

        DataFrame cfg = JSONMarshaler.marshal(json).get(0);
        TransformEngine engine = TransformEngineFactory.getInstance(cfg);

        assertNotNull(engine);
        assertTrue(engine.getTransformers().size() > 0, "Engine should have at least one transformer");
    }

    @Test
    public void testSingleObjectWriterConfig() {
        String json = "{\"writer\": {\"class\":\"ConsoleWriter\"}}";
        DataFrame cfg = JSONMarshaler.marshal(json).get(0);
        TransformEngine engine = TransformEngineFactory.getInstance(cfg);
        assertNotNull(engine);
        assertEquals(1, engine.getWriters().size());
    }

    @Test
    public void testArrayBasedWriterConfig() {
        String json = "{\"writer\": [{\"class\":\"ConsoleWriter\"}, {\"class\":\"ConsoleWriter\"}]}";
        DataFrame cfg = JSONMarshaler.marshal(json).get(0);
        TransformEngine engine = TransformEngineFactory.getInstance(cfg);
        assertNotNull(engine);
        assertEquals(2, engine.getWriters().size());
    }

    @Test
    public void testMapBasedWriterConfig() {
        String json = "{\"writer\": {\"ConsoleWriter\": {}, \"CsvWriter\": {\"target\": \"out.csv\"}}}";
        DataFrame cfg = JSONMarshaler.marshal(json).get(0);
        TransformEngine engine = TransformEngineFactory.getInstance(cfg);
        assertNotNull(engine);
        assertEquals(2, engine.getWriters().size());
    }
    @Test
    public void testInvalidWriterConfigThrowsRuntimeException() {
        String json = "{\"writer\": {\"NonExistentWriter\": {}}}";
        DataFrame cfg = JSONMarshaler.marshal(json).get(0);
        assertThrows(RTWConfigurationException.class, () -> {
            TransformEngineFactory.getInstance(cfg);
        });
    }
}
