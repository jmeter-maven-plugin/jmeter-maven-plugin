package com.lazerycode.jmeter;

import java.util.Comparator;
import java.util.List;

import static org.apache.tools.ant.types.selectors.SelectorUtils.matchPath;

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
     *
     * @param s1 String
     * @param s2 String
     * @return int
     */
    @Override
    public int compare(String s1, String s2) {
        final int i1 = includes.indexOf(s1);
        final int i2 = includes.indexOf(s2);
        return (i1 < i2 ? -1 : (i1 == i2 ? s1.compareTo(s2) : 1));
    }

}
