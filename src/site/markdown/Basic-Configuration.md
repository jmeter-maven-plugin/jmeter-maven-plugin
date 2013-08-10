# Basic Configuration

Add the plugin to the build section of your **pom.xml** (Best practice is to define the version of the Maven JMeter plugin that you want to use in either your **pom.xml** or a parent **pom.xml**):

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

By default the plugin uses the default properties files supplied with JMeter.  If you want to override any of these files you simply need to create a replacement in **${project.base.directory}/src/test/jmeter**.

The following properties files will be used if they are found in **${project.base.directory}/src/test/jmeter**:

* jmeter.properties
* saveservice.properties
* upgrade.properties
* system.properties
* user.properties
* global.properties

The plugin will automatically run all of the JMX test files held in the **${project.base.directory}/src/test/jmeter** directory.
To run the tests open a terminal/command prompt and then type:

	cd ${project.base.directory}
	mvn verify

There are a series of configuration options available, see the [advanced configuration](https://github.com/Ronnie76er/jmeter-maven-plugin/wiki/Advanced-Configuration) section.