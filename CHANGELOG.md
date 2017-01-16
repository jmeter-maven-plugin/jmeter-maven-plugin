# Changelog

##Next Version (Release Date TBC) Release Notes

##Version 2.1.0 Release Notes

* **JMeter version 3.1.0 support added.** 
* **Issue #201** - Don't copy any artifact dependencies for jmeterExtensions and junitLibraries if `<downloadExtensionDependencies>` and `<downloadLibraryDependencies>` are set to false. 
* You now have an option to try and download all transitive dependencies for the JMeter artifacts (This is disabled by default since the transitive dependency tree for JMeter 3.1 is currently broken). You can toggle this using `<downloadJMeterDependencies>`.
* **Issue #186** - Transitive dependencies for all files you add via jmeterExtensions and junitLibraries are now all downloaded by default.  You can toggle this using `<downloadExtensionDependencies>` and `downloadLibraryDependencies`. 
* **Issue #204** - We know support JMeter 3.1 by default (See https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/wiki/Specifying-JMeter-Version if you want to change the version of JMeter used)
* **Issue #203** - The list of files read by the CheckResultsMojo is now stored on disk in a config.json file in the ${project.build.directory}.  This does mean you could theoretically change the contents of this file before running the results phase to specify the files you want to check.
* **Issue #195** - The results file format is now stored in a config.json file in the ${project.build.directory}.
* **Issue #190** - The list of results files was previously null since in memory data is not shared between MOJO's. They are now stored in a config.json file in the ${project.build.directory}. 

##Version 2.0.3 Release Notes

* **Issue #182** - We are now correctly picking up all .jmx files if no test files are specified

##Version 2.0.2 Release Notes

* **Issue #184** - use .replace() instead of .replaceAll() since File.separator is not a valid regex on windows machines.

##Version 2.0.1 Release Notes

* **Issue #183** - `<resultsFileFormat>` is no longer ignored.

##Version 2.0.0 Release Notes

* **JMeter version 3.0.0 support added.** 
* **BREAKING CHANGE:** Location of the .jmx files is now set by using the configuration option `<testFilesDirectory>`:

		<configuration>
    		<testFilesDirectory>my/jmx/file/directory</testFilesDirectory>
		</configuration>

* **Issue #170** - add a shutdown hook to destroy the JMeter process if the parent thread is terminated.
* **Issue #165** - Enabled the plugin to continue execution if there are no tests or properties, you will now get a message telling you a test run was skipped if not test files are available.
* **Merge pull request #167** (Thanks [Pascal Treilhes](https://github.com/treilhes)) - You can now use your current maven proxy with the `<useMavenProxy>true</useMavenProxy>` tag, only the first active proxy will be used. `<useMavenProxy>` have a lower priority than `<proxyConfiguration>`.
* You can now specify where logs are saved using the `<logsDirectory>` tag.  This will default to `${project.build.directory}/jmeter/logs`

		<logsDirectory>${project.build.directory}/jmeter/logs</logsDirectory>
		
* **Issue #162** - Reserved properties are now stripped out of custom properties files.
* **Issue #136** - The default JMeter user.properties is always used as a base file that is then modified now.
* **BREAKING CHANGE:** `<workDir>` has been renamed to `<jmeterDirectory>`
* **BREAKING CHANGE: Issue #131/Issue#160** Dependency resolution is now performed using the Eclipse Aether libraries.  
	To add artifacts to the lib/ext directory you now need to use:

		<jmeterExtensions>
			<artifact>kg.apc:jmeter-plugins:pom:1.3.1</artifact>
		</jmeterExtensions>
		
	To add libraries to the lib/junit folder you now need to use:
		
		<junitLibraries>
			<artifact>com.lazerycode.junit:junit-test:1.0.0</artifact>
		</junitLibraries>

	The format for artifact elements is: \<groupId\>:\<artifactId\>[:\<extension\>[:\<classifier\>]]:\<version\>
	
	You should no longer need to make these artifacts dependencies of the plugin, we will just got and get the artifact and its associated dependencies.  This should make configuration much easier and less verbose.
	
	This will also allow you to choose the version of JMeter that you use with the plugin, bear in mind that different versions of JMeter depend on a different list of artifacts, you will have to change `<jmeterVersion>` and `<jmeterArtifacts>`:
	
		<jmeterVersion>3.0</jmeterVersion>
	
		<jmeterArtifacts>
			<artifact>com.lazerycode.junit:junit-test:1.0.0</artifact>
		</jmeterArtifacts>
		
	The `<jmeterArtifacts>` list is hard coded by default, if you specify your own version of JMeter you will also have to provide your own list of dependencies. 	
	

* **Issue #117** - You now have the option to scan results files for failed requests and successful requests.  Both are disabled by default since they do have a performance hit.

		<scanResultsForFailedRequests>true</scanResultsForFailedRequests>
		<scanResultsForSuccessfulRequests>true</scanResultsForSuccessfulRequests>

* **Issue #125** - Console output is now clearer when talking about failures.
* **Merge pull request #111** (Thanks [Gordon](https://github.com/gordon00)) - Add support for opening a test file in jmeter:gui
* **BREAKING CHANGE: Merge pull request #161** (Thanks [Irek Pastusiak](https://github.com/automatictester)) - Add support for multiple custom properties files 

		<customPropertiesFiles>
			<file>someFile</file>
			<file>someOtherFile</file>
		</customPropertiesFiles>

##Version 1.10.1 Release Notes

* **JMeter version 2.13 support added by hacking about with dependencies.**
* **Issue #108** - Send log output to debug if suppressJMeterOutput is true to prevent buffer overflow
* **Merge pull request #110** (Thanks [Nanne](https://github.com/nbaars)) - Additional logging.
* **Merge pull request #120** (Thanks [Irek Pastusiak](https://github.com/automatictester)) - Make Properties file directory configurable.

##Version 1.10.0 Release Notes

* **JMeter version 2.11 support added.**
* **Merge pull request #100** (Thanks [Sascha Theves](https://github.com/sath1982)) - Add support for `<junitPlugins>` element to let you copy libraries into the  `jmeter/lib/junit` directory.

		<configuration>
		    <junitPlugins>
		        <plugin>
		            <groupId>my.group</groupId>
		            <artifactId>my.artifact</artifactId>
		        </plugin>
		    </junitPlugins>
		</configuration>

* **Issue #103** - system.properties and user.properties from JMeter used if custom ones are not specified.

##Version 1.9.1 Release Notes

* **Merge pull request #99** (Thanks [Peter Murray](https://github.com/peter-murray)) - Set Java runtime

##Version 1.9.0 Release Notes

* **JMeter version 2.10 support added.**
* **Issue #56** - Now using a ProcessBuilder to isolate the JVM JMeter runs in.
* **Issue #63** - Fixed remote configuration documentation errors.
* **Issue #64** - Remote execution seems to be stopping before agent stops running the tests.
* **Issue #66** - Jmeter lib directory contains additional jars.
* **Merge pull request #70** (Thanks [Erik G. H. Meade](https://github.com/eghm)) - Add requiresDirectInvocation true to JMeterMojo.
* **Issue #71** - Fixed documentation errors.
* **Issue #72** - Remove the maven site from the plugin.
* **Merge pull request #73** (Thanks [Zmicier Zaleznicenka](https://github.com/dzzh)) - Added missed dependency causing file not found / error in NonGUIDriver error.
* **Issue #73** - Add missing dependency for ApacheJMeter-native.
* **Issue #75** - Allow empty propertiesUser properties.
* **Issue #80** - Integration Tests Failing With Maven 2.
* **Issue #82** - Allow users to specify the resultsDir:

		<configuration>
		    <resultsDirectory>/tmp/jmeter</resultsDirectory>
		</configuration>
		
* **Merge pull request #85** to fix Issues #77 and #84
* **Issue #77** - JMeter plugins artifacts now placed in lib/ext directory (Thanks [Michael Lex](https://github.com/mlex)).  You can specify which artifacts are JMeter plugins using the new jmeterPlugins configuration setting:
* **Merge pull request #78** (Thanks [Mike Patel](https://github.com/patelm5)) - Changes to allow system / global jmeter properties to be sent to remote clients.
* **Issue #84** - Correctly place explicit dependencies in the /lib directory (Thanks [Michael Lex](https://github.com/mlex)).

		<configuration>
		    <jmeterPlugins>
		        <plugin>
		            <groupId>my.group</groupId>
		            <artifactId>my.artifact</artifactId>
		        </plugin>
		    </jmeterPlugins>
		</configuration>

* Added the ability to configure the JMeter JVM:

		<configuration>
		    <jMeterProcessJVMSettings>
		        <xms>1024</xms>
		        <xmx>1024</xmx>
		        <arguments>
		            <argument>-Xprof</argument>
		            <argument>-Xfuture</argument>
		        </arguments>
		    </jMeterProcessJVMSettings>
		</configuration>


* **Issue #88** - ApacheJMeter_mongodb dependency is not in POM
* **Issue #89** - Add support for advanced log config.  If you add a "logkit.xml" into the `<testFilesDirectory>` it will now be copied into the /bin folder.  If one does not exist the default one supplied with JMeter will be used instead.  If you don't want to call your advanced log config file "logkit.xml", you can specify the filename using:

		<configuration>
		    <logConfigFilename>myFile.xml</logConfigFilename>
		</configuration>

##Version 1.8.1 Release Notes

* **Issue #62** - Fixed bug where testResultsTimestamp was ignored.

##Version 1.8.0 Release Notes

* **JMeter version 2.9 support added.**
* **Issue #58/Issue #59** - Add dependencies with custom function to /lib/ext folder.
* **Issue #61** - Added skipTests ability.
* Removed jmx file sorting code as it was not sorting files into a determinalistic order.
* Removed checks for **`<error>true</error>`** and **`<failure>true</failure>`** in .jtl files, these elements do not occur in JMeter 2.9.
* Added ability to choose whether to Append or Prepend date to filename, new configuration option added: **`<appendResultsTimestamp>false</appendResultsTimestamp>`**
* Set default timestamp to an ISO_8601 timestamp.  The formatter now used is a JodaTime DateTimeFormatter (See [http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html](http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html))
* Added the ability to override the root log level, new configuration option added: **`<overrideRootLogLevel>DEBUG</overrideRootLogLevel>`**.  Valid log levels are **FATAL_ERROR**, **ERROR**, **WARN**, **INFO** and **DEBUG**.
* Failure scanner refactored to use a Boyer-Moore algorithm to increase performance on large results files.
* Added the ability to set the result file format using **`<resultsFileFormat>CSV</resultsFileFormat>`**.  Valid options are **XML** | **CSV**, it will default to XML.
* Modified remote configuration settings, configuration options are now self explanitory:

		<remoteConfig>
			<startAndStopServersForEachTest>false</startAndStopServersForEachTest>
			<startServersBeforeTests>true</startServersBeforeTests>
			<stopServersAfterTests>true</stopServersAfterTests>
			<serverList>server1,server2</serverList>
		</remoteConfig>

##Version 1.7.0 Release Notes

* **JMeter version 2.8 support added (Issue #57).**
* **Issue #54** - Build directory ignored - Fix applied

##Version 1.6.1 Release Notes

* **Issue #54** - Build directory ignored - Fix applied

## Version 1.6.0 Release Notes

* **JMeter version 2.7 support added.**

##Version 1.5.1 Release Notes

* jmeter-maven-plugin can no longer generate reports. Use jmeter-analysis-maven-plugin instead.
* clear system property "org.apache.commons.logging.Log" at the end of the test run

## Version 1.5.0 Release Notes

* **Issue #41** - Removed deprecated log scanning code.
* **Issue #42** - Reporting disabled by default (Takes a long time to parse the logs, so you need to explicitly turn it on now).
* **Issue #45** - Fixed intermittant test end detection bug (Major refactor using thread detection to detect test end).
* **Issue #49** - Added jmeter:gui goal so that you can start the GUI using maven.
* Reporting is now marked as deprecated in preparation for bringing in a new reporting module.

## Version 1.4.1 Release Notes

* **Issue #34** - plugin now uses the jmeter.exit.check.pause set in the final jmeter.properties used by the plugin and is no longer hard coded to 2000.
* **Issue #37** - plugin now uses a listener instead of scanning log files to work out when test has completed.

## Version 1.4.0 Release Notes

* **JMeter version 2.6 support added.**
* New group.id -> `com.lazerycode.jmeter`
* New artifact.id -> `jmeter-maven-plugin`
* Depends on the official Apache JMeter 2.6 artifacts (now available at [http://repo1.maven.org/maven2/org/apache/jmeter/](http://repo1.maven.org/maven2/org/apache/jmeter/))
* Incompatible configuration element changes
* Custom property file locations cannnot be configured directly any more, they have to be put into `src/test/jmeter/`

## Renamed/moved properties

* includes -> testFilesIncluded
* excludes -> testFilesExcluded
* srcDir -> testFilesDirectory
* enableReports -> reportConfig->enableReports
* reportDir -> reportConfig->outputDirectory
* reportPostfix -> reportConfig->postfix
* reportXslt -> reportConfig->xsltFile
* jmeterIgnoreFailure -> ignoreResultFailures
* jmeterIgnoreError -> ignoreResultErrors
* jmeterUserProperties -> propertiesUser

* jmeterDefaultPropertiesFile -> propertiesJMeter

## New properties

### Used to configure Proxy

* proxyConfiguration->host
* proxyConfiguration->port
* proxyConfiguration->username
* proxyConfiguration->password
* proxyConfiguration->nonProxyHosts

### Used to configure handling of remote testing

* remoteConfiguration->start
* remoteConfiguration->stop
* remoteConfiguration->startAll
* remoteConfiguration->startAndStopOnce
   
### Other

* suppressJMeterOutput(Default: true) - suppress JMeter output to standard out.
* testResultsTimestamp(Default: true) - enableReports/disable timestamping of the results filename(s).