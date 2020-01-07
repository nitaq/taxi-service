package com.internship.amazingtaxiservice.taxiservice.rsql.misc;

import java.util.List;


public interface ArgumentParser {

    <T> T parse(String argument, Class<T> type)
        throws ArgumentFormatException, IllegalArgumentException;


    <T> List<T> parse(List<String> arguments, Class<T> type)
        throws ArgumentFormatException, IllegalArgumentException;
}
