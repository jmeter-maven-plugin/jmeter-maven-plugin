package com.lazerycode.jmeter.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesMapping;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestConfiguration {
    private String executionID;
    private String jmeterDirectoryPath;
    private String runtimeJarName;
    private Boolean resultsOutputIsCSVFormat;
    private Boolean generateReports;
    private String[] resultFilesLocations = new String[0];
    private Map<ConfigurationFiles, PropertiesMapping> propertiesMap;

    public String getExecutionID() {
        return executionID;
    }

    public void setExecutionID(String executionID) {
        this.executionID = executionID;
    }

    public String getJmeterDirectoryPath() {
        return jmeterDirectoryPath;
    }

    public String getJmeterWorkingDirectoryPath() {
        if (null == jmeterDirectoryPath) {
            return null;
        }
        return Paths.get(jmeterDirectoryPath, "bin").toString();
    }

    public void setJmeterDirectoryPath(String jmeterDirectoryPath) {
        this.jmeterDirectoryPath = jmeterDirectoryPath;
    }

    public String getRuntimeJarName() {
        return runtimeJarName;
    }

    public void setRuntimeJarName(String runtimeJarName) {
        this.runtimeJarName = runtimeJarName;
    }

    public Boolean getResultsOutputIsCSVFormat() {
        return resultsOutputIsCSVFormat;
    }

    public void setResultsOutputIsCSVFormat(Boolean resultsOutputIsCSVFormat) {
        this.resultsOutputIsCSVFormat = resultsOutputIsCSVFormat;
    }

    public List<String> getResultFilesLocations() {
        return Arrays.asList(resultFilesLocations);
    }

    public void setResultFilesLocations(List<String> resultFilesLocations) {
        this.resultFilesLocations = resultFilesLocations.toArray(new String[0]);
    }

    public Boolean getGenerateReports() {
        return generateReports;
    }

    public void setGenerateReports(Boolean generateReports) {
        this.generateReports = generateReports;
    }

    public Map<ConfigurationFiles, PropertiesMapping> getPropertiesMap() {
        return propertiesMap;
    }

    public void setPropertiesMap(Map<ConfigurationFiles, PropertiesMapping> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestConfiguration that = (TestConfiguration) o;
        return Objects.equals(executionID, that.executionID) &&
                Objects.equals(jmeterDirectoryPath, that.jmeterDirectoryPath) &&
                Objects.equals(resultsOutputIsCSVFormat, that.resultsOutputIsCSVFormat) &&
                Arrays.equals(resultFilesLocations, that.resultFilesLocations) &&
                Objects.equals(generateReports, that.generateReports);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(executionID, jmeterDirectoryPath, resultsOutputIsCSVFormat, generateReports);
        result = 31 * result + Arrays.hashCode(resultFilesLocations);
        return result;
    }
}
