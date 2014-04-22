package com.lazerycode.jmeter.properties;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PropertyHandlerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testPropertyHandlerWithSourceDirectory() throws Exception {
       Assert.assertNotNull(new PropertyHandler(folder.newFolder("source"), folder.newFolder("target"), null, false, true));
    }

    @Test(expected=MojoExecutionException.class)
    public void testPropertyHandlerWithoutSourceDirectoryWithError() throws Exception {
        new PropertyHandler(new File("/dir_no_exists"), folder.newFolder("target"), null, false, true);
    }

    @Test
    public void testPropertyHandlerWithoutSourceDirectoryWithoutError() throws Exception {
        Assert.assertNotNull(new PropertyHandler(new File("/dir_no_exists"), folder.newFolder("target"), null, false, false));
    }
}
