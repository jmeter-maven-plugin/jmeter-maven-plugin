<#ftl/>
<#-- @ftlvariable name="jmeterGraphs" type="java.util.Map<java.lang.String, java.io.File>" -->
<#-- @ftlvariable name="jmeterResults" type="com.lazerycode.jmeter.parser.JmeterResults" -->
<#-- @ftlvariable name="summaryFilename" type="java.lang.String" -->
<#macro tableResult counters>
    <table cellspacing="0">
        <tr>
            <th class="first">Samplers</th>
            <th>Throughput</th>
            <th>Average</th>
            <th>Min</th>
            <th>50%</th>
            <th>90%</th>
            <th>95%</th>
            <th>99%</th>
            <th>99.9%</th>
            <th>Max</th>
            <th>Success</th>
            <th class="last">Error</th>
        </tr>
        <#assign count=1 />
        <#list counters?keys as sampler>
            <#assign counter=counters[sampler]/>
            <tr class="${zebra(count)}">
                <td class="first">${sampler}</td>
                <td>${counter.getThroughput()} q/s</td>
                <td>${counter.getAverage()} ms</td>
                <td>${counter.getMin()} ms</td>
                <td>${counter.getPercentile(50)} ms</td>
                <td>${counter.getPercentile(90)} ms</td>
                <td>${counter.getPercentile(95)} ms</td>
                <td>${counter.getPercentile(99)} ms</td>
                <td>${counter.getPercentile(99.9)} ms</td>
                <td>${counter.getMax()} ms</td>
                <td>${counter.getSuccessCount()} (${counter.getSuccessCountPercent()}%)</td>
                <td class="last">${counter.getErrorCount()} (${counter.getErrorCountPercent()}%)</td>
            </tr>
            <#assign count=count + 1 />
        </#list>
    </table>
</#macro>
<#function zebra index>
  <#if (index % 2) == 0>
    <#return "" />
  <#else>
    <#return "odd-row" />
  </#if>
</#function>
<html>
<head>
  <title>JMeter Summary ${summaryFilename}</title>
  <style type="text/css">
    .center {
        text-align: center;
    }

    html, body, div, span, object, iframe,
    h1, h2, h3, h4, h5, h6, p, blockquote, pre,
    abbr, address, cite, code,
    del, dfn, em, img, ins, kbd, q, samp,
    small, strong, sub, sup, var,
    b, i,
    dl, dt, dd, ol, ul, li,
    fieldset, form, label, legend,
    table, caption, tbody, tfoot, thead, tr, th, td {
            margin:0;
            padding:0;
            border:0;
            outline:0;
            font-size:100%;
            vertical-align:baseline;
            background:transparent;
    }

    body {
            margin:0;
            padding:0;
            font:12px/15px "Helvetica Neue",Arial, Helvetica, sans-serif;
            color: #555;
            background:#f5f5f5;
    }
    h1 {
            font:22px/25px "Helvetica Neue",Arial, Helvetica, sans-serif;
            padding-top:8px;
    }
    h3 {
            font:17px/20px "Helvetica Neue",Arial, Helvetica, sans-serif;
            padding-top:18px;
    }

    /*
    Pretty Table Styling
    CSS Tricks also has a nice writeup: http://css-tricks.com/feature-table-design/
    */

    table {
        overflow:hidden;
        border:1px solid #d3d3d3;
        background:#fefefe;
        width:95%;
        margin:10px auto 0;
        -moz-border-radius:5px; /* FF1+ */
        -webkit-border-radius:5px; /* Saf3-4 */
        border-radius:5px;
        -moz-box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
        -webkit-box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
    }

    table.summary {
        width:60%;
    }

    th, td {padding:8px 8px 8px; text-align:center; }

    th {padding-top:8px; text-shadow: 1px 1px 1px #fff; background:#e8eaeb;}

    td {border-top:1px solid #e0e0e0; border-right:1px solid #e0e0e0;}

    tr.odd-row td {background:#f6f6f6;}

    td.first, th.first {text-align:left}

    td.last {border-right:none;}

    /*
    Background gradients are completely unnecessary but a neat effect.
    */

    td {
        background: -moz-linear-gradient(100% 25% 90deg, #fefefe, #f9f9f9);
        background: -webkit-gradient(linear, 0% 0%, 0% 25%, from(#f9f9f9), to(#fefefe));
    }

    tr.odd-row td {
        background: -moz-linear-gradient(100% 25% 90deg, #f6f6f6, #f1f1f1);
        background: -webkit-gradient(linear, 0% 0%, 0% 25%, from(#f1f1f1), to(#f6f6f6));
    }

    th {
        background: -moz-linear-gradient(100% 20% 90deg, #e8eaeb, #ededed);
        background: -webkit-gradient(linear, 0% 0%, 0% 20%, from(#ededed), to(#e8eaeb));
    }

    /*
    I know this is annoying, but we need additional styling so webkit will recognize rounded corners on background elements.
    Nice write up of this issue: http://www.onenaught.com/posts/266/css-inner-elements-breaking-border-radius

    And, since we've applied the background colors to td/th element because of IE, Gecko browsers also need it.
    */

    tr:first-child th.first {
        -moz-border-radius-topleft:5px;
        -webkit-border-top-left-radius:5px; /* Saf3-4 */
    }

    tr:first-child th.last {
        -moz-border-radius-topright:5px;
        -webkit-border-top-right-radius:5px; /* Saf3-4 */
    }

    tr:last-child td.first {
        -moz-border-radius-bottomleft:5px;
        -webkit-border-bottom-left-radius:5px; /* Saf3-4 */
    }

    tr:last-child td.last {
        -moz-border-radius-bottomright:5px;
        -webkit-border-bottom-right-radius:5px; /* Saf3-4 */
    }
  </style>
</head>
    <body>
        <h1 class="center">JMeter Summary ${summaryFilename}</h1>
        <#if !jmeterGraphs?keys?has_content>
        <p>Results file is empty.</p>
        <#else>
        <div class="center">
            <table class="summary" cellspacing="0">
                <tr>
                    <th class="first">Start Date</th>
                    <th>End Date</th>
                    <th class="last">Duration</th>
                </tr>
                <tr class="${zebra(1)}">
                    <td class="first">${jmeterResults.getStartTimestamp()?number_to_datetime}</td>
                    <td>${jmeterResults.getEndTimestamp()?number_to_datetime}</td>
                    <td class="last">${jmeterResults.getDurationTestFormat()}</td>
                </tr>
            </table>
            <@tableResult counters={"GLOBAL":jmeterResults.getGlobalCounter()} />
            <@tableResult counters=jmeterResults.getUrisCounter() />
            <#list jmeterGraphs?keys as key>
                <#assign file=jmeterGraphs(key)/>
                <h3>${key}</h3>
                <img src="resources/${file.getName()}" />
            </#list>
        </div>
        </#if>
    </body>
</html>