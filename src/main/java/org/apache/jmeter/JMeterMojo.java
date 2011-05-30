package org.apache.jmeter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.security.Permission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.DirectoryScanner;

/**
 * JMeter Maven plugin.
 * 
 * @author Tim McCune 
 * @goal jmeter
 * @requiresProject true
 */
public class JMeterMojo extends AbstractMojo {


    /**
     * Path to a Jmeter test XML file.
     * Relative to srcDir.
     * May be declared instead of the parameter includes.
     *
     * @parameter
     */
    private File jmeterTestFile;

    /**
     * Sets the list of include patterns to use in directory scan for JMeter Test XML files.
     * Relative to srcDir.
     * May be declared instead of a single jmeterTestFile.
     * Ignored if parameter jmeterTestFile is given.
     *
     * @parameter
     */
    private List<String> includes;

    /**
     * Sets the list of exclude patterns to use in directory scan for Test files.
     * Relative to srcDir.
     * Ignored if parameter jmeterTestFile file is given.
     *
     * @parameter
     */
    private List<String> excludes;

    /**
     * Path under which JMeter test XML files are stored.
     *
     * @parameter expression="${jmeter.testfiles.basedir}"
     *          default-value="${basedir}/src/test/jmeter"
     */
    private File srcDir;

    /**
     * Directory in which the reports are stored.
     *
     * @parameter expression="${jmeter.reports.dir}"
     *          default-value="${basedir}/target/jmeter-report"
     */
    private File reportDir;

    /**
     * Whether or not to generate reports after measurement.
     * 
     * @parameter default-value="true"
     */
    private boolean enableReports;

    /**
     * Custom Xslt which is used to create the report.
     *
     * @parameter
     */
    private File reportXslt;

    /**
     * Absolute path to JMeter default properties file.
     * The default properties file is part of a JMeter installation and sets basic properties needed for running JMeter.
     *
     * @parameter expression="${jmeter.properties}"
     *          default-value="${basedir}/src/test/jmeter/jmeter.properties"
     * @required
     */
    private File jmeterDefaultPropertiesFile;

    /**
     * Absolute path to JMeter custom (test dependent) properties file.
     *
     * @parameter
     */
    private File jmeterCustomPropertiesFile;

    /**
     * JMeter Properties that override those given in jmeterProps
     * 
     * @parameter
     */
    @SuppressWarnings("rawtypes")
	private Map jmeterUserProperties;

    /**
     * Use remote JMeter installation to run tests
     *
     * @parameter default-value=false
     */
    private boolean remote;

    /**
     * Sets whether ErrorScanner should ignore failures in JMeter result file.
     *
     * @parameter expression="${jmeter.ignore.failure}" default-value=false
     */
    private boolean jmeterIgnoreFailure;
    
    /**
     * Sets whether ErrorScanner should ignore errors in JMeter result file.
     *
     * @parameter expression="${jmeter.ignore.error}" default-value=false
     */
    private boolean jmeterIgnoreError;


    /**
     * Absolute path to File to log results to.
     *
     * @parameter
     */
    private String resultFileName;

    /**
     * @parameter expression="${project}"
     * @required
     */
    @SuppressWarnings("unused")
	private MavenProject mavenProject;

    /**
     * @parameter expression="${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
     * @required
     */
    private ArtifactResolver artifactResolver;

    /**
     * @parameter expression="${localRepository}"
     */
    private ArtifactRepository localRepository;


    /**
     * Postfix to add to report file.
     *
     * @parameter default-value="-report.html"
     */
    private String reportPostfix;

    private File workDir;
    private List<File> temporaryPropertyFiles = new ArrayList<File>();
    private File jmeterLog;
    private DateFormat fmt = new SimpleDateFormat("yyMMdd");

    private static final String JMETER_ARTIFACT_GROUPID = "org.apache.jmeter";
    private static final String JMETER_ARTIFACT_ID = "jmeter";
    private static final String JMETER_ARTIFACT_VERSION = "2.4";

    /**
     * Run all JMeter tests.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        initSystemProps();
        try {
            List<String> jmeterTestFiles = new ArrayList<String>();
            List<String> results = new ArrayList<String>();
            if(jmeterTestFile != null) {
                jmeterTestFiles.add(jmeterTestFile.getName());
            } else {
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(srcDir);
            scanner.setIncludes(includes == null ? new String[] { "**/*.jmx" } : includes.toArray(new String[] {}));
            if (excludes != null) {
                scanner.setExcludes(excludes.toArray(new String[] {}));
            }
            scanner.scan();
                jmeterTestFiles.addAll(Arrays.asList(scanner.getIncludedFiles()));
            }

            for (String file : jmeterTestFiles) {
                results.add(executeTest(new File(srcDir, file)));
            }
            if (this.enableReports) {
                makeReport(results);
            }
            checkForErrors(results);
        } finally {
            for(File temporaryPropertyFile : temporaryPropertyFiles) {
                temporaryPropertyFile.delete();
            }
        }
    }

    private void makeReport(List<String> results) throws MojoExecutionException {
        try {
            ReportTransformer transformer;
            transformer = new ReportTransformer(getXslt());
            getLog().info("Building JMeter Report.");
            for (String resultFile : results) {
                final String outputFile = toOutputFileName(resultFile);                
                getLog().info("transforming: " + resultFile + " to " + outputFile);
                transformer.transform(resultFile, outputFile);                
            }
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("Error writing report file jmeter file.", e);
        } catch (TransformerException e) {
            throw new MojoExecutionException("Error transforming jmeter results", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Error copying resources to jmeter results", e);
        }
    }

    /**
     * returns the fileName with the configured reportPostfix
     * 
     * @param fileName the String to modify
     * @return modified fileName
     */
    private String toOutputFileName(String fileName) {
        if (fileName.endsWith(".xml")) {               
            return fileName.replace(".xml", this.reportPostfix);
        } else {
            return fileName + this.reportPostfix;
        }
    }

    private InputStream getXslt() throws IOException {
        if (this.reportXslt == null) {
            //if we are using the default report, also copy the images out.
            IOUtils.copy(Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("reports/collapse.jpg"), new FileOutputStream(this.reportDir.getPath() + File.separator + "collapse.jpg"));
            IOUtils.copy(Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("reports/expand.jpg"), new FileOutputStream(this.reportDir.getPath() + File.separator + "expand.jpg"));
            return Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/jmeter-results-detail-report_21.xsl");
        } else {
            return new FileInputStream(this.reportXslt);
        }
    }

    private void checkForErrors(List<String> results) throws MojoExecutionException, MojoFailureException {
        ErrorScanner scanner = new ErrorScanner(this.jmeterIgnoreError, this.jmeterIgnoreFailure);
        try {
            for (String file : results) {
               if (scanner.scanForProblems(new File(file))) {
                   getLog().warn("There were test errors.  See the jmeter logs for details");                  
               }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Can't read log file", e);
        }
    }

    private void initSystemProps() throws MojoExecutionException {
        workDir = new File("target" + File.separator + "jmeter");
        workDir.mkdirs();
        createTemporaryProperties();
        resolveJmeterArtifact();

        jmeterLog = new File(workDir, "jmeter.log");
        try {
            System.setProperty("log_file", jmeterLog.getCanonicalPath());
        } catch (IOException e) {
            throw new MojoExecutionException("Can't get canonical path for log file", e);
        }
    }

    /**
     * Resolve JMeter artifact, set necessary System Property.
     *
     * This mess is necessary because JMeter must load this info from a file.
     * Loading resources from classpath won't work.
     * @throws org.apache.maven.plugin.MojoExecutionException exception
     */
    private void resolveJmeterArtifact() throws MojoExecutionException {
        try {
            //VersionRange needed for Maven 2.x compatibility.
            VersionRange versionRange = VersionRange.createFromVersionSpec(JMETER_ARTIFACT_VERSION);
            Artifact jmeterArtifact = new DefaultArtifact(JMETER_ARTIFACT_GROUPID, JMETER_ARTIFACT_ID, versionRange, "", "jar", "", new DefaultArtifactHandler());
            List remoteArtifactRepositories = mavenProject.getRemoteArtifactRepositories();
            artifactResolver.resolve(jmeterArtifact, remoteArtifactRepositories, localRepository);

            System.setProperty("search_paths", jmeterArtifact.getFile().getAbsolutePath());

        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException("Could not resolve JMeter artifact. ", e);
        } catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException("Could not find JMeter artifact. ", e);
        } catch (InvalidVersionSpecificationException e) {
            throw new MojoExecutionException("Invalid version declaration. ", e);
        }
    }

    /**
     * Create temporary property files and set necessary System Properties.
     *
     * This mess is necessary because JMeter must load this info from a file.
     * Loading resources from classpath won't work.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          Exception
     */
    @SuppressWarnings("unchecked")
    private void createTemporaryProperties() throws MojoExecutionException {
        String jmeterTargetDir = File.separator + "target" + File.separator + "jmeter" + File.separator;
        File saveServiceProps = new File(workDir, "saveservice.properties");
        System.setProperty("saveservice_properties", jmeterTargetDir + saveServiceProps.getName());
        temporaryPropertyFiles.add(saveServiceProps);
        File upgradeProps = new File(workDir, "upgrade.properties");
        System.setProperty("upgrade_properties", jmeterTargetDir + upgradeProps.getName());
        temporaryPropertyFiles.add(upgradeProps);

        for (File propertyFile : temporaryPropertyFiles) {
            try {
                FileWriter out = new FileWriter(propertyFile);
                IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFile.getName()), out);
                out.flush();
                out.close();
            } catch (IOException e) {
                throw new MojoExecutionException("Could not create temporary property file "+propertyFile.getName()+" in directory "+jmeterTargetDir, e);
            }
        }
    }

    /**
     * Executes a single JMeter test by building up a list of command line
     * parameters to pass to JMeter.start().
     * 
     * @param test JMeter test XML
     * @return the report file names.
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          Exception
     */
    private String executeTest(File test) throws MojoExecutionException {

        try {
            getLog().info("Executing test: " + test.getCanonicalPath());

            if(resultFileName == null) {
                resultFileName = reportDir.toString() + File.separator + test.getName().substring(0, test.getName().lastIndexOf(".")) + "-" + fmt.format(new Date()) + ".xml";
            }
            //delete file if it already exists
            new File(resultFileName).delete();
            List<String> argsTmp = Arrays.asList("-n", "-t",
                    test.getCanonicalPath(), 
                    "-l", resultFileName,
                    "-p", jmeterDefaultPropertiesFile.toString(),
                    "-d", System.getProperty("user.dir"));

            List<String> args = new ArrayList<String>();
            args.addAll(argsTmp);
            args.addAll(getUserProperties());
            if(jmeterCustomPropertiesFile != null) {
                args.add("-q");
                args.add(jmeterCustomPropertiesFile.toString());
            }
            if (remote) {
                args.add("-r");
            }
            if(getLog().isDebugEnabled()) {
                getLog().debug("JMeter is called with the following command line arguments: " + args.toString());
            }
            // This mess is necessary because JMeter likes to use System.exit.
            // We need to trap the exit call.
            SecurityManager oldManager = System.getSecurityManager();
            System.setSecurityManager(new SecurityManager() {
                @Override
                public void checkExit(int status) {
                    throw new ExitException(status);
                }

                @Override
                public void checkPermission(Permission perm, Object context) {
                }

                @Override
                public void checkPermission(Permission perm) {
                }
            });
            UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                public void uncaughtException(Thread t, Throwable e) {
                    if (e instanceof ExitException && ((ExitException) e).getCode() == 0) {
                        return; // Ignore
                    }
                    getLog().error("Error in thread " + t.getName());
                }
            });
            try {
                // This mess is necessary because the only way to know when
                // JMeter
                // is done is to wait for its test end message!
                new JMeter().start(args.toArray(new String[] {}));
                BufferedReader in = new BufferedReader(new FileReader(jmeterLog));
                while (!checkForEndOfTest(in)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            } catch (ExitException e) {
                if (e.getCode() != 0) {
                    throw new MojoExecutionException("Test failed", e);
                }
            } finally {
                System.setSecurityManager(oldManager);
                Thread.setDefaultUncaughtExceptionHandler(oldHandler);
            }

            return resultFileName;
        } catch (IOException e) {
            throw new MojoExecutionException("Can't execute test", e);
        }
    }

    private boolean checkForEndOfTest(BufferedReader in) throws MojoExecutionException {
        boolean testEnded = false;
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("Test has ended")) {
                    testEnded = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Can't read log file", e);
        }
        return testEnded;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<String> getUserProperties() {
        ArrayList<String> propsList = new ArrayList<String>();
        if (jmeterUserProperties == null) {
            return propsList;
        }
        Set<String> keySet = (Set<String>) jmeterUserProperties.keySet();

        for (String key : keySet) {

            propsList.add("-J");
            propsList.add(key + "=" + jmeterUserProperties.get(key));
        }

        return propsList;
    }

    private static class ExitException extends SecurityException {
        private static final long serialVersionUID = 5544099211927987521L;

        public int _rc;

        public ExitException(int rc) {
            super(Integer.toString(rc));
            _rc = rc;
        }

        public int getCode() {
            return _rc;
        }
    }

}
