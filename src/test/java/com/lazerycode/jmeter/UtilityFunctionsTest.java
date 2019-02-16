package com.lazerycode.jmeter;

import com.lazerycode.jmeter.utility.UtilityFunctions;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests {@link UtilityFunctions} class
 */
public class UtilityFunctionsTest {

    @Test
    public void humanReadableCommandLineOutputTest() {
        String expected = "a b c d";
        List<String> testArray = new ArrayList<String>(Arrays.asList("a", "b", "c", "d"));
        String returnValue = UtilityFunctions.humanReadableCommandLineOutput(testArray);

        assertThat(returnValue).isEqualTo(expected);
    }

    @Test
    public void isNotSetMapTest() {
        Map testMap = null;
        assertThat(UtilityFunctions.isNotSet(testMap)).isTrue();

        Map testMap2 = Collections.emptyMap();
        assertThat(UtilityFunctions.isNotSet(testMap2)).isTrue();
    }

    @Test
    public void isNotSetStringTest() {
        String testString = null;
        String testString2 = "";
        String testString3 = "    ";

        assertThat(UtilityFunctions.isNotSet(testString)).isTrue();
        assertThat(UtilityFunctions.isNotSet(testString2)).isTrue();
        assertThat(UtilityFunctions.isNotSet(testString3)).isTrue();
    }

    @Test
    public void isNotSetFile() {
        File testFile = null;
        File testFile2 = new File("   ");
        File testFile3 = new File("");

        assertThat(UtilityFunctions.isNotSet(testFile)).isTrue();
        assertThat(UtilityFunctions.isNotSet(testFile2)).isTrue();
        assertThat(UtilityFunctions.isNotSet(testFile3)).isTrue();
    }

    @Test
    public void removeCarriageReturnsTest() {
        assertThat(UtilityFunctions.stripCarriageReturns("foo\n")).isEqualTo("foo");
        assertThat(UtilityFunctions.stripCarriageReturns("bar\r")).isEqualTo("bar");
        assertThat(UtilityFunctions.stripCarriageReturns("foo\nbar\r")).isEqualTo("foobar");
    }

    @Test
    public void checkPrivateConstructor() throws Exception {
        Constructor<UtilityFunctions> utilityFunctions;
        try {
            utilityFunctions = UtilityFunctions.class.getDeclaredConstructor();
            utilityFunctions.setAccessible(true);
            utilityFunctions.newInstance();
        } catch (InvocationTargetException e) {
            assertThat(e.getTargetException().getMessage()).isEqualTo("This class is non-instantiable.");
        }
    }
}
