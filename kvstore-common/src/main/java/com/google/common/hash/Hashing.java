/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.common.hash;

/**
 * copied from guava, only remain consistentHash method, delete others
 *
 * <p>A comparison of the various hash functions can be found
 * <a href="http://goo.gl/jS7HH">here</a>.
 *
 * @author Kevin Bourrillion
 * @author Dimitris Andreou
 * @author Kurt Alfred Kluever
 * @since 11.0
 */
public final class Hashing {
    /**
     * Assigns to {@code input} a "bucket" in the range {@code [0, buckets)}, in a uniform
     * manner that minimizes the need for remapping as {@code buckets} grows. That is,
     * {@code consistentHash(h, n)} equals:
     * <p>
     * <ul>
     * <li>{@code n - 1}, with approximate probability {@code 1/n}
     * <li>{@code consistentHash(h, n - 1)}, otherwise (probability {@code 1 - 1/n})
     * </ul>
     * <p>
     * <p>See the <a href="http://en.wikipedia.org/wiki/Consistent_hashing">wikipedia
     * article on consistent hashing</a> for more information.
     */
    public static int consistentHash(long input, int buckets) {
        LinearCongruentialGenerator generator = new LinearCongruentialGenerator(input);
        int candidate = 0;
        int next;

        // Jump from bucket to bucket until we go out of range
        while (true) {
            next = (int) ((candidate + 1) / generator.nextDouble());
            if (next >= 0 && next < buckets) {
                candidate = next;
            } else {
                return candidate;
            }
        }
    }

    /**
     * Linear CongruentialGenerator to use for consistent hashing.
     * See http://en.wikipedia.org/wiki/Linear_congruential_generator
     */
    private static final class LinearCongruentialGenerator {
        private long state;

        public LinearCongruentialGenerator(long seed) {
            this.state = seed;
        }

        public double nextDouble() {
            state = 2862933555777941757L * state + 1;
            return ((double) ((int) (state >>> 33) + 1)) / (0x1.0p31);
        }
    }
}
