# Selecting Tests To Run

* [Running All Tests](#1)
* [Specifying &lt;testFilesIncluded&gt;](#2)
* [Specifying &lt;testFilesIncluded&gt; Using Regex](#3)
* [Specifying &lt;testFilesExcluded&gt;](#4)
* [Specifying &lt;testFilesExcluded&gt; Using Regex](#5)
* [Specifying The &lt;testFilesDirectory&gt;](#6)

***

<a id="1"/>
##Running All Tests

To run all tests held in the **${project.base.directory}/src/test/jmeter** you just need to run the phase you have assigned to the execution phase.

In the example below the execution phase has been set to verify:

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
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

So to run all the tests type:

	mvn verify

<a id="2"/>
##Specifying &lt;testFilesIncluded&gt;

You can explicitly specify which tests in the **${project.base.directory}/src/test/jmeter** should be run by using the &lt;jMeterTestFiles&gt; tag:

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
						<testFilesIncluded>
							<jMeterTestFile>test1.jmx</jMeterTestFile>
							<jMeterTestFile>test2.jmx</jMeterTestFile>
						</testFilesIncluded>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

This time when you type **mvn verify** only **test1.jmx** and **test2.jmx** will be run.

<a id="3"/>
##Specifying &lt;testFilesIncluded&gt; Using Regex

You can also use a regex to specify which tests to include, below is a simple example showing how to include all tests starting with the text **foo**:

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
						<testFilesIncluded>
							<jMeterTestFile>foo*.jmx</jMeterTestFile>
						</testFilesIncluded>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+


<a id="4"/>
##Specifying &lt;testFilesExcluded&gt;

Rather than specifying which tests should be run, you could alternatively specify which tests in the **${project.base.directory}/src/test/jmeter** you do not wish to run by using the &lt;excludeJMeterTestFiles&gt; tag:

	+---+
	<project>
		[...]
		<build>
			<plugins>
				<plugin>
					<groupId>com.lazerycode.jmeter</groupId>
					<artifactId>jmeter-maven-plugin</artifactId>
					<version>1.8.1</version>
					<configuration>
						<testFilesExcluded>
							<excludeJMeterTestFile>test3.jmx</excludeJMeterTestFile>
							<excludeJMeterTestFile>test4.jmx</excludeJMeterTestFile>
						</testFilesExcluded>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

This time when you type **mvn verify** all tests in the tests in the **${project.base.directory}/src/test/jmeter** apart from **test3.jmx** and **test4.jmx** will be run.

<a id="5"/>
##Specifying &lt;testFilesExcluded&gt; Using Regex

You can also use a regex to specify which tests to exclude, below is a simple example showing how to include all tests ending with the text **bar**:

	+---+
	<project>
		[...]
		<build>
			<plugins>
				<plugin>
					<groupId>com.lazerycode.jmeter</groupId>
					<artifactId>jmeter-maven-plugin</artifactId>
					<version>1.8.1</version>
					<configuration>
						<testFilesExcluded>
							<excludeJMeterTestFile>*bar.jmx</excludeJMeterTestFile>
						</testFilesExcluded>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

<a id="6"/>
##Specifying The &lt;testFilesDirectory&gt;

You can specify the directory where the test files are located in your file system (by default the plugin will assume they are in **${project.base.directory}/src/test/jmeter**)

	+---+
	<project>
		[...]
		<build>
			<plugins>
				<plugin>
					<groupId>com.lazerycode.jmeter</groupId>
					<artifactId>jmeter-maven-plugin</artifactId>
					<version>1.8.1</version>
					<configuration>
						<testFilesDirectory>/scratch/testfiles/</testFilesDirectory>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+