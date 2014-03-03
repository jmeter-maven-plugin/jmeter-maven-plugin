/**
 *
 */
package com.lazerycode.jmeter.configuration;

/**
 *
 */
public enum CMDRunnerCommandLineArguments {

    // MANDATORY
    TOOL("tool"),
    GENERATE_PNG("generate-png"),               // <file>   generate PNG file containing graph
    GENERATE_CSV("generate-csv"),               // <file>   generate CSV file containing graph data
    INPUT_JTL("input-jtl"),                     // <file>  load data from specified JTL file
    PLUGIN_TYPE("plugin-type"),                 // <class>   which type of graph use for results generation

    // OPTIONS
    WIDTH("width"),                             // <pixels>    for PNG only - width of the image, default is 800
    HEIGHT("height"),                           // <pixels>   for PNG only - height of the image, default is 600
    GRANULATION("granulation"),                 // <ms>  granulation time for samples
    RELATIVE_TIMES("relative-times"),           // <yes/no>   use relative X axis times, no will set absolute times
    AGGREGATE_ROWS("aggregate-rows"),           // <yes/no>   aggregate all rows into one
    PAINT_GRADIENT("paint-gradient"),           // <yes/no>   paint gradient background
    PAINT_ZEROING("paint-zeroing"),             // <yes/no>    paint zeroing lines
    PAINT_MARKERS("paint-markers"),             // <yes/no>    paint markers on data points (since 1.1.3)
    PREVENT_OUTLIERS("prevent-outliers"),       // <yes/no> prevent outliers on distribution graph
    LIMIT_ROWS("limit-rows"),                   // <num of points>    limit number of points in row
    FORCE_Y("force-y"),                         // <limit>   force Y axis limit
    HIDE_LOWS_COUNTS("hide-low-counts"),        // <limit>   hide points with sample count below limit
    SUCCESS_FILTER("success-filter"),           // <true/false>   filter samples by success flag (since 0.5.6), possible values are true, false, if not set no filtering on success flag will occur
    INCLUDE_LABELS("include-labels"),           // <labels list>  include in report only samples with specified labels, comma-separated
    EXCLUDE_LABELS("exclude-labels"),           // <labels list>  exclude from report samples with specified labels, comma-separated
    AUTO_SCALE("auto-scale"),                   // <yes/no>   enable/disable auto-scale multipliers for perfmon/composite graph
    LINE_WEIGHT("line-weight"),                 // <num of pixels>   line thickness for graph rows
    EXTRACTOR_REGEXPS("extractor-regexps");     // <regExps list>  list of keyRegExp and valRegExp pairs separated with {;}, only used by PageDataExtractorOverTime

    private final String commandLineArgument;

    CMDRunnerCommandLineArguments(String commandLineArgument) {
        this.commandLineArgument = commandLineArgument;
    }

    public String getCommandLineArgument() {
        return new StringBuilder("--").append(commandLineArgument).toString();
    }

}
