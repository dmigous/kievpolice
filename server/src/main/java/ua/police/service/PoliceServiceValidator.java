package ua.police.service;

import java.util.Arrays;

/**
 * Created by julia
 */
public class PoliceServiceValidator {

    public static void notNullValidation(Object... args)
    {
        if(Arrays.stream(args).anyMatch(arg -> arg == null))
            throw new IllegalArgumentException("Some arguments are null.");
    }
}
