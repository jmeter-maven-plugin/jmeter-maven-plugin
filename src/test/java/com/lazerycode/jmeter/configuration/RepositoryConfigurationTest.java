package com.lazerycode.jmeter.configuration;

import org.eclipse.aether.repository.RemoteRepository;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RepositoryConfigurationTest {

    @Test
    public void CorrectRemoteRepositoryIsReturnedTest() {
        String id = "some id";
        String type = "default";
        String url = "http://www.example.com";
        RemoteRepository expected = new RemoteRepository.Builder(id, type, url).build();
        RepositoryConfiguration newRepository = new RepositoryConfiguration();
        newRepository.setId(id);
        newRepository.setType(type);
        newRepository.setUrl(url);

        assertThat(newRepository.getRemoteRepository()).isEqualTo(expected);
    }

    @Test
    public void GetterAndSetterTest() {
        String id = "some id";
        String type = "default";
        String url = "http://www.example.com";
        RepositoryConfiguration repository = new RepositoryConfiguration();
        repository.setId(id);
        repository.setType(type);
        repository.setUrl(url);

        assertThat(repository.getId()).isEqualTo(id);
        assertThat(repository.getType()).isEqualTo(type);
        assertThat(repository.getUrl()).isEqualTo(url);
    }
}
