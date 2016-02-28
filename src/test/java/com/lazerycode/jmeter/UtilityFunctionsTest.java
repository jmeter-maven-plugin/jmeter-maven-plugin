package com.lazerycode.jmeter;

import com.lazerycode.jmeter.utility.UtilityFunctions;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests {@link UtilityFunctions} class
 */
public class UtilityFunctionsTest {

	@Test
	public void humanReadableCommandLineOutputTest() {
		List<String> testArray = new ArrayList<String>(Arrays.asList("a", "b", "c", "d"));

		String returnValue = UtilityFunctions.humanReadableCommandLineOutput(testArray);

		String expected = "a b c d";

		assertEquals("string does not match array input", expected, returnValue);
	}

	@Test
	public void isNotSetMapTest() {
		Map testMap = null;
		assertThat(UtilityFunctions.isNotSet(testMap), is(equalTo(true)));

		Map testMap2 = Collections.emptyMap();
		assertThat(UtilityFunctions.isNotSet(testMap2), is(equalTo(true)));
	}

	@Test
	public void isNotSetStringTest() {
		String testString = null;
		assertThat(UtilityFunctions.isNotSet(testString), is(equalTo(true)));

		String testString2 = "";
		assertThat(UtilityFunctions.isNotSet(testString2), is(equalTo(true)));

		String testString3 = "    ";
		assertThat(UtilityFunctions.isNotSet(testString3), is(equalTo(true)));
	}

	@Test
	public void isNotSetFile() {
		File testFile = null;
		assertThat(UtilityFunctions.isNotSet(testFile), is(equalTo(true)));

		File testFile2 = new File("   ");
		assertThat(UtilityFunctions.isNotSet(testFile2), is(equalTo(true)));

		File testFile3 = new File("");
		assertThat(UtilityFunctions.isNotSet(testFile3), is(equalTo(true)));
	}

	@Test
	public void removeCarriageReturnsTest() {
		assertThat(UtilityFunctions.stripCarriageReturns("foo\n"),
				is(equalTo("foo")));
		assertThat(UtilityFunctions.stripCarriageReturns("bar\r"),
				is(equalTo("bar")));
		assertThat(UtilityFunctions.stripCarriageReturns("foo\nbar\r"),
				is(equalTo("foobar")));
	}

	@Test
	public void checkPrivateConstructor() throws Exception {
		Constructor<UtilityFunctions> utilityFunctions;
		try {
			utilityFunctions = UtilityFunctions.class.getDeclaredConstructor();
			utilityFunctions.setAccessible(true);
			utilityFunctions.newInstance();
		} catch (InvocationTargetException e) {
			assertThat(e.getTargetException().getMessage(),
					is(equalTo("This class is non-instantiable.")));
		}
	}
}
