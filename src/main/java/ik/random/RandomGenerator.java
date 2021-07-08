package ik.random;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomGenerator {

    /**
     * Generates random alphanumeric string of defined length.
     *
     * @param length of string to return
     * @return random alphanumeric string
     */
    public String randomAlphanumeric(int length) {
        int numbersMinAsciCode = 48; // character '0'
        int numbersMaxAsciCode = 57; // character '9'
        int lettersCapitalMinAsciCode = 65; // character 'A'
        int lettersCapitalMaxAsciCode = 90; // character 'Z'
        int lettersLowercaseMinAsciCode = 97; // character 'a'
        int lettersLowercaseMaxAsciCode = 122; // character 'z'

        Random random = ThreadLocalRandom.current();
            return random
                    .ints(numbersMinAsciCode, lettersLowercaseMaxAsciCode + 1)
                    .filter(i -> (i <= numbersMaxAsciCode || i >= lettersCapitalMinAsciCode)
                            && (i <= lettersCapitalMaxAsciCode || i >= lettersLowercaseMinAsciCode))
                    .limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
    }
}
