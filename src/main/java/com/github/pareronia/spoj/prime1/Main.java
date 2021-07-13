package com.github.pareronia.spoj.prime1;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Main {
    
    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public Main(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.sample = sample;
        this.in = in;
        this.out = out;
    }
    
    private void log(final Supplier<Object> supplier) {
        if (!sample) {
            return;
        }
        System.out.println(supplier.get());
    }
    
    private Result<?> handleTestCase(final Scanner sc, final Integer i) {
        final int m = sc.nextInt();
        final int n = sc.nextInt();
        log(() -> m);
        assert m <= 1_000_000_000;
        log(() -> n);
        assert n <= 1_000_000_000;
        assert n - m <= 100_000;
        final List<Integer> primes = getPrimes(m, n + 1).stream()
                .filter(p -> m <= p && p <= n)
                .collect(toList());
        log(() -> primes.size());
        return new Result<>(primes);
    }
    
    private List<Integer> simpleSieve(final int x) {
        final boolean[] a = new boolean[x];
        Arrays.fill(a, true);
        for (int i = 2; i <= Math.sqrt(x); i++) {
            if (!a[i]) {
                continue;
            }
            for (int j = i * i; j < x; j += i) {
                a[j] = false;
            }
        }
        final List<Integer> ans = new ArrayList<>();
        for (int i = 2; i < x; i++) {
            if (a[i]) {
                ans.add(i);
            }
        }
        return ans;
    }
    
    private List<Integer> getPrimes(final int from, final int x) {
        final List<Integer> ans = new ArrayList<>();
        final int sqrt = (int) Math.floor(Math.sqrt(x)) + 1;
        final List<Integer> primes = simpleSieve(sqrt);
        primes.stream().filter(p -> p >= from).forEach(ans::add);
        
        int low = sqrt;
        int high = 2 * sqrt;
        while (low < x) {
            if (low <= from && from <= high || from < low) {
                final boolean[] mark = new boolean[sqrt + 1];
                Arrays.fill(mark, true);
                for (int i = 0; i < primes.size(); i++) {
                    int start = low / primes.get(i) * primes.get(i);
                    if (start < low) {
                        start += primes.get(i);
                    }
                    for (int j = start; j < high; j += primes.get(i)) {
                        mark[j - low] = false;
                    }
                }
                
                for (int i = low; i < high; i++) {
                    if (mark[i - low]) {
                        ans.add(i);
                    }
                }
            }
            low += sqrt;
            high = Math.min(x, high + sqrt);
        }
        return ans;
    }
    
    private void output(final List<Result<?>> results) {
        results.forEach(r -> {
            r.values.stream().map(Object::toString).forEach(this.out::println);
            this.out.println("");
        });
    }
    
    public void solve() {
        try (final Scanner sc = new Scanner(new InputStreamReader(this.in))) {
            final int numberOfTestCases = sc.nextInt();
            final List<Result<?>> results =
                    Stream.iterate(1, i -> i <= numberOfTestCases, i -> i + 1)
                            .map(i -> handleTestCase(sc, i))
                            .collect(toList());
            output(results);
        }
    }

    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final InputStream is;
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            is = Main.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new Main(sample, is, out).solve();
    	
        if (sample) {
            final long timeSpent = (System.nanoTime() - timerStart) / 1_000;
            final double time;
            final String unit;
            if (timeSpent < 1_000) {
                time = timeSpent;
                unit = "µs";
            } else if (timeSpent < 1_000_000) {
                time = timeSpent / 1_000.0;
                unit = "ms";
            } else {
                time = timeSpent / 1_000_000.0;
                unit = "s";
            }
            final Path path
                    = Paths.get(Main.class.getResource("sample.out").toURI());
            final List<String> expected = Files.readAllLines(path);
            final List<String> actual = asList(baos.toString().split("\\r?\\n"));
            if (!expected.equals(actual)) {
                throw new AssertionError(String.format(
                        "Expected %s, got %s", expected, actual));
            }
            actual.forEach(System.out::println);
            System.out.println(String.format("took: %.3f %s", time, unit));
        }
    }

    private static boolean isSample() {
        return "sample".equals(System.getProperty("spoj"));
    }
    
    private static final class Result<T> {
        private final List<T> values;

        public Result(final List<T> values) {
            this.values = values;
        }
    }
}
