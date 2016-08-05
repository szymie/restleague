package org.tiwpr.szymie;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class KeysGenerator {

    public static final char[] symbols;

    static {

        String numericSymbols = IntStream.rangeClosed('0', '9').mapToObj(KeysGenerator::intCharToString).collect(joining());
        String alphabeticSymbols = IntStream.rangeClosed('a', 'z').mapToObj(KeysGenerator::intCharToString).collect(joining());

        symbols = (numericSymbols + alphabeticSymbols).toCharArray();
    }

    private static String intCharToString(int character) {
        return String.valueOf((char)character);
    }

    private final Random random;
    private final int length;

    public KeysGenerator(int length) {

        if(length <= 0) {
            throw new IllegalArgumentException("KeysGenerator: length must be greater than 0");
        }

        random = new Random();
        this.length = length;
    }

    public String nextKey() {
        return Stream.generate(this::generateNextCharacter).limit(length).collect(joining());
    }

    private String generateNextCharacter() {
        return String.valueOf(symbols[random.nextInt(symbols.length)]);
    }
}
