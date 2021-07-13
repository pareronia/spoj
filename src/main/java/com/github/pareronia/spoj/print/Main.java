package com.github.pareronia.spoj.print;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main {
    
    private final InputStream in;
    private final PrintStream out;
    
    public Main(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final FastScanner sc, final Integer i) {
        final int m = sc.nextInt();
        final int n = sc.nextInt();
        assert m <= Integer.MAX_VALUE;
        assert n <= Integer.MAX_VALUE;
        assert n - m <= 100_000;
        if (n == Integer.MAX_VALUE) {
            getPrimes(m, Integer.MAX_VALUE);
            this.out.println(Integer.MAX_VALUE);
        } else {
            getPrimes(m, n + 1);
        }
    }
    
    private int[] simpleSieve(final int from, final int x) {
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
        int cnt = 0;
        for (int i = 2; i < x; i++) {
            if (a[i]) {
                cnt++;
                if (i >= from) {
                    this.out.println(i);
                }
            }
        }
        final int[] ans = new int[cnt];
        int j = 0;
        for (int i = 2; i < x; i++) {
            if (a[i]) {
                ans[j++] = i;
            }
        }
        return ans;
    }
    
    private void getPrimes(final int from, final int x) {
        final int sqrt = (int) Math.floor(Math.sqrt(x)) + 1;
        final int[] primes = simpleSieve(from, sqrt);
        
        long low = sqrt;
        long high = 2 * sqrt;
        while (low < x) {
            if (low <= from && from <= high || from < low) {
                final boolean[] mark = new boolean[sqrt + 1];
                Arrays.fill(mark, true);
                for (int i = 0; i < primes.length; i++) {
                    long start = low / primes[i] * primes[i];
                    if (start < low) {
                        start += primes[i];
                    }
                    for (long j = start; j < high; j += primes[i]) {
                        mark[(int) (j - low)] = false;
                    }
                }
                
                for (long i = low; i < high; i++) {
                    if (mark[(int) (i - low)]
                            && i <= Integer.MAX_VALUE && from <= i) {
                        this.out.println(i);
                    }
                }
            }
            low += sqrt;
            high = Math.min(x, high + sqrt);
        }
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases = sc.nextInt();
            for (int i = 1; i <= numberOfTestCases; i++) {
                handleTestCase(sc, i);
            }
        }
        this.out.flush();
    }

    public static void main(final String[] args) throws IOException, URISyntaxException {
        new Main(false, System.in, System.out).solve();
    }
    
    private static final class FastScanner implements Closeable {
        private final BufferedReader br;
        private StringTokenizer st;
        
        public FastScanner(final InputStream in) {
            this.br = new BufferedReader(new InputStreamReader(in));
            st = new StringTokenizer("");
        }
        
        public String next() {
            while (!st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return st.nextToken();
        }
    
        public int nextInt() {
            return Integer.parseInt(next());
        }
        
        @SuppressWarnings("unused")
        public long nextLong() {
            return Long.parseLong(next());
        }

        @Override
        public void close() {
            try {
                this.br.close();
            } catch (final IOException e) {
                // ignore
            }
        }
    }
}
