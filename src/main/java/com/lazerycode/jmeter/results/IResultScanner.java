package com.lazerycode.jmeter.results;

public interface IResultScanner {

    int getSuccessCount();

    int getFailureCount();

    int getTotalCount();

}
