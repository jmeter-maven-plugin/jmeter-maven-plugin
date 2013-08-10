#Test Results File Format

* [Disabling The &lt;testResultsTimestamp&gt;](#1)
* [Enabling &lt;appendResultsTimestamp&gt;](#2)
* [Setting The &lt;resultsFileNameDateFormat&gt;](#3)
* [Choosing The &lt;resultsFileFormat&gt;](#4)

***

<a id="1" />
##Disabling The &lt;testResultsTimestamp&gt;

By default this plugin will add a timestamp to each results file that it generates.  If you do not want a timestamp added you can disable this behaviour by setting the **&lt;testResultsTimestamp&gt;** configuration setting to false.

    +---+
	<project>
		[...]
		<build>
			<plugins>
				<plugin>
					<groupId>com.lazerycode.jmeter</groupId>
					<artifactId>jmeter-maven-plugin</artifactId>
					<version>1.8.1</version>
					<executions>
						<execution>
							<id>jmeter-tests</id>
							<phase>verify</phase>
							<goals>
								<goal>jmeter</goal>
							</goals>
						</execution>
					</executions>
					<configuration>
						<testResultsTimestamp>false</testResultsTimestamp>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

<a id="2" />
##Enabling &lt;appendResultsTimestamp&gt;

When **&lt;testResultsTimestamp&gt;** is set to true the default positioning of the timestamp is at the start of the results filename.  You can set the **&lt;appendResultsTimestamp&gt** to true to make the plugin add the timestamp to the end of the results filename.

    +---+
	<project>
		[...]
		<build>
			<plugins>
				<plugin>
					<groupId>com.lazerycode.jmeter</groupId>
					<artifactId>jmeter-maven-plugin</artifactId>
					<version>1.8.1</version>
					<executions>
						<execution>
							<id>jmeter-tests</id>
							<phase>verify</phase>
							<goals>
								<goal>jmeter</goal>
							</goals>
						</execution>
					</executions>
					<configuration>
						<appendResultsTimestamp>true</appendResultsTimestamp>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

<a id="3" />
##Setting The &lt;resultsFileNameDateFormat&gt;

The default format for the timestamp added to results filenames created by the plugin is a basic ISO_8601 date format (YYYMMDD).  You can modify the format of the timestamp by setting the **&lt;resultsFileNameDateFormat&gt;** configuration setting, we use a JodaTime DateTimeFormatter (See [http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html](http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html))

    +---+
	<project>
		[...]
		<build>
			<plugins>
				<plugin>
					<groupId>com.lazerycode.jmeter</groupId>
					<artifactId>jmeter-maven-plugin</artifactId>
					<version>1.8.1</version>
					<executions>
						<execution>
							<id>jmeter-tests</id>
							<phase>verify</phase>
							<goals>
								<goal>jmeter</goal>
							</goals>
						</execution>
					</executions>
					<configuration>
						<resultsFileNameDateFormat >MMMM, yyyy</resultsFileNameDateFormat >
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

<a id="4" />
##Choosing The &lt;resultsFileFormat&gt;

JMeter is capable of creating .jtl (an XML format) test results and csv test results.  By default this plugin uses the .jtl format so that it can scan the result file for failures.  You can switch thos to csv format if you would prefer, but the plugin is currently unable to parse .csv files for failures and .csv files will not work with the JMeter Analysis Maven Plugin.

    +---+
	<project>
		[...]
		<build>
			<plugins>
				<plugin>
					<groupId>com.lazerycode.jmeter</groupId>
					<artifactId>jmeter-maven-plugin</artifactId>
					<version>1.8.1</version>
					<executions>
						<execution>
							<id>jmeter-tests</id>
							<phase>verify</phase>
							<goals>
								<goal>jmeter</goal>
							</goals>
						</execution>
					</executions>
					<configuration>
						<resultsFileFormat>csv</resultsFileFormat>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+