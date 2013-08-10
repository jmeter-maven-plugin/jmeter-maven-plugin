## Modifying Properties

* [Using Your Own Properties Files](#1)
* [Adding Additional Properties To &lt;propertiesJMeter&gt;](#2)
* [Adding Additional Properties To &lt;propertiesSaveService&gt;](#3)
* [Adding Additional Properties To &lt;propertiesUpgrade&gt;](#4)
* [Adding Additional Properties To &lt;propertiesUser&gt;](#5)
* [Adding Additional Properties To &lt;propertiesGlobal&gt;](#6)
* [Adding Additional Properties To &lt;propertiesSystem&gt;](#7)
* [Setting &lt;propertiesReplacedByCustomFiles&gt;](#8)
* [Specifying A &lt;customPropertiesFile&gt;](#9)

***

<a id="1" />
##Using Your Own Properties Files

The easiest way to configure JMeter with this plugin is to supply your own properties files.  When it starts up the plugin will scan the **${project.base.directory}/src/test/jmeter** directory for the following files:

* jmeter.properties
* saveservice.properties
* upgrade.properties
* system.properties
* user.properties
* global.properties

<a id="2" />
##Adding Additional Properties To &lt;propertiesJMeter&gt;

It is possible to set properties that configure the main JMeter library.  To set those properties you will need to specify each property in your **pom.xml** in the config element **&lt;propertiesJmeter&gt;** (The example below shows a property called **log_level.jmeter** being set).

Each property specified is merged into the JMeter properties file **jmeter.properties**, it will overwrite any identical properties within the file.

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
						<propertiesJmeter>
							<log_level.jmeter>DEBUG</log_level.jmeter>
						</propertiesJmeter>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

<a id="3" />
##Adding Additional Properties To &lt;propertiesSaveService&gt;

It is possible to set properties that configure the Saveservice of the main JMeter library.  To set those properties you will need to specify each property in your **pom.xml** in the config element **&lt;propertiesSaveservice&gt;** (The example below shows a property called **HTTPSampler2** being set).

Each property specified is merged into the JMeter properties file **saveservice.properties**, it will overwrite any identical properties within the file.

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
						<propertiesSaveService>
							<HTTPSampler2>org.apache.jmeter.protocol.http.sampler.HTTPSampler2</HTTPSampler2>
						</propertiesSaveService>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

<a id="4" />
##Adding Additional Properties To &lt;propertiesUpgrade&gt;

It is possible to set properties that are used in the oldValue to newValue upgrade mapping of the main JMeter library.  To set those properties you will need to specify each property in your **pom.xml** in the config element **&lt;propertiesUpgrade&gt;**. (The example below shows a property called **my.old.ClassName** being set).

Each property specified is merged into the JMeter properties file **upgrade.properties**, it will overwrite any identical properties within the file.

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
						<propertiesUpgrade>
							<my.old.ClassName>my.new.ClassName</my.old.ClassName>
						</propertiesUpgrade>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

<a id="5" />
##Adding Additional Properties To &lt;propertiesUser&gt;

JMeter user properties are properties supplied to JMeter that can be used in JMeter tests.  To set user properties you will need to specify each property in your **pom.xml** in the config element **&lt;propertiesUser&gt;** (The example below shows a property called **threads** and a propery called **testIterations** being set).

Each property specified is merged into the JMeter properties file **user.properties**, it will overwrite any identical properties within the file.

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
						<propertiesUser>
							<threads>10</threads>
							<testIterations>5</testIterations>
						</propertiesUser>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

<a id="6" />
##Adding Additional Properties To &lt;propertiesGlobal&gt;

Global properties are properties that are sent to the remote machines.
To set those properties you will need to specify each property in your **pom.xml** in the config element **&lt;propertiesGlobal**&gt; (The example below shows a property called **threads** and a property called **testIterations** being set).

Each property specified is merged into the JMeter properties file **global.properties**, it will overwrite any identical properties within the file.

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
						<propertiesGlobal>
							<threads>10</threads>
							<testIterations>5</testIterations>
						</propertiesGlobal>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

<a id="7" />
##Adding Additional Properties To &lt;propertiesSystem&gt;

JMeter can set system properties, these are global environment properties accessible by all applications running in the same JVM (They are not accessible outside the JVM).  To set system properties you will need to specify each property in your **pom.xml** in the config element **&lt;propertiesSystem&gt;** (The example below shows a property called **my.system.property** being set).

Each property specified is merged into the JMeter properties file system.properties, it will overwrite any identical properties within the file.

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
						<propertiesSystem>
							<my.system.property>my.system.property.value</my.system.property>
						</propertiesSystem>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

<a id="8" />
##Setting &lt;propertiesReplacedByCustomFiles&gt;

By default all properties specified in the settings above will be merged with any existing properties.  If you want them to be replaced instead you can do this by setting **&lt;propertiesReplacedByCustomFiles&gt;** to true (it is false by default).  Please think **very carefully** before doing this, the reason we merge properties by default is to ensure that all properties are merged into the latest valid versions of the properties files supplied with JMeter.  If you overwrite the properties files but are missing a property that is required by JMeter things will most likely break. 

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
						<propertiesReplacedByCustomFiles>${basedir}true</propertiesReplacedByCustomFiles>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

<a id="9" />
##Specifying A &lt;customPropertiesFile&gt;

This will allow you to set an absolute path to JMeter custom (test dependent) properties file.  This is the equivalent of setting " --addprop my.properties" on the command line.

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
						<customPropertiesFile>/user/home/myuser/myCustom.properties</customPropertiesFile>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+