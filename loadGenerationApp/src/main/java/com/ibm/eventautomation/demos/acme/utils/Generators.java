package com.ibm.eventautomation.demos.acme.utils;

import java.util.List;
import java.util.Random;

public class Generators {

    private final static Random RNG = new Random();


    public static <T> T randomItem(List<T> list) {
        return list.get(RNG.nextInt(list.size()));
    }

    public static double randomPrice(double min, double max) {
        double randomValue = min + (max - min) * RNG.nextDouble();
        return Math.round(randomValue * 100.0) / 100.0;
    }

    public static boolean shouldDo(double ratio) {
        return RNG.nextDouble() < ratio;
    }

    public static int randomInt(int min, int max) {
        return RNG.nextInt(min, max + 1);
    }
}
