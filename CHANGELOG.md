# Changelog

##Next Version (Release Date TBC) Release Notes

* Issue #125 - Console output is now clearer when talking about failures.
* Merge pull request #111 (Thanks [Gordon](https://github.com/gordon00) - Add support for opening a test file in jmeter:gui
* **BREAKING CHANGE:** Merge pull request #161 (Thanks [Irek P](https://github.com/automatictester)) - Add support for multiple custom properties files 

		<customPropertiesFiles>
			<customerPropertiesFile>someFile</customerPropertiesFile>
			<customerPropertiesFile>someOtherFile</customerPropertiesFile>
		</customPropertiesFiles>

##Version 1.10.1 Release Notes

* **JMeter version 2.13 support added by hacking about with dependencies.**
* Issue #108 - Send log output to debug if suppressJMeterOutput is true to prevent buffer overflow
* Merge pull request #110 (Thanks [Nanne](https://github.com/nbaars)) - Additional logging.
* Merge pull request #120 (Thanks [Irek P](https://github.com/automatictester)) - Make Properties file directory configurable.

##Version 1.10.0 Release Notes

* **JMeter version 2.11 support added.**
* Merge pull request #100 (Thanks [Sascha Theves](https://github.com/sath1982)) - Add support for `<junitPlugins>` element to let you copy libraries into the  `jmeter/lib/junit` directory.

		<configuration>
		    <junitPlugins>
		        <plugin>
		            <groupId>my.group</groupId>
		            <artifactId>my.artifact</artifactId>
		        </plugin>
		    </junitPlugins>
		</configuration>

* Issue #103 - system.properties and user.properties from JMeter used if custom ones are not specified.

##Version 1.9.1 Release Notes

* Merge pull request #99 (Thanks [Peter Murray](https://github.com/peter-murray)) - Set Java runtime

##Version 1.9.0 Release Notes

* **JMeter version 2.10 support added.**
* Issue #56 - Now using a ProcessBuilder to isolate the JVM JMeter runs in.
* Issue #63 - Fixed remote configuration documentation errors.
* Issue #64 - Remote execution seems to be stopping before agent stops running the tests.
* Issue #66 - Jmeter lib directory contains additional jars.
* Merge pull request #70 (Thanks [Erik G. H. Meade](https://github.com/eghm)) - Add requiresDirectInvocation true to JMeterMojo.
* Issue #71 - Fixed documentation errors.
* Issue #72 - Remove the maven site from the plugin.
* Merge pull request #73 (Thanks [Zmicier Zaleznicenka](https://github.com/dzzh)) - Added missed dependency causing file not found / error in NonGUIDriver error.
* Issue #73 - Add missing dependency for ApacheJMeter-native.
* Issue #75 - Allow empty propertiesUser properties.
* Issue #80 - Integration Tests Failing With Maven 2.
* Issue #82 - Allow users to specify the resultsDir:

		<configuration>
		    <resultsDirectory>/tmp/jmeter</resultsDirectory>
		</configuration>
		
* Merge pull request #85 to fix Issues #77 and #84
* Issue #77 - JMeter plugins artifacts now placed in lib/ext directory (Thanks [Michael Lex](https://github.com/mlex)).  You can specify which artifacts are JMeter plugins using the new jmeterPlugins configuration setting:
* Merge pull request #78 (Thanks [Mike Patel](https://github.com/patelm5)) - Changes to allow system / global jmeter properties to be sent to remote clients.
* Issue #84 - Correctly place explicit dependencies in the /lib directory (Thanks [Michael Lex](https://github.com/mlex)).

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


* Issue #88 - ApacheJMeter_mongodb dependency is not in POM
* Issue #89 - Add support for advanced log config.  If you add a "logkit.xml" into the `<testFilesDirectory>` it will now be copied into the /bin folder.  If one does not exist the default one supplied with JMeter will be used instead.  If you don't want to call your advanced log config file "logkit.xml", you can specify the filename using:

		<configuration>
		    <logConfigFilename>myFile.xml</logConfigFilename>
		</configuration>

##Version 1.8.1 Release Notes

* Issue #62 - Fixed bug where testResultsTimestamp was ignored.

##Version 1.8.0 Release Notes

* **JMeter version 2.9 support added.**
* Issue #58/Issue #59 - Add dependencies with custom function to /lib/ext folder.
* Issue #61 - Added skipTests ability.
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
* Issue #54 - Build directory ignored - Fix applied

##Version 1.6.1 Release Notes

* Issue #54 - Build directory ignored - Fix applied

## Version 1.6.0 Release Notes

* **JMeter version 2.7 support added.**

##Version 1.5.1 Release Notes

* jmeter-maven-plugin can no longer generate reports. Use jmeter-analysis-maven-plugin instead.
* clear system property "org.apache.commons.logging.Log" at the end of the test run

## Version 1.5.0 Release Notes

* Issue #41 - Removed deprecated log scanning code.
* Issue #42 - Reporting disabled by default (Takes a long time to parse the logs, so you need to explicitly turn it on now).
* Issue #45 - Fixed intermittant test end detection bug (Major refactor using thread detection to detect test end).
* Issue #49 - Added jmeter:gui goal so that you can start the GUI using maven.
* Reporting is now marked as deprecated in preparation for bringing in a new reporting module.

## Version 1.4.1 Release Notes

* Issue #34 - plugin now uses the jmeter.exit.check.pause set in the final jmeter.properties used by the plugin and is no longer hard coded to 2000.
* Issue #37 - plugin now uses a listener instead of scanning log files to work out when test has completed.

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