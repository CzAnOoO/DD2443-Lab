import java.util.Arrays;
import java.util.HashSet;

public class Log {
        private Log() {
                // Do not implement
        }

        public static int validate(Log.Entry[] log) {
                // Implement this.
                // Should return the number of discrepancies in the log.
                if (log == null) {
                        return -1;
                }

                HashSet<Integer> s = new HashSet<>();
                Arrays.sort(log, (a, b) -> Long.compare(a.timestamp, b.timestamp));

                int discrepancies = 0;
                for (Entry e : log) {
                        boolean flag = false;
                        if (e.method == Log.Method.ADD) {
                                flag = s.add(e.arg);
                        } else if (e.method == Log.Method.REMOVE) {
                                flag = s.remove(e.arg);
                        } else if (e.method == Log.Method.CONTAINS) {
                                flag = s.contains(e.arg);
                        }

                        if (flag != e.ret) {
                                discrepancies++;
                        }
                }

                return discrepancies;
        }

        // Log entry for linearization point.
        public static class Entry {
                public Method method;
                public int arg;
                public boolean ret;
                public long timestamp;

                public Entry(Method method, int arg, boolean ret, long timestamp) {
                        this.method = method;
                        this.arg = arg;
                        this.ret = ret;
                        this.timestamp = timestamp;
                }
        }

        public static enum Method {
                ADD, REMOVE, CONTAINS
        }
}
