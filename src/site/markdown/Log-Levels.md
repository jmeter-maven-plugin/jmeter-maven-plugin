#Log Levels

* [How To &lt;overrideRootLogLevel&gt;](#1)
* [Individual Log Levels](#2)

***

<a id="1"/>
##How To &lt;overrideRootLogLevel&gt;

You can specify a root log level for debug purposes.

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
						<overrideRootLogLevel>debug</overrideRootLogLevel>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

Setting the root log level will always override any settings for individual category log levels (as a result if the **&lt;overrideRootLogLevel&gt;** is set all category log levels set elsewhere will be ignored.

<a id="2"/>
##Individual Log Levels

You can override individual log levels by setting them in your jmeter.properties file.  If you want to override them in the POM have a look at Adding [Additional Properties To **&lt;propertiesJMeter&gt;**](Modifying-Properties#wiki-1).