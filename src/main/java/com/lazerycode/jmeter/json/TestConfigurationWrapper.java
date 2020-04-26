package com.lazerycode.jmeter.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import net.minidev.json.JSONArray;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

/**
 * Allows user to specify the files they want to check.
 */
public class TestConfigurationWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(JMeterArgumentsArray.class);
    private static final String DEFAULT_CONFIGURATION_NAME = "default_plugin_configuration";  //FIXME use a hash to make it more likely to be unique? 61695e67f0df122c254df14fa94b511ab02ad4f7f95a89fe08893fc655e2027d
    private ObjectMapper mapper = new ObjectMapper();
    private TestConfiguration testConfiguration;

    public TestConfigurationWrapper() {
        testConfiguration = new TestConfiguration();
    }

    public TestConfigurationWrapper(File jsonFile, String executionIdName) throws MojoExecutionException {
        Configuration jsonPathConfiguration = Configuration.builder()
                .mappingProvider(new JacksonMappingProvider())
                .jsonProvider(new JacksonJsonProvider())
                .build();
        try (FileReader jsonFileReader = new FileReader(jsonFile)) {
            Filter configFilter = filter(
                    where("executionID").is(Optional.ofNullable(executionIdName).orElse(DEFAULT_CONFIGURATION_NAME))
            );
            testConfiguration = JsonPath
                    .using(jsonPathConfiguration)
                    .parse(
                            JsonPath.parse(IOUtils.toString(jsonFileReader))
                                    .read("$..[?]", JSONArray.class, configFilter)
                                    .toJSONString()
                    ).read("$[0]", TestConfiguration.class);
        } catch (Exception ex) {
            LOGGER.debug("Using: " + jsonFile + " with execution id: " + executionIdName);
            throw new MojoExecutionException(String.format("%s\nHave you added the configure goal to your POM?\n" +
                    "    <execution>\n" +
                    "        <id>configuration</id>\n" +
                    "        <goals>\n" +
                    "            <goal>configure</goal>\n" +
                    "        </goals>\n" +
                    "    </execution>", ex.getMessage()), ex);
        }
    }

    String getFullConfig() throws JsonProcessingException {
        return mapper.writeValueAsString(testConfiguration);
    }

    public void writeResultFilesConfigTo(String configLocation) throws MojoExecutionException {
        //TODO do we need to worry about file locking?
        TestConfigurations configurations = new TestConfigurations();
        Path configurationFilePath = Paths.get(configLocation);
        try {
            if (Files.exists(configurationFilePath)) {
                configurations = mapper.readValue(configurationFilePath.toFile(), TestConfigurations.class);
            }
            configurations.getConfigurations().removeIf(config -> config.getExecutionID().equals(testConfiguration.getExecutionID()));
            configurations.getConfigurations().add(testConfiguration);
            mapper.writeValue(configurationFilePath.toFile(), configurations);
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    public TestConfiguration getCurrentTestConfiguration() {
        return this.testConfiguration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestConfigurationWrapper that = (TestConfigurationWrapper) o;
        return Objects.equals(testConfiguration, that.testConfiguration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testConfiguration);
    }
}
