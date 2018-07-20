package com.github.wuminorb.kvstore.util;

import com.google.common.hash.Hashing;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class HashingTest {

    @Test
    public void testBucketDescriptive() throws Exception {
        int bucket = 10;
        double count[] = new double[bucket];

        for (long j = 0; j < 10000000; j++) {
            count[Hashing.consistentHash(j, bucket)]++;
        }
        System.out.println(Arrays.toString(count));
        System.out.println(new DescriptiveStatistics(count).getStandardDeviation());
    }
}