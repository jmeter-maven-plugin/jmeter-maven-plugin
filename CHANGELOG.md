# Changelog

## Version 3.0.0-SNAPSHOT Release Notes (IN PROGRESS)

### Enhancements ###

### Bug Fixes ###

* Issue [329](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/329) **BREAKING CHANGE** Fix error where default configure goal was always called resulting in config overwrite.  You will now need to always explicitly add the configure execution block to your POM
```
<execution>
    <id>configuration</id>
    <goals>
        <goal>configure</goal>
    </goals>
</execution>
``` 

## Version 2.9.0 Release Notes (2nd April 2019)

### Enhancements ###

* Issue [319](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/319) Support Apache JMeter 5.1.1.
* Set a default of `<generateReports>false</generateReports>`.  Not everybody wants reports and they take time to generate so they should always be disabled by default.  It will also help reduce confusion around the results file format being ignored if you don't explicitly disable report generation.
* **BREAKING CHANGE:** Set minimum Maven version to **3.5.2** which includes some fixes to package and assemble for JDK 9.

### Bug Fixes ###

* Issue [325](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/325) Fix error where we were always logging scanned file type as .jtl.
* Issue [323](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/323) Fix error where existing reports dir would cause build to fail.
* Issue [318](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/318) Fix forceHeadless override logic.
* Issue [321](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/321) Fix bug where server log name was malformed if server host was not explicitly set
* Issue [320](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/320) RemoteConfiguration toString printed server list under the tag start.
* Issue [321](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/321) Minor logging bug.  Was previously logging that JMeter GUI was being shut down when in fact JMeter server was being shut down.

## Version 2.8.6 Release Notes (20th february 2019)

### Enhancements ###

* Issue [311](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/311) Support Apache JMeter 5.1.
* Improved test coverage and moved from Hamcrest to AssertJ.

## Version 2.8.5 Release Notes (29th january 2019)

### Enhancements ###

* Issue [304](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/304) Run headless when possible.

### Bug Fixes ###

* Issue [305](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/305) Response message containing "false" is considered a failure when parsed by ResultScanner. **Thanks at https://github.com/martymcflywa for his contribution !**.

## Version 2.8.4 (INVALID)

## Version 2.8.3 Release Notes (15th january 2019)

### Enhancements ###

* Issue [300](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/299) Simplify default setup when using 3rd party plugins relying on old JMeter version.
* New tutorial [Shift left your performance tests with JMeter and Maven](https://www.ubik-ingenierie.com/blog/shift-left-performance-tests-jmeter-maven/) available.

### Bug Fixes ###

* Issue [299](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/299) Using exclusion with '*' artifact in does not work.
* Issue [298](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/298) Drop of coverage from 77% to 32%.
* Issue [297](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/297) Random failure of integration test it/aremote-test on travis.

## Version 2.8.2 Release Notes (27th december 2018) 

### Changes ###

Starting with this version, the plugin will not add a suffix yyyyMMdd_HHmmss to the HTML report folder to make the
path a fixed one, allowing usage of HTML Publisher plugin 

* Issue [295](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/295) Remove date_time suffix from path when generating html report.
 
## Version 2.8.1 Release Notes

* Issue [265](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/265) Add ability to fail build when error rate is over a threshold, see https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/wiki/Test-Results-File-Format#9.

## Version 2.8.0 Release Notes

* Issue [285](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/285) Support Apache JMeter 5.0.
* Issue [287](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/287) Upgraded dependencies.
* Travis : Add Java 10.

## Version 2.7.0 Release Notes

* Issue [272](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/272) Support Apache JMeter 4.0.
* Issue [277](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/277) Travis : Add tests for Java 9.

## Version 2.6.0 Release Notes

* Issue [262](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/262) Improve management of non plugin related dependencies.

To add to lib folder of Apache JMeter your test dependencies you can now use:

   ```
    <testPlanLibraries>
	<artifact>org.apache.activemq:activemq-client:5.15.2</artifact>
    </testPlanLibraries>
   ```

* Issue [261](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/261) Default values should be improved.

Default values for following properties have been changed:

* generateReports=true
* scanResultsForFailedRequests=true
* scanResultsForSuccessfulRequests=true
* resultsFileFormat=csv

## Version 2.5.0 Release Notes

* Issue [94](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/94) Add the ability to start a remote server that is ready to accept connections.

   ```
    mvn jmeter:remote-server
   ```

## Version 2.5.1 Release Notes

* Issue [258](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/258) NPE when running remote-server if no jMeterProcessJVMSettings set.

* Issue [219](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/219) Unit tests fail on Windows.

## Version 2.5.0 Release Notes

* Issue [94](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/94) Add the ability to start a remote server that is ready to accept connections.

   ```
    mvn jmeter:remote-server
   ```


* Issue [256](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/256) Add an option to exclude a dependency.

   ```
    <excludedArtifacts>
	<exclusion>commons-pool2:commons-pool2</exclusion>
    </excludedArtifacts>
	```

* Issue [255](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/255) Improve javadocs of mojo so that help displays useful info.

* Issue [229](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/229) The plugin Downloads excluded dependencies of the Extension.

* Issue [216](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/216) Capturing cyclic dependencies to avoid stackoverflow.

* Issue [211](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/211) Remove duplicate versions of the same transitive dependency by selecting the most up to date.

**Thanks at https://github.com/kostd for his contribution to improving greatly dependency management !**

## Version 2.4.1 Release Notes

* Issue [252](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/252) - If generateReports is true, jmeter:gui adds invalid arguments.

* Issue [251](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/251) - Static properties in AbstractJMeterMojo and ConfigureJMeterMojo can introduce weird issues .

* Issue [250](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/250) - Plugin wrongly copies jorphan to lib/ext instead of lib.

## Version 2.4.0 Release Notes

* Issue [244](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/244) - Settings ignoreResultFailures will not work if scanResultsForFailedRequests is not set to true.

* Issue [243](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/243) - Make Result Scanning also work with results in CSV format.

* Issue [242](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/242) - customPropertiesFiles Tag Drops The Period.

## Version 2.3.0 Release Notes

* Issue [240](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/240) - JMeter version 3.3 support added.

* **Code cleanups and better logging.**

## Version 2.2.0 Release Notes

* Issue [222](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/222) - JMeter version 3.2.0 support added.

* Issue [208](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/208) - Add support for JMeter's built in report generation

* Issue [221](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/221) - Fix error `[INFO] Invalid value detected for <postTestPauseInSeconds>. Setting pause to 0...]`

* Issue [210](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/210) - When downloading transitive dependencies, optional dependencies are now not pulled down by default.  To change this toggle `<downloadOptionalDependencies>false</downloadOptionalDependencies>`

* Issue [179](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/179) - The plugin will now copy everything in the `${basedir}/src/test/conf` directory into the `jmeter/bin/` directory.  You can configure this by setting
	```
	<confFilesDirectory>${basedir}/src/test/some_conf_dir</confFilesDirectory>
	```

## Version 2.1.0 Release Notes

* **JMeter version 3.1.0 support added.**
* Issue [188](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/188) - Directory structure is now preserved.  Test results will use the relative path of the test file (with the file seperators replaced with _) as its filename.
* Add the ability to provide a list of artifacts that should be ignored when populating the JMeter directory tree:
	```
	<ignoredArtifacts>
		<artifact>org.bouncycastle:bcprov-jdk15on:1.49</artifact>
	</ignoredArtifacts>
	```
* Issue [202](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/202) - The `<ignoreResultFailures>` config setting is no longer being ignored.
* Issue [201](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/201) - Don't copy any artifact dependencies for jmeterExtensions and junitLibraries if `<downloadExtensionDependencies>` and `<downloadLibraryDependencies>` are set to false. 
* You now have an option to try and download all transitive dependencies for the JMeter artifacts (This is disabled by default since the transitive dependency tree for JMeter 3.1 is currently broken). You can toggle this using `<downloadJMeterDependencies>`.
* Issue [186](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/186) - Transitive dependencies for all files you add via jmeterExtensions and junitLibraries are now all downloaded by default.  You can toggle this using `<downloadExtensionDependencies>` and `downloadLibraryDependencies`. 
* Issue [204](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/204) - We now support JMeter 3.1 by default (See https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/wiki/Specifying-JMeter-Version if you want to change the version of JMeter used)
* Issue [203](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/203) - The list of files read by the CheckResultsMojo is now stored on disk in a config.json file in the ${project.build.directory}.  This does mean you could theoretically change the contents of this file before running the results phase to specify the files you want to check.
* Issue [195](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/195) - The results file format is now stored in a config.json file in the ${project.build.directory}.
* Issue [190](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/190) - The list of results files was previously null since in memory data is not shared between MOJO's. They are now stored in a config.json file in the ${project.build.directory}. 

## Version 2.0.3 Release Notes

* Issue [182](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/182) - We are now correctly picking up all .jmx files if no test files are specified

## Version 2.0.2 Release Notes

* Issue [184](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/184) - use .replace() instead of .replaceAll() since File.separator is not a valid regex on windows machines.

## Version 2.0.1 Release Notes

* Issue [183](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/183) - `<resultsFileFormat>` is no longer ignored.

## Version 2.0.0 Release Notes

* **JMeter version 3.0.0 support added.** 
* **BREAKING CHANGE:** Location of the .jmx files is now set by using the configuration option `<testFilesDirectory>`:

		<configuration>
    		<testFilesDirectory>my/jmx/file/directory</testFilesDirectory>
		</configuration>

* Issue [170](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/170) - add a shutdown hook to destroy the JMeter process if the parent thread is terminated.
* Issue [165](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/165) - Enabled the plugin to continue execution if there are no tests or properties, you will now get a message telling you a test run was skipped if not test files are available.
* Merge pull request [167](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/167) (Thanks [Pascal Treilhes](https://github.com/treilhes)) - You can now use your current maven proxy with the `<useMavenProxy>true</useMavenProxy>` tag, only the first active proxy will be used. `<useMavenProxy>` have a lower priority than `<proxyConfiguration>`.
* You can now specify where logs are saved using the `<logsDirectory>` tag.  This will default to `${project.build.directory}/jmeter/logs`

		<logsDirectory>${project.build.directory}/jmeter/logs</logsDirectory>
		
* Issue [162](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/162) - Reserved properties are now stripped out of custom properties files.
* Issue [136](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/136) - The default JMeter user.properties is always used as a base file that is then modified now.
* BREAKING CHANGE: `<workDir>` has been renamed to `<jmeterDirectory>`
* BREAKING CHANGE: Issue [131](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/131)/Issue[160](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/160) Dependency resolution is now performed using the Eclipse Aether libraries.  
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
	

* Issue [117](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/117) - You now have the option to scan results files for failed requests and successful requests.  Both are disabled by default since they do have a performance hit.

		<scanResultsForFailedRequests>true</scanResultsForFailedRequests>
		<scanResultsForSuccessfulRequests>true</scanResultsForSuccessfulRequests>

* Issue [125](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/125) - Console output is now clearer when talking about failures.
* Merge pull request [111](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/111) (Thanks [Gordon](https://github.com/gordon00)) - Add support for opening a test file in jmeter:gui
* BREAKING CHANGE: Merge pull request [161](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/161) (Thanks [Irek Pastusiak](https://github.com/automatictester)) - Add support for multiple custom properties files 

		<customPropertiesFiles>
			<file>someFile</file>
			<file>someOtherFile</file>
		</customPropertiesFiles>

## Version 1.10.1 Release Notes

* JMeter version 2.13 support added by hacking about with dependencies.
* Issue [108](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/108) - Send log output to debug if suppressJMeterOutput is true to prevent buffer overflow
* Merge pull request [110](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/110) (Thanks [Nanne](https://github.com/nbaars)) - Additional logging.
* Merge pull request [120](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/120) (Thanks [Irek Pastusiak](https://github.com/automatictester)) - Make Properties file directory configurable.

## Version 1.10.0 Release Notes

* **JMeter version 2.11 support added.**
* Merge pull request [100](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/100) (Thanks [Sascha Theves](https://github.com/sath1982)) - Add support for `<junitPlugins>` element to let you copy libraries into the  `jmeter/lib/junit` directory.

		<configuration>
		    <junitPlugins>
		        <plugin>
		            <groupId>my.group</groupId>
		            <artifactId>my.artifact</artifactId>
		        </plugin>
		    </junitPlugins>
		</configuration>

* Issue [103](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/103) - system.properties and user.properties from JMeter used if custom ones are not specified.

## Version 1.9.1 Release Notes

* Merge pull request [99](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/99) (Thanks [Peter Murray](https://github.com/peter-murray)) - Set Java runtime

## Version 1.9.0 Release Notes

* JMeter version 2.10 support added.
* Issue [56](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/56) - Now using a ProcessBuilder to isolate the JVM JMeter runs in.
* Issue [63](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/63) - Fixed remote configuration documentation errors.
* Issue [64](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/64) - Remote execution seems to be stopping before agent stops running the tests.
* Issue [66](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/66) - Jmeter lib directory contains additional jars.
* Merge pull request [70](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/70) (Thanks [Erik G. H. Meade](https://github.com/eghm)) - Add requiresDirectInvocation true to JMeterMojo.
* Issue [71](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/71) - Fixed documentation errors.
* Issue [72](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/72) - Remove the maven site from the plugin.
* Merge pull request [73](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/73) (Thanks [Zmicier Zaleznicenka](https://github.com/dzzh)) - Added missed dependency causing file not found / error in NonGUIDriver error.
* Issue [73](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/73) - Add missing dependency for ApacheJMeter-native.
* Issue [75](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/75) - Allow empty propertiesUser properties.
* Issue [80](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/80) - Integration Tests Failing With Maven 2.
* Issue [82](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/82) - Allow users to specify the resultsDir:

		<configuration>
		    <resultsDirectory>/tmp/jmeter</resultsDirectory>
		</configuration>
		
* Merge pull request [85](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/85) to fix Issues [77](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/77) and [84](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/84)
* Issue [77](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/77) - JMeter plugins artifacts now placed in lib/ext directory (Thanks [Michael Lex](https://github.com/mlex)).  You can specify which artifacts are JMeter plugins using the new jmeterPlugins configuration setting:
* Merge pull request [78](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/78) (Thanks [Mike Patel](https://github.com/patelm5)) - Changes to allow system / global jmeter properties to be sent to remote clients.
* Issue [84](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/84) - Correctly place explicit dependencies in the /lib directory (Thanks [Michael Lex](https://github.com/mlex)).

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


* Issue [88](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/88) - ApacheJMeter_mongodb dependency is not in POM
* Issue [89](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/89) - Add support for advanced log config.  If you add a "logkit.xml" into the `<testFilesDirectory>` it will now be copied into the /bin folder.  If one does not exist the default one supplied with JMeter will be used instead.  If you don't want to call your advanced log config file "logkit.xml", you can specify the filename using:

		<configuration>
		    <logConfigFilename>myFile.xml</logConfigFilename>
		</configuration>

## Version 1.8.1 Release Notes

* Issue [62](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/62) - Fixed bug where testResultsTimestamp was ignored.

## Version 1.8.0 Release Notes

* **JMeter version 2.9 support added.**
* Issue [58](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/58)/Issue [59](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/59) - Add dependencies with custom function to /lib/ext folder.
* Issue [61](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/61) - Added skipTests ability.
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

## Version 1.7.0 Release Notes

* JMeter version 2.8 support added (Issue [57](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/57)).
* Issue [54](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/54) - Build directory ignored - Fix applied

## Version 1.6.1 Release Notes

* Issue [54](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/54) - Build directory ignored - Fix applied

## Version 1.6.0 Release Notes

* **JMeter version 2.7 support added.**

## Version 1.5.1 Release Notes

* jmeter-maven-plugin can no longer generate reports. Use jmeter-analysis-maven-plugin instead.
* clear system property "org.apache.commons.logging.Log" at the end of the test run

## Version 1.5.0 Release Notes

* Issue [41](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/41) - Removed deprecated log scanning code.
* Issue [42](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/42) - Reporting disabled by default (Takes a long time to parse the logs, so you need to explicitly turn it on now).
* Issue [45](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/45) - Fixed intermittant test end detection bug (Major refactor using thread detection to detect test end).
* Issue [49](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/49) - Added jmeter:gui goal so that you can start the GUI using maven.
* Reporting is now marked as deprecated in preparation for bringing in a new reporting module.

## Version 1.4.1 Release Notes

* Issue [34](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/34) - plugin now uses the jmeter.exit.check.pause set in the final jmeter.properties used by the plugin and is no longer hard coded to 2000.
* Issue [37](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues/37) - plugin now uses a listener instead of scanning log files to work out when test has completed.

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
   
### Other

* suppressJMeterOutput(Default: true) - suppress JMeter output to standard out.
* testResultsTimestamp(Default: true) - enableReports/disable timestamping of the results filename(s).
