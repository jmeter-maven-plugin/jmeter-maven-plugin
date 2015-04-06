package com.lazerycode.jmeter;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static junit.framework.Assert.assertTrue;
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
		assertTrue("null value returns false", UtilityFunctions.isNotSet(testMap));

		Map testMap2 = Collections.emptyMap();
		assertTrue("empty value returns false", UtilityFunctions.isNotSet(testMap2));
	}

	@Test
	public void isNotSetStringTest() {
		String testString = null;
		assertTrue("null value returns false", UtilityFunctions.isNotSet(testString));

		String testString2 = "";
		assertTrue("empty value returns false", UtilityFunctions.isNotSet(testString2));

		String testString3 = "    ";
		assertTrue("blank value returns false", UtilityFunctions.isNotSet(testString3));
	}

	@Test
	public void isNotSetFile() {
		File testFile = null;
		assertTrue("null value returns false", UtilityFunctions.isNotSet(testFile));

		File testFile2 = new File("   ");
		assertTrue("blank value returns false", UtilityFunctions.isNotSet(testFile2));

		File testFile3 = new File("");
		assertTrue("empty value returns false", UtilityFunctions.isNotSet(testFile3));
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
