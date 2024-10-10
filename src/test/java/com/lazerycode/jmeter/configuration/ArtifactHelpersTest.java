package com.lazerycode.jmeter.configuration;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.lazerycode.jmeter.configuration.ArtifactHelpers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class ArtifactHelpersTest {

    @Test
    public void jarArtifactIsDetectedAsLibrary() {
        Artifact artifact = new DefaultArtifact("com.example.test", "testArtifact", "jar", "1.0.0");

        assertThat(ArtifactHelpers.isArtifactALibrary(artifact)).isTrue();
    }

    @Test
    public void warArtifactIsDetectedAsLibrary() {
        Artifact artifact = new DefaultArtifact("com.example.test", "testArtifact", "war", "1.0.0");

        assertThat(ArtifactHelpers.isArtifactALibrary(artifact)).isTrue();
    }

    @Test
    public void zipArtifactIsDetectedAsLibrary() {
        Artifact artifact = new DefaultArtifact("com.example.test", "testArtifact", "zip", "1.0.0");

        assertThat(ArtifactHelpers.isArtifactALibrary(artifact)).isTrue();
    }

    @Test
    public void earArtifactIsDetectedAsLibrary() {
        Artifact artifact = new DefaultArtifact("com.example.test", "testArtifact", "ear", "1.0.0");

        assertThat(ArtifactHelpers.isArtifactALibrary(artifact)).isTrue();
    }

    @Test
    public void fooArtifactIsNotDetectedAsLibrary() {
        Artifact artifact = new DefaultArtifact("com.example.test", "testArtifact", "foo", "1.0.0");

        assertThat(ArtifactHelpers.isArtifactALibrary(artifact)).isFalse();
    }

    @Test
    public void olderArtifactReturnsTrue() throws InvalidVersionSpecificationException {
        Artifact artifactOne = new DefaultArtifact("com.example.test", "testArtifact", "foo", "1.0.0");
        Artifact artifactTwo = new DefaultArtifact("com.example.test", "testArtifact", "foo", "1.1.0");

        assertThat(isArtifactIsOlderThanArtifact(artifactOne, artifactTwo)).isTrue();
    }

    @Test
    public void newerArtifactReturnsFalse() throws InvalidVersionSpecificationException {
        Artifact artifactOne = new DefaultArtifact("com.example.test", "testArtifact", "foo", "1.1.0");
        Artifact artifactTwo = new DefaultArtifact("com.example.test", "testArtifact", "foo", "1.0.0");

        assertThat(isArtifactIsOlderThanArtifact(artifactOne, artifactTwo)).isFalse();
    }

    @Test
    public void artifactsThatAreSameVersionReturnsFalse() throws InvalidVersionSpecificationException {
        Artifact artifactOne = new DefaultArtifact("com.example.test", "testArtifact", "foo", "1.0.0");
        Artifact artifactTwo = new DefaultArtifact("com.example.test", "testArtifact", "foo", "1.0.0");

        assertThat(isArtifactIsOlderThanArtifact(artifactOne, artifactTwo)).isFalse();
    }

    @Test
    public void comparingArtifactsWhereOneHasInvalidVersionThrowsException() throws InvalidVersionSpecificationException {
        Artifact artifactOne = new DefaultArtifact("com.example.test", "testArtifact", "foo", "-0021986912836");
        Artifact artifactTwo = new DefaultArtifact("com.example.test", "testArtifact", "foo", "1.0.0");

        isArtifactIsOlderThanArtifact(artifactOne, artifactTwo);
    }

    @Test
    public void artifactsAreMatchingTypesIfIdentical() {
        Artifact artifactOne = new DefaultArtifact("com.example.test", "testArtifact", "some_classifier", "foo", "1.0.0");
        Artifact artifactTwo = new DefaultArtifact("com.example.test", "testArtifact", "some_classifier", "foo", "1.0.0");

        assertThat(artifactsAreMatchingTypes(artifactOne, artifactTwo)).isTrue();
    }

    @Test
    public void artifactsAreMatchingTypesIfVersionDiffers() {
        Artifact artifactOne = new DefaultArtifact("com.example.test", "testArtifact", "some_classifier", "foo", "1.0.0");
        Artifact artifactTwo = new DefaultArtifact("com.example.test", "testArtifact", "some_classifier", "foo", "1.1.0");

        assertThat(artifactsAreMatchingTypes(artifactOne, artifactTwo)).isTrue();
    }

    @Test
    public void artifactsAreNotMatchingTypesIfGroupIdDiffers() {
        Artifact artifactOne = new DefaultArtifact("com.example.test", "testArtifact", "some_classifier", "foo", "1.0.0");
        Artifact artifactTwo = new DefaultArtifact("com.example.toast", "testArtifact", "some_classifier", "foo", "1.0.0");

        assertThat(artifactsAreMatchingTypes(artifactOne, artifactTwo)).isFalse();
    }

    @Test
    public void artifactsAreNotMatchingTypesIfArtifactIdDiffers() {
        Artifact artifactOne = new DefaultArtifact("com.example.test", "testArtifact", "some_classifier", "foo", "1.0.0");
        Artifact artifactTwo = new DefaultArtifact("com.example.test", "testArtifuct", "some_classifier", "foo", "1.0.0");

        assertThat(artifactsAreMatchingTypes(artifactOne, artifactTwo)).isFalse();
    }

    @Test
    public void artifactsAreNotMatchingTypesIfExtensionDiffers() {
        Artifact artifactOne = new DefaultArtifact("com.example.test", "testArtifact", "some_classifier", "foo", "1.0.0");
        Artifact artifactTwo = new DefaultArtifact("com.example.test", "testArtifact", "some_classifier", "bar", "1.0.0");

        assertThat(artifactsAreMatchingTypes(artifactOne, artifactTwo)).isFalse();
    }

    @Test
    public void artifactsAreNotMatchingTypesIfClassifierDiffers() {
        Artifact artifactOne = new DefaultArtifact("com.example.test", "testArtifact", "some_classifier", "foo", "1.0.0");
        Artifact artifactTwo = new DefaultArtifact("com.example.test", "testArtifact", "some_other_classifier", "foo", "1.0.0");
        artifactTwo.getClassifier();

        assertThat(artifactsAreMatchingTypes(artifactOne, artifactTwo)).isFalse();
    }

// TODO rewrite this to work with new func
//    @Test
//    public void checkThatDefaultArrayIsStructuredCorrectly() {
//        String jmeterVersion = "1.2.3";
//        List<String> expected = new ArrayList<>();
//        JMETER_ARTIFACT_NAMES.forEach(artifactName ->
//                expected.add(String.format("%s:%s:%s", JMETER_GROUP_ID, artifactName, jmeterVersion))
//        );
//
//        assertThat(createDefaultJmeterArtifactsArray(jmeterVersion)).isEqualTo(expected);
//    }

    @Test
    public void checkTwoPartExclusionPatternIsConvertedIntoExclusion() throws MojoExecutionException {
        String pattern = "one:two";
        Exclusion expected = new Exclusion("one", "two", null, null);

        assertThat(convertExclusionPatternIntoExclusion(pattern)).isEqualTo(expected);
    }

    @Test
    public void checkThreePartExclusionPatternIsConvertedIntoExclusion() throws MojoExecutionException {
        String pattern = "one:two:three";
        Exclusion expected = new Exclusion("one", "two", "three", null);

        assertThat(convertExclusionPatternIntoExclusion(pattern)).isEqualTo(expected);
    }

    @Test
    public void checkFourPartExclusionPatternIsConvertedIntoExclusion() throws MojoExecutionException {
        String pattern = "one:two:three:four";
        Exclusion expected = new Exclusion("one", "two", "three", "four");

        assertThat(convertExclusionPatternIntoExclusion(pattern)).isEqualTo(expected);
    }

    @Test(expected = MojoExecutionException.class)
    public void checkOnePartExclusionPatternThrowsException() throws MojoExecutionException {
        String pattern = "one";
        convertExclusionPatternIntoExclusion(pattern);
    }

    @Test(expected = MojoExecutionException.class)
    public void checkFivePartExclusionPatternThrowsException() throws MojoExecutionException {
        String pattern = "one:two:three:four:five";
        convertExclusionPatternIntoExclusion(pattern);
    }

    @Test
    public void checkNullInputReturnsSetOfBlockedArtifacts() throws MojoExecutionException {
        Set<Exclusion> expected = new HashSet<>();
        for (String artifact : BLOCKED_ARTIFACTS) {
            expected.add(convertExclusionPatternIntoExclusion(artifact));
        }

        assertThat(setupExcludedArtifacts(null)).isEqualTo(expected);
    }

    @Test
    public void checkEmptyInputReturnsSetOfBlockedArtifacts() throws MojoExecutionException {
        Set<Exclusion> expected = new HashSet<>();
        for (String artifact : BLOCKED_ARTIFACTS) {
            expected.add(convertExclusionPatternIntoExclusion(artifact));
        }

        assertThat(setupExcludedArtifacts(Collections.emptyList())).isEqualTo(expected);
    }

    @Test
    public void checkListOfValidEntriesReturnsSetOfBlockedArtifacts() throws MojoExecutionException {
        List<String> input = Arrays.asList("com.lazerycode.test:testArtifact", "com.lazerycode.another:testArtifact");
        Set<Exclusion> expected = new HashSet<>();
        for (String artifact : BLOCKED_ARTIFACTS) {
            expected.add(convertExclusionPatternIntoExclusion(artifact));
        }
        for (String artifact : input) {
            expected.add(convertExclusionPatternIntoExclusion(artifact));
        }

        assertThat(setupExcludedArtifacts(input)).isEqualTo(expected);
    }

    @Test(expected = MojoExecutionException.class)
    public void checkListOfInvalidEntriesThrowsException() throws MojoExecutionException {
        List<String> invalidInput = Arrays.asList("com.lazerycode.test", "com.lazerycode.another:testArtifact");

        setupExcludedArtifacts(invalidInput);
    }

    @Test
    public void exclusionIsInCollectionReturnsTrue() {
        Exclusion expected = new Exclusion("one", "two", null, null);
        Collection<Exclusion> listOfExclusions = new HashSet<>();
        listOfExclusions.add(expected);

        assertThat(containsExclusion(listOfExclusions, expected)).isTrue();
    }

    @Test
    public void wildcardExclusionIsInCollectionReturnsTrue() {
        Exclusion expected = new Exclusion("one", "two", null, null);
        Collection<Exclusion> listOfExclusions = new HashSet<>();
        listOfExclusions.add(new Exclusion("one", "*", null, null));

        assertThat(containsExclusion(listOfExclusions, expected)).isTrue();
    }

    @Test
    public void exclusionNotInCollectionReturnsFalse() {
        Exclusion expected = new Exclusion("one", "two", null, null);
        Collection<Exclusion> listOfExclusions = new HashSet<>();
        listOfExclusions.add(new Exclusion("three", "four", null, null));

        assertThat(containsExclusion(listOfExclusions, expected)).isFalse();
    }

    @Test
    public void nullExclusionReturnsFalse() {
        Collection<Exclusion> listOfExclusions = new HashSet<>();
        listOfExclusions.add(new Exclusion("three", "four", null, null));

        assertThat(containsExclusion(listOfExclusions, null)).isFalse();
    }

    @Test
    public void nullCollectionReturnsFalse() {
        Exclusion expected = new Exclusion("one", "two", null, null);

        assertThat(containsExclusion(null, expected)).isFalse();
    }

    @Test
    public void emptyCollectionReturnsFalse() {
        Exclusion expected = new Exclusion("one", "two", null, null);

        assertThat(containsExclusion(Collections.emptyList(), expected)).isFalse();
    }

    @Test
    public void artifactIsNotExcludedReturnsFalse() {
        Artifact expected = new DefaultArtifact("one", "two", null, null);
        Collection<Exclusion> listOfExclusions = new HashSet<>();
        listOfExclusions.add(new Exclusion("one", "two", null, null));

        assertThat(artifactIsNotExcluded(listOfExclusions, expected)).isFalse();
    }

    @Test
    public void wildcardExclusionIsInCollectionReturnsFalse() {
        Artifact expected = new DefaultArtifact("one", "two", null, null);
        Collection<Exclusion> listOfExclusions = new HashSet<>();
        listOfExclusions.add(new Exclusion("one", "*", null, null));

        assertThat(artifactIsNotExcluded(listOfExclusions, expected)).isFalse();
    }

    @Test
    public void exclusionNotInCollectionReturnsTrue() {
        Artifact expected = new DefaultArtifact("one", "two", null, null);
        Collection<Exclusion> listOfExclusions = new HashSet<>();
        listOfExclusions.add(new Exclusion("three", "four", null, null));

        assertThat(artifactIsNotExcluded(listOfExclusions, expected)).isTrue();
    }

    @Test
    public void nullExclusionReturnsTrue() {
        Collection<Exclusion> listOfExclusions = new HashSet<>();
        listOfExclusions.add(new Exclusion("three", "four", null, null));

        assertThat(artifactIsNotExcluded(listOfExclusions, null)).isTrue();
    }

    @Test
    public void nullCollectionReturnsTrue() {
        Artifact expected = new DefaultArtifact("one", "two", null, null);

        assertThat(artifactIsNotExcluded(null, expected)).isTrue();
    }

    @Test
    public void emptyCollectionReturnsTrue() {
        Artifact expected = new DefaultArtifact("one", "two", null, null);

        assertThat(artifactIsNotExcluded(Collections.emptyList(), expected)).isTrue();
    }

    @Test
    public void checkPrivateConstructor() throws Exception {
        Constructor<ArtifactHelpers> artifactHelpersConstructor;
        try {
            artifactHelpersConstructor = ArtifactHelpers.class.getDeclaredConstructor();
            artifactHelpersConstructor.setAccessible(true);
            artifactHelpersConstructor.newInstance();
        } catch (InvocationTargetException e) {
            assertThat(e.getTargetException().getMessage()).isEqualTo("This class is non-instantiable.");
        }
    }

    @Test(expected = InstantiationError.class)
    public void cannotInstantiateClass() {
        new ArtifactHelpers();
    }

    //repositorySystem, repositorySystemSession, repositoryList

    @Test
    public void resolveArtifactVersionWithNoRange() throws VersionRangeResolutionException {
        RepositorySystem system = mock(RepositorySystem.class);
        RepositorySystemSession session = mock(RepositorySystemSession.class);
        Artifact artifact = new DefaultArtifact("com.example.test", "testArtifact", "jar", "1.0.0");
        Artifact resolvedArtifact = resolveArtifactVersion(system, session, null, artifact);

        assertThat(resolvedArtifact.getVersion()).isEqualTo("1.0.0");
    }

    @Test
    public void resolveArtifactVersionRangeWithSquareBrackets() throws InvalidVersionSpecificationException, VersionRangeResolutionException {
        GenericVersionScheme versionScheme = new GenericVersionScheme();
        Version[] availableVersions = new Version[]{versionScheme.parseVersion("1.0.0"), versionScheme.parseVersion("1.1.0"), versionScheme.parseVersion("1.1.1"), versionScheme.parseVersion("1.2.0")};
        Artifact artifact = new DefaultArtifact("com.example.test", "testArtifact", "jar", "[1.0.0, 1.2.0]");

        VersionRangeRequest versionRangeRequest = new VersionRangeRequest(artifact, null, null);
        RepositorySystemSession session = mock(RepositorySystemSession.class);
        RepositorySystem system = mock(RepositorySystem.class);
        Mockito.when(system.resolveVersionRange(any(RepositorySystemSession.class), any(VersionRangeRequest.class))).thenReturn(new VersionRangeResult(versionRangeRequest).setVersions(Arrays.asList(availableVersions)));

        Artifact resolvedArtifact = resolveArtifactVersion(system, session, null, artifact);

        assertThat(resolvedArtifact.getVersion()).isEqualTo("1.2.0");
    }

    @Test
    public void resolveArtifactVersionRangeWithParentheses() throws InvalidVersionSpecificationException, VersionRangeResolutionException {
        GenericVersionScheme versionScheme = new GenericVersionScheme();
        Version[] availableVersions = new Version[]{versionScheme.parseVersion("1.0.0"), versionScheme.parseVersion("1.1.0"), versionScheme.parseVersion("1.1.1"), versionScheme.parseVersion("1.2.0")};
        Artifact artifact = new DefaultArtifact("com.example.test", "testArtifact", "jar", "(1.0.0, 1.2.0)");

        VersionRangeRequest versionRangeRequest = new VersionRangeRequest(artifact, null, null);
        RepositorySystemSession session = mock(RepositorySystemSession.class);
        RepositorySystem system = mock(RepositorySystem.class);
        Mockito.when(system.resolveVersionRange(any(RepositorySystemSession.class), any(VersionRangeRequest.class))).thenReturn(new VersionRangeResult(versionRangeRequest).setVersions(Arrays.asList(availableVersions)));

        Artifact resolvedArtifact = resolveArtifactVersion(system, session, null, artifact);

        assertThat(resolvedArtifact.getVersion()).isEqualTo("1.2.0");
    }

    @Test
    public void resolveArtifactVersionMixedRangeStartingWithWithSquareBrackets() throws InvalidVersionSpecificationException, VersionRangeResolutionException {
        GenericVersionScheme versionScheme = new GenericVersionScheme();
        Version[] availableVersions = new Version[]{versionScheme.parseVersion("1.0.0"), versionScheme.parseVersion("1.1.0"), versionScheme.parseVersion("1.1.1"), versionScheme.parseVersion("1.2.0")};
        Artifact artifact = new DefaultArtifact("com.example.test", "testArtifact", "jar", "[1.0.0, 1.2.0)");

        VersionRangeRequest versionRangeRequest = new VersionRangeRequest(artifact, null, null);
        RepositorySystemSession session = mock(RepositorySystemSession.class);
        RepositorySystem system = mock(RepositorySystem.class);
        Mockito.when(system.resolveVersionRange(any(RepositorySystemSession.class), any(VersionRangeRequest.class))).thenReturn(new VersionRangeResult(versionRangeRequest).setVersions(Arrays.asList(availableVersions)));

        Artifact resolvedArtifact = resolveArtifactVersion(system, session, null, artifact);

        assertThat(resolvedArtifact.getVersion()).isEqualTo("1.2.0");
    }

    @Test
    public void resolveArtifactVersionMixedRangeStartingWithWithParentheses() throws InvalidVersionSpecificationException, VersionRangeResolutionException {
        GenericVersionScheme versionScheme = new GenericVersionScheme();
        Version[] availableVersions = new Version[]{versionScheme.parseVersion("1.0.0"), versionScheme.parseVersion("1.1.0"), versionScheme.parseVersion("1.1.1"), versionScheme.parseVersion("1.2.0")};
        Artifact artifact = new DefaultArtifact("com.example.test", "testArtifact", "jar", "(1.0.0, 1.2.0]");

        VersionRangeRequest versionRangeRequest = new VersionRangeRequest(artifact, null, null);
        RepositorySystemSession session = mock(RepositorySystemSession.class);
        RepositorySystem system = mock(RepositorySystem.class);
        Mockito.when(system.resolveVersionRange(any(RepositorySystemSession.class), any(VersionRangeRequest.class))).thenReturn(new VersionRangeResult(versionRangeRequest).setVersions(Arrays.asList(availableVersions)));

        Artifact resolvedArtifact = resolveArtifactVersion(system, session, null, artifact);

        assertThat(resolvedArtifact.getVersion()).isEqualTo("1.2.0");
    }

}
