#Setting the &lt;proxyConfig&gt;

If you configure proxy settings to be used by JMeter, the proxy settings will be used to run **every** test.

Setting a proxy host is mandatory, if you do not set a proxy host the rest of the proxy configuration will be ignored.  If you do not set a proxy port it will always default to port 80.

**&lt;username&gt;** and **&lt;password&gt;** are optional settings that do not have to be set.

You can also set an optional **&lt;hostExclusions&lt;** element to notify JMeter of hosts that should not be proxied, this is a regular expression based setting.

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
						<proxyConfig>
							<host>10.10.10.53</host>
							<port>80</port>
							<username>jimbob</username>
							<password>correct horse battery staple</password>
							<hostExclusions>localhost|*.lazerycode.com</hostExclusions>
						</proxyConfig>
					</configuration>
				</plugin>
			</plugins>
		</build>
		[...]
	</project>
	+---+