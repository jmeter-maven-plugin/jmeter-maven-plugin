package org.apache.jmeter;

import java.util.Comparator;
import java.util.List;

import org.apache.tools.ant.types.selectors.SelectorUtils;

/**
 * Compare filenames based on their order in a list of includes (using default
 * ant include pattern).
 */
public class IncludesComparator implements Comparator<String> {
    private final List<String> includes;

    public IncludesComparator(List<String> includes) {
        super();
        this.includes = includes;
    }

    /**
     * Compares two filenames based on the index of the first include pattern
     * that matches them in the given list. If the index is equal, the actual
     * filename lexicographical order is used to ensure a total order.
     */
    public int compare(String s1, String s2) {
        final int i1 = includesIndex(s1);
        final int i2 = includesIndex(s2);
        return (i1 < i2 ? -1 : (i1 == i2 ? s1.compareTo(s2) : 1));
    }

    private int includesIndex(String filename) {
        if ((includes == null) || (filename == null)) {
            return 0;
        }
        int index = 0;
        for (final String include : includes) {
            if (matches(include, filename.replace('\\', '/'))) {
                return index;
            }
            index++;
        }
        return 0;
    }

    private boolean matches(String pattern, String filename) {
        return SelectorUtils.matchPath(pattern, filename, true);
    }
}
