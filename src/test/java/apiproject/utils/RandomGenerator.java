package apiproject.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomGenerator {

    public int getIndex(int index) {
        return ThreadLocalRandom.current().nextInt(0, index);
    }
}

