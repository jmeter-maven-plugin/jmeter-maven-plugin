package com.lazerycode.jmeter.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class MappingTest {

    @Test()
    public void checkThatPropertiesMapCanBeReadInCorrectly() throws URISyntaxException {
        URL configFile = this.getClass().getResource("/mappingFiles/properties.json");
        File testPropertiesFile = new File(configFile.toURI());

        Configuration jsonPathConfiguration = Configuration
                .builder()
                .mappingProvider(new JacksonMappingProvider())
                .jsonProvider(new JacksonJsonProvider())
                .build();

        TestConfiguration parser = null;
        try (FileReader jsonFileReader = new FileReader(testPropertiesFile)) {
            parser = JsonPath
                    .using(jsonPathConfiguration)
                    .parse(IOUtils.toString(jsonFileReader))
                    .read("$", TestConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(parser.getPropertiesMap().size()).isEqualTo(7);
    }

}


