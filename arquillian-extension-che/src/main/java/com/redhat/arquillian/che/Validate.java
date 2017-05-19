package com.redhat.arquillian.che;

import java.util.Arrays;

public class Validate {

    /**
     * Checks is the given string is null or empty
     */
    public static boolean isEmpty(String str){
        return str == null || str.trim().isEmpty();
    }

    /**
     * Checks is the given strings are all null or empty
     */
    public static boolean areAllEmpty(String... str){
        return Arrays.stream(str).allMatch(s -> isEmpty(s));
    }

    /**
     * Checks is the given string is not null nor empty
     */
    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }
}
