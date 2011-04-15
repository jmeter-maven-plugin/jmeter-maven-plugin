package org.apache.jmeter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.plugin.MojoFailureException;

/**
 * Handles checking the jmeter xml logs for errors and failures.
 * 
 * @author Jon Roberts
 */
public class ErrorScanner {

    private static final String PAT_ERROR = "<error>true</error>";
    private static final String PAT_FAILURE = "<failure>true</failure>";

    private boolean ignoreErrors;

    private boolean ignoreFailures;

    /**
     * 
     * @param ignoreErrors
     *            if an error is found with this scanner it will throw an
     *            exception instead of returning true;
     * @param ignoreFailures
     *            if a failure is found with this scanner it will throw an
     *            exception instead of returning true;
     */
    public ErrorScanner(boolean ignoreErrors, boolean ignoreFailures) {
        this.ignoreErrors = ignoreErrors;
        this.ignoreFailures = ignoreFailures;
    }

    public boolean scanForProblems(File file) throws MojoFailureException, IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line;            
            while ((line = in.readLine()) != null) {
                if (line.contains(PAT_ERROR)) {
                    if (this.ignoreErrors) {
                        return true;
                    } else {
                        throw new MojoFailureException("There were test errors.  See the jmeter logs for details.");
                    }
                }
                if (line.contains(PAT_FAILURE)) {
                    if (this.ignoreFailures) {
                        return true;
                    } else {
                        throw new MojoFailureException("There were test failures.  See the jmeter logs for details.");
                    }
                }
            }
        } finally {
            in.close();
        }
        return false;
    }
}
