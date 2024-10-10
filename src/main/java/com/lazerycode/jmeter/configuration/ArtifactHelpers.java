package com.lazerycode.jmeter.configuration;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArtifactHelpers {

    private static final List<String> LIBRARY_ARTIFACT_EXTENSIONS = Arrays.asList("jar", "war", "zip", "ear");
    private static final Pattern COORDINATE_PATTERN = Pattern.compile("([^: ]+):([^: ]+)(:([^: ]+))?(:([^: ]+))?");
    private static final String ARTIFACT_STAR = "*";
    static final String JMETER_GROUP_ID = "org.apache.jmeter";
    static final List<String> JMETER_ARTIFACT_NAMES = Arrays.asList(
            "ApacheJMeter",
            "ApacheJMeter_bolt",
            "ApacheJMeter_components",
            "ApacheJMeter_config",
            "ApacheJMeter_core",
            "ApacheJMeter_ftp",
            "ApacheJMeter_functions",
            "ApacheJMeter_http",
            "ApacheJMeter_java",
            "ApacheJMeter_jdbc",
            "ApacheJMeter_jms",
            "ApacheJMeter_junit",
            "ApacheJMeter_ldap",
            "ApacheJMeter_mail",
            "ApacheJMeter_mongodb",
            "ApacheJMeter_native",
            "ApacheJMeter_tcp",
            "jorphan"
    );
    static final List<String> BLOCKED_ARTIFACTS = Arrays.asList(
            // Exclude broken artifacts identified in https://bz.apache.org/bugzilla/show_bug.cgi?id=57555
            "d-haven-managed-pool:d-haven-managed-pool",
            "event:event",
            // Exclude broken artifacts identified in https://bz.apache.org/bugzilla/show_bug.cgi?id=57734
            "commons-pool2:commons-pool2",
            "commons-math3:commons-math3",
            // Exclude conflicting libraries since JMeter 3.2
            "logkit:logkit",
            "avalon-logkit:avalon-logkit"
    );

    /**
     * Make constructor private as this is a non-instantiable helper classes
     */
    ArtifactHelpers() throws InstantiationError {
        throw new InstantiationError("This class is non-instantiable.");
    }

    /**
     * Build up an exclusion set
     *
     * @param excludedArtifacts List of artifact coords patterns in the format &lt;groupId&gt;:&lt;artifactId&gt;[:&lt;extension&gt;][:&lt;classifier&gt;]
     * @return Set&lt;Exclusion&gt;
     * @throws MojoExecutionException if any patterns are invalid
     */
    public static Set<Exclusion> setupExcludedArtifacts(List<String> excludedArtifacts) throws MojoExecutionException {
        Set<Exclusion> exclusionSet = new HashSet<>();
        for (String artifact : Optional.ofNullable(excludedArtifacts).orElse(Collections.emptyList())) {
            exclusionSet.add(convertExclusionPatternIntoExclusion(artifact));
        }
        for (String artifact : BLOCKED_ARTIFACTS) {
            exclusionSet.add(convertExclusionPatternIntoExclusion(artifact));
        }

        return exclusionSet;
    }

    /**
     * Convert an exclusion pattern into an Exclusion object
     *
     * @param exceptionPattern coords pattern in the format <groupId>:<artifactId>[:<extension>][:<classifier>]
     * @return Exclusion object
     * @throws MojoExecutionException if coords pattern is invalid
     */
    static Exclusion convertExclusionPatternIntoExclusion(String exceptionPattern) throws MojoExecutionException {
        Matcher matcher = COORDINATE_PATTERN.matcher(exceptionPattern);
        if (!matcher.matches()) {
            throw new MojoExecutionException(String.format("Bad artifact coordinates %s, expected format is <groupId>:<artifactId>[:<extension>][:<classifier>]", exceptionPattern));
        }

        return new Exclusion(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(6));
    }

    /**
     * Exclusive can be specified by wildcard:
     * -- groupId:artifactId:*:*
     * -- groupId:*:*:*
     * <p>
     * And in general, to require a strict match up to the version and the classifier is not necessary
     * <p>
     * TODO: the correct fix would be to rewrite {@link Exclusion # equals (Object)}, but what about the boundary case:
     * If contains (id1: *: *: *, id1: id2: *: *) == true, then that's equals ??
     * TODO: there must be useful code in Aether or maven on this topic
     * <p>
     *
     * @param exclusions Collection of exclusions
     * @param exclusion  Specific exclusion to search through collection for
     * @return boolean
     */
    public static boolean containsExclusion(Collection<Exclusion> exclusions, Exclusion exclusion) {
        return Optional.ofNullable(exclusions).orElse(Collections.emptyList())
                .stream().anyMatch(selectedExclusion ->
                        null != exclusion && selectedExclusion.getGroupId().equals(exclusion.getGroupId()) &&
                                (selectedExclusion.getArtifactId().equals(exclusion.getArtifactId()) || (selectedExclusion.getArtifactId().equals(ARTIFACT_STAR)))
                );
    }

    /**
     * Check that an artifact is not excluded
     *
     * @param exclusions Collection of exclusions
     * @param artifact   Specific artifact to search through collection for
     * @return boolean
     */
    public static boolean artifactIsNotExcluded(Collection<Exclusion> exclusions, Artifact artifact) {
        return Optional.ofNullable(exclusions).orElse(Collections.emptyList())
                .stream().noneMatch(selectedExclusion ->
                        null != artifact && selectedExclusion.getGroupId().equals(artifact.getGroupId()) &&
                                (selectedExclusion.getArtifactId().equals(artifact.getArtifactId()) || (selectedExclusion.getArtifactId().equals(ARTIFACT_STAR)))
                );
    }

    /**
     * Create a default Array of JMeter artifact coordinates
     *
     * @param jmeterVersion JMeter version for artifacts
     * @return List&lt;String&gt; of artifact coords
     */
    public static List<String> createDefaultJmeterArtifactsArray(String jmeterVersion) {
        List<String> artifacts = new ArrayList<>();
        JMETER_ARTIFACT_NAMES.forEach(artifactName ->
                artifacts.add(String.format("%s:%s:%s", JMETER_GROUP_ID, artifactName, jmeterVersion))
        );

        return artifacts;
    }

    /**
     * Check to see if artifact is a library
     *
     * @param artifact {@link Artifact}
     * @return boolean if true
     */
    public static boolean isArtifactALibrary(Artifact artifact) {
        return LIBRARY_ARTIFACT_EXTENSIONS.contains(artifact.getExtension());
    }

    /**
     * Check to see if a specified artifact is the same version, or a newer version that the comparative artifact
     *
     * @param artifact           An artifact
     * @param comparisonArtifact another Artifact to compare with.
     * @return true if artifact is the same or a higher version.  False if the artifact is a lower version
     * @throws InvalidVersionSpecificationException Unable to get artifact versions
     */
    public static boolean isArtifactIsOlderThanArtifact(Artifact artifact, Artifact comparisonArtifact) throws InvalidVersionSpecificationException {
        GenericVersionScheme genericVersionScheme = new GenericVersionScheme();
        Version firstArtifactVersion = genericVersionScheme.parseVersion(artifact.getVersion());
        Version secondArtifactVersion = genericVersionScheme.parseVersion(comparisonArtifact.getVersion());

        return firstArtifactVersion.compareTo(secondArtifactVersion) < 0;

    }

    /**
     * Checks to see if two artifacts are of the same type (but not necessarily the same version)
     *
     * @param first  An Artifact
     * @param second A comparison Artifact
     * @return true if the Artifacts match, false if they don't
     */
    public static boolean artifactsAreMatchingTypes(Artifact first, Artifact second) {
        return first.getGroupId().equals(second.getGroupId()) &&
                first.getArtifactId().equals(second.getArtifactId()) &&
                first.getExtension().equals(second.getExtension()) &&
                first.getClassifier().equals(second.getClassifier());
    }

    /**
     * Ensure we have a valid version number to download an artifact.
     * This will check to see if the version number supplied is a range or not.
     * If it is a range it will replace the range with the highest version (inside the range) available
     *
     * @param repositorySystem        system repositories
     * @param repositorySystemSession session repositories
     * @param repositoryList          list of repositories to try and download artifacts from
     * @param desiredArtifact         the artifact we want to download
     * @return the artifact with the version number set to a static version number instead of a range
     * @throws VersionRangeResolutionException Thrown if we cannot resolve any versions
     */
    public static Artifact resolveArtifactVersion(RepositorySystem repositorySystem, RepositorySystemSession repositorySystemSession, List<RemoteRepository> repositoryList, Artifact desiredArtifact) throws VersionRangeResolutionException {
        Pattern isAVersionRange = Pattern.compile("[\\[|(].+[]|)]");
        if (isAVersionRange.matcher(desiredArtifact.getVersion()).matches()) {
            VersionRangeRequest versionRangeRequest = new VersionRangeRequest(desiredArtifact, repositoryList, null);
            VersionRangeResult versionRangeResult = repositorySystem.resolveVersionRange(repositorySystemSession, versionRangeRequest);
            return desiredArtifact.setVersion(versionRangeResult.getHighestVersion().toString());
        }
        return desiredArtifact;
    }
}
