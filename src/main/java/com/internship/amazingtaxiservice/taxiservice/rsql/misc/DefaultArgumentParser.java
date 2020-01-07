package com.internship.amazingtaxiservice.taxiservice.rsql.misc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DefaultArgumentParser implements ArgumentParser {

    private static final Logger LOG = Logger.getLogger(DefaultArgumentParser.class.getName());

    private static final String DATE_PATTERN = "yyyy-MM-dd"; //ISO 8601
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"; //ISO 8601

    public <T> T parse(String argument, Class<T> type)
        throws ArgumentFormatException, IllegalArgumentException {

        LOG.log(Level.INFO, "Parsing argument ''{0}'' as type {1}, thread {2}", new Object[]{argument, type.getSimpleName(), Thread.currentThread().getName()});

        // Nullable object
        if (argument == null || "null".equals(argument.trim().toLowerCase())) {
            return (T) null;
        }

        // common types
        try {
            if (type.equals(String.class)) return (T) argument;
            if (type.equals(Integer.class) || type.equals(int.class)) return (T) Integer.valueOf(argument);
            if (type.equals(Boolean.class) || type.equals(boolean.class)) return (T) Boolean.valueOf(argument);
            if (type.isEnum()) return (T) Enum.valueOf((Class<Enum>) type, argument);
            if (type.equals(Float.class) || type.equals(float.class)) return (T) Float.valueOf(argument);
            if (type.equals(Double.class) || type.equals(double.class)) return (T) Double.valueOf(argument);
            if (type.equals(Long.class) || type.equals(long.class)) return (T) Long.valueOf(argument);
            if (type.equals(BigDecimal.class)) return (T) new BigDecimal(argument);
        } catch (IllegalArgumentException ex) {
            throw new ArgumentFormatException(argument, type);
        }

        // date
        if (type.equals(Date.class)) {
            return (T) parseDate(argument, type);
        }

        // try to parse via valueOf(String s) method
        try {
            LOG.log(Level.INFO, "Trying to get and invoke valueOf(String s) method on {0}", type);
            Method method = type.getMethod("valueOf", String.class);
            return (T) method.invoke(type, argument);
        } catch (InvocationTargetException ex) {
            throw new ArgumentFormatException(argument, type);
        } catch (ReflectiveOperationException ex) {
            LOG.log(Level.WARNING, "{0} does not have method valueOf(String s) or method is inaccessible", type);
            throw new IllegalArgumentException("Cannot parse argument type " + type);
        }
    }

    private <T> Date parseDate(String argument, Class<T> type) {
        try {
            return new SimpleDateFormat(DATE_TIME_PATTERN).parse(argument);
        } catch (ParseException ex) {
            LOG.log(Level.INFO, "Not a date time format, lets try with date format.");
        }
        try {
            return new SimpleDateFormat(DATE_PATTERN).parse(argument);
        } catch (ParseException ex1) {
            throw new ArgumentFormatException(argument, type);
        }
    }


    public <T> List<T> parse(List<String> arguments, Class<T> type)
        throws ArgumentFormatException, IllegalArgumentException {
        List<T> castedArguments = new ArrayList<T>(arguments.size());
        for (String argument : arguments) {
            castedArguments.add(this.parse(argument, type));
        }
        return castedArguments;
    }
}