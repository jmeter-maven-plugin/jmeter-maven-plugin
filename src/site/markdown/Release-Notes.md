# Release Notes

##Next Version (Release Date TBC) Release Notes

* Issue #56 - Now using a ProcessBuilder to isolate the JVM JMeter runs in.
* Merge Pull request #70 from Erik G. H. Meade - Add requiresDirectInvocation true to JMeterMojo 
* Issue #71 - Fixed documentation errors.
* Issue #63 - Fixed remote configuration documentation errors.

##Version 1.8.1 Release Notes

* Issue #62 - Fixed bug where testResultsTimestamp was ignored

##Version 1.8.0 Release Notes

* JMeter version 2.9 support added.
* Issue #61 - Added skipTests ability.
* Issue #58/Issue #59 - Add dependencies with custom function to /lib/ext folder
* Removed jmx file sorting code as it was not sorting files into a determinalistic order.
* Removed checks for **&lt;error&gt;true&lt;/error&gt;** and **&lt;failure&gt;true&lt;/failure&gt;** in .jtl files, these elements do not occur in JMeter 2.9.
* Added ability to choose whether to Append or Prepend date to filename, new configuration option added: **&lt;appendResultsTimestamp&gt;false&lt;/appendResultsTimestamp&gt;**
* Set default timestamp to an ISO_8601 timestamp.  The formatter now used is a JodaTime DateTimeFormatter (See http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html)
* Added the ability to override the root log level, new configuration option added: **&lt;overrideRootLogLevel&gt;DEBUG&lt;/overrideRootLogLevel&gt;**.  Valid log levels are **FATAL_ERROR**, **ERROR**, **WARN**, **INFO** and **DEBUG**.
* Failure scanner refactored to use a Boyer-Moore algorithm to increase performance on large results files.
* Added the ability to set the result file format using **&lt;resultsFileFormat&gt;CSV&lt;/resultsFileFormat&gt;**.  Valid options are **XML** | **CSV**, it will default to XML.
* Modified remote configuration settings, configuration options are now self explanitory:

		<remoteConfig>
			<startAndStopServersForEachTest>false</startAndStopServersForEachTest>
			<startServersBeforeTests>true</startServersBeforeTests>
			<stopServersAfterTests>true</stopServersAfterTests>
			<serverList>server1,server2</serverList>
		</remoteConfig>

##Version 1.7.0 Release Notes

* Issue #54 - Build directory ignored - Fix applied
* Issue #57 - Jmeter version 2.8 support added.

##Version 1.6.1 Release Notes

* Issue #54 - Build directory ignored - Fix applied

## Version 1.6.0 Release Notes

* Support for JMeter 2.7

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

* New group.id -> `com.lazerycode.jmeter`
* New artifact.id -> `jmeter-maven-plugin`
* Depends on the official Apache JMeter 2.6 artifacts (now available at http://repo1.maven.org/maven2/org/apache/jmeter/)
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