// Copyright (C) 1997-2017 Roger L. Deran.
//
//    This file is part of AirConcurrentMap. AirConcurrentMap
//    itself is proprietary.
//
//    This file is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 2 of the License, or
//    (at your option) any later version.
//
//    This file is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    To get a copy of the GNU General Public License,
//    see <http://www.gnu.org/licenses/>.
//
//    For dual licensing of this file, see boilerbay.com. 
//    For commercial licensing of AirConcurrentMap email 
//    support@boilerbay.com. The author email is rlderan2 at boilerbay.com.

package com.infinitydb;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import com.infinitydb.map.visitor.ThreadedMapVisitor;
import com.infinitydb.map.visitor.VisitableMap;

/**
 * The AirConcurrentMap performance tests using the defacto standard Java
 * Microbenchmarking Harness.
 * 
 * Before running these tests, install maven from https://maven.apache.org,
 * then, do this once in the root directory of the archive:
 * 
 * <pre>
 * cd jmh/maptest
 * mvn install:install-file -DgroupId=com.infinitydb \
 *  -DartifactId=airconcurrentmap -Dversion=3.1.0 -Dpackaging=jar -Dfile=../../airconcurrentmap.jar
 * </pre>
 * 
 * The above takes airconcurrentmap.java and puts it into the 'local maven
 * repository' which is actually just a cache in ~/.m2/repository where it stays
 * for all further tests (unless a newer version of airconcurrentmap.jar is
 * released, in which case you change the version number there and in pom.xml).
 *
 * Then, the following builds the test into target/benchmark.jar, so do it once
 * initially, and then again after any change to this test code:
 * 
 * <pre>
 * mvn clean install
 * </pre>
 * 
 * Then, to run a test, do for example (4g for 64-bit JVM):
 * 
 * <pre>
 * java  -Xmx4g -jar target/benchmarks.jar -f 1 -i 2 -wi 5 <testName>
 * </pre>
 * 
 * The available test parameters can be shown with:
 * 
 * <pre>
 * java -jar target/benchmark.jar -h
 * </pre>
 * 
 * <pre>
 * Result "testSummingStream":
 *   6.195 ▒(99.9%) 0.034 ops/s [Average]
 *   (min, avg, max) = (5.462, 6.195, 6.510), stdev = 0.144
 *   CI (99.9%): [6.161, 6.229] (assumes normal distribution)
 * 
 * 
 * # Run complete. Total time: 01:58:59
 * 
 * Benchmark                                                                     (mapClassName)  (mapSize)   Mode  Cnt         Score        Error  Units
 * StreamsJMHAirConcurrentMapTest.testSummingStream     com.infinitydb.map.air.AirConcurrentMap          0  thrpt  200  47669627.157 ▒ 408068.881  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream     com.infinitydb.map.air.AirConcurrentMap          1  thrpt  200  36128245.803 ▒ 219021.093  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream     com.infinitydb.map.air.AirConcurrentMap         10  thrpt  200  28819134.716 ▒ 215535.681  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream     com.infinitydb.map.air.AirConcurrentMap        100  thrpt  200   5983782.906 ▒  12171.457  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream     com.infinitydb.map.air.AirConcurrentMap       1000  thrpt  200    503450.631 ▒   2160.534  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream     com.infinitydb.map.air.AirConcurrentMap      10000  thrpt  200     51363.052 ▒    192.871  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream     com.infinitydb.map.air.AirConcurrentMap     100000  thrpt  200      8785.362 ▒    180.963  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream     com.infinitydb.map.air.AirConcurrentMap    1000000  thrpt  200       280.375 ▒      1.321  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream     com.infinitydb.map.air.AirConcurrentMap   10000000  thrpt  200        18.017 ▒      0.070  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream  java.util.concurrent.ConcurrentSkipListMap          0  thrpt  200  11715613.910 ▒  30641.713  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream  java.util.concurrent.ConcurrentSkipListMap          1  thrpt  200  10587246.514 ▒  25303.461  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream  java.util.concurrent.ConcurrentSkipListMap         10  thrpt  200    476882.573 ▒  69406.838  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream  java.util.concurrent.ConcurrentSkipListMap        100  thrpt  200     92033.529 ▒   4059.402  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream  java.util.concurrent.ConcurrentSkipListMap       1000  thrpt  200     49317.703 ▒    620.204  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream  java.util.concurrent.ConcurrentSkipListMap      10000  thrpt  200     17426.535 ▒    503.417  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream  java.util.concurrent.ConcurrentSkipListMap     100000  thrpt  200      2666.609 ▒     96.302  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream  java.util.concurrent.ConcurrentSkipListMap    1000000  thrpt  200       166.188 ▒      2.526  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream  java.util.concurrent.ConcurrentSkipListMap   10000000  thrpt  200         4.473 ▒      0.136  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream      java.util.concurrent.ConcurrentHashMap          0  thrpt  200  11748597.966 ▒  55376.295  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream      java.util.concurrent.ConcurrentHashMap          1  thrpt  200   7591252.605 ▒  58655.123  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream      java.util.concurrent.ConcurrentHashMap         10  thrpt  200    192421.408 ▒   1781.336  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream      java.util.concurrent.ConcurrentHashMap        100  thrpt  200     93215.872 ▒   1053.339  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream      java.util.concurrent.ConcurrentHashMap       1000  thrpt  200     65290.405 ▒    592.948  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream      java.util.concurrent.ConcurrentHashMap      10000  thrpt  200     15459.798 ▒     37.701  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream      java.util.concurrent.ConcurrentHashMap     100000  thrpt  200      1527.065 ▒     11.372  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream      java.util.concurrent.ConcurrentHashMap    1000000  thrpt  200        70.491 ▒      1.110  ops/s
 * StreamsJMHAirConcurrentMapTest.testSummingStream      java.util.concurrent.ConcurrentHashMap   10000000  thrpt  200         6.195 ▒      0.034  ops/s
 * </pre>
 */

@State(Scope.Benchmark)
public class StreamsJMHAirConcurrentMapTest {

    @Param({
            "com.infinitydb.map.air.AirConcurrentMap",
            "java.util.concurrent.ConcurrentSkipListMap",
            "java.util.concurrent.ConcurrentHashMap"
    })
    static String mapClassName;
    @Param({ "0", "1", "10", "100", "1000", "10000", "100000", "1000000", "10000000" })
    static long mapSize;
    static ConcurrentMap<Object, Long> map;

    @Setup(Level.Trial)
    static public void setup() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<ConcurrentMap<Object, Long>> mapClass =
                (Class<ConcurrentMap<Object, Long>>)Class.forName(mapClassName);
        map = mapClass.newInstance();
        Random random = new Random(System.nanoTime());
        System.gc();
        // Load up the Map
        for (long i = 0; i < mapSize; i++) {
            long v = random.nextLong();
            map.put(v, v);
        }
        System.gc();
    }

    @Benchmark
    public static long testSummingStream() {
        // Client code.
        return new SummingVisitor().getSum(map);
    }

    static class SummingVisitor extends ThreadedMapVisitor<Object, Long> {
        long sum = 0;

        long getSum(Map<Object, Long> map) {
            if (map instanceof VisitableMap) {
                // Use the fast AirConcurrentMap parallel scan
                ((VisitableMap)map).visit(this);
                return sum;
            } else {
                // Drop back to slower streams.
                // The code for sum() is just a reduce, giving the same
                // performance
                return map.values().stream().parallel()
                        .mapToLong(v -> ((Long)v).longValue())
                        .reduce(0L, (x, y) -> x + y);
            }
        }

        /*
         * implement MapVisitor for speed. Invoked when used with a VisitableMap
         * such as AirConcurrentMap. Similar to BiConsumer.
         */
        @Override
        public void visit(Object k, Long v) {
            sum += v.longValue();
        }

        // Implement ThreadedMapVisitor For parallelism
        @Override
        public SummingVisitor split() {
            return new SummingVisitor();
        }

        // Implement ThreadedMapVisitor For parallelism
        @Override
        public void merge(ThreadedMapVisitor tmv) {
            sum += ((SummingVisitor)tmv).sum;
        }
    }

}
