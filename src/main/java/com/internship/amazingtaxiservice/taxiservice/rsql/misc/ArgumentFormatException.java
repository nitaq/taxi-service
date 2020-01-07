package com.internship.amazingtaxiservice.taxiservice.rsql.misc;


public class ArgumentFormatException extends RuntimeException {

    private static final long serialVersionUID = 521849874508654920L;

    private final String argument;
    private final Class<?> propertyType;


    public ArgumentFormatException(String argument, Class<?> propertyType) {
        super("Cannot cast '" + argument + "' to type " + propertyType);
        this.argument = argument;
        this.propertyType = propertyType;
    }


    public String getArgument() {
        return argument;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }
}