package com.lazerycode.jmeter.configuration;

import org.eclipse.aether.repository.RemoteRepository;

import static org.eclipse.aether.repository.RemoteRepository.Builder;

/**
 * Allows you to specify additional remote repositories
 * <br>
 * Configuration in pom.xml:
 * <br>
 * <pre>
 * {@code
 * <repository>
 *     <id></id>
 *     <type></type>
 *     <url></url>
 * </repository>
 * }
 * </pre>
 *
 * @author Mark Collin
 */

public class RepositoryConfiguration {

    private String id;
    private String type;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RemoteRepository getRemoteRepository() {
        return new Builder(id, type, url).build();
    }
}
