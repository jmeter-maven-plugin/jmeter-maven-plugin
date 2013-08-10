#Remote Start And Stop Of Servers Via &lt;remoteConfig&gt;

Setting the **&lt;startServersBeforeTests&gt;** option will result in a --runremote command being send to JMeter which will start up any remote servers you have defined in your _jmeter.properties_ when your first test starts.

Setting the **&lt;stopServersAfterTests&gt;** option will result in a --remoteexit command being send to JMeter which will shut down all remote servers defined in _jmeter.properties_ after your last test has been run.

**&lt;startServersBeforeTests&gt;** and **&lt;stopServersAfterTests&gt;** can be used independantly so that it is possible to use another process to start and stop servers if required.

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
						<remoteConfiguration>
							<startServersBeforeTests>true</startServersBeforeTests>
							<stopServersAfterTests>true</stopServersAfterTests>
						</remoteConfiguration>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

You can configure the plugin to perform a remote start and stop for each individual test by setting the **&lt;startAndStopServersForEachTest&gt;** variable to true.  If you set this along with **&lt;startServersBeforeTests&gt;** and **&lt;stopServersAfterTests&gt;** the **&lt;startServersBeforeTests&gt;** and **&lt;stopServersAfterTests&gt;** settings will be ignored.

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
						<remoteConfiguration>
							<startAndStopServersForEachTest>false</startAndStopServersForEachTest>
						</remoteConfiguration>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+

Instead of starting all remote servers, you can specify which ones to start by using the **&lt;serverList&gt;** option, this will accept a comma separated list of servers for JMeter to start (these must be defined in your _jmeter.properties_).

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
						<remoteConfiguration>
                               <startServersBeforeTests>true</startServersBeforeTests>
							<serverList>server1,server2</serverList>
							<stopServersAfterTests>true</stopServersAfterTests>
						</remoteConfiguration>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+