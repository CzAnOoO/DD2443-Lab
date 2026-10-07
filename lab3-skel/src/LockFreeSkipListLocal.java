import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
// import Log.*;

public class LockFreeSkipListLocal<T extends Comparable<T>> implements LockFreeSet<T> {
    /* Number of levels */
    private static final int MAX_LEVEL = 16;

    private final Node<T> head = new Node<T>();
    private final Node<T> tail = new Node<T>();

    private final List<Log.Entry>[] threadLog;

    @SuppressWarnings("unchecked")
    public LockFreeSkipListLocal(int threadsNum) {
        for (int i = 0; i < head.next.length; i++) {
            head.next[i] = new AtomicMarkableReference<LockFreeSkipListLocal.Node<T>>(tail, false);
        }

        threadLog = (List<Log.Entry>[]) new List[threadsNum];
        for (int i = 0; i < threadsNum; i++) {
            threadLog[i] = new ArrayList<>();
        }
    }

    private static final class Node<T> {
        private final T value;
        private final AtomicMarkableReference<Node<T>>[] next;
        private final int topLevel;

        @SuppressWarnings("unchecked")
        public Node() {
            value = null;
            next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[MAX_LEVEL + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node<T>>(null, false);
            }
            topLevel = MAX_LEVEL;
        }

        @SuppressWarnings("unchecked")
        public Node(T x, int height) {
            value = x;
            next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[height + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node<T>>(null, false);
            }
            topLevel = height;
        }
    }

    /* Returns a level between 0 to MAX_LEVEL,
     * P[randomLevel() = x] = 1/2^(x+1), for x < MAX_LEVEL.
     */
    private static int randomLevel() {
        int r = ThreadLocalRandom.current().nextInt();
        int level = 0;
        r &= (1 << MAX_LEVEL) - 1;
        while ((r & 1) != 0) {
            r >>>= 1;
            level++;
        }
        return level;
    }

    @SuppressWarnings("unchecked")
    public boolean add(int threadId, T x) {
        long timestamp;
        int topLevel = randomLevel();
        int bottomLevel = 0;
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
        while (true) {
            boolean found = find(x, preds, succs);
            if (found) {
                timestamp = System.nanoTime();
                threadLog[threadId].add(new Log.Entry(Log.Method.ADD, (Integer) x, false, timestamp));
                return false;
            } else {
                Node<T> newNode = new Node(x, topLevel);
                for (int level = bottomLevel; level <= topLevel; level++) {
                    Node<T> succ = succs[level];
                    newNode.next[level].set(succ, false);
                }
                Node<T> pred = preds[bottomLevel];
                Node<T> succ = succs[bottomLevel];
                if (!pred.next[bottomLevel].compareAndSet(succ, newNode, false, false)) {
                    continue;
                }
                timestamp = System.nanoTime();
                threadLog[threadId].add(new Log.Entry(Log.Method.ADD, (Integer) x, true, timestamp));
                for (int level = bottomLevel + 1; level <= topLevel; level++) {
                    while (true) {
                        pred = preds[level];
                        succ = succs[level];
                        if (pred.next[level].compareAndSet(succ, newNode, false, false))
                            break;
                        find(x, preds, succs);
                    }
                }
                return true;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public boolean remove(int threadId, T x) {
        long timestamp;
        int bottomLevel = 0;
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T> succ;
        while (true) {
            boolean found = find(x, preds, succs);
            if (!found) {
                timestamp = System.nanoTime();
                threadLog[threadId].add(new Log.Entry(Log.Method.REMOVE, (Integer) x, false, timestamp));
                return false;
            } else {
                Node<T> nodeToRemove = succs[bottomLevel];
                for (int level = nodeToRemove.topLevel; level >= bottomLevel + 1; level--) {
                    boolean[] marked = { false };
                    succ = nodeToRemove.next[level].get(marked);
                    while (!marked[0]) {
                        nodeToRemove.next[level].compareAndSet(succ, succ, false, true);
                        succ = nodeToRemove.next[level].get(marked);
                    }
                }
                boolean[] marked = { false };
                succ = nodeToRemove.next[bottomLevel].get(marked);
                while (true) {
                    boolean iMarkedIt = nodeToRemove.next[bottomLevel].compareAndSet(succ, succ, false, true);
                    succ = succs[bottomLevel].next[bottomLevel].get(marked);
                    timestamp = System.nanoTime();
                    if (iMarkedIt) {
                        threadLog[threadId].add(new Log.Entry(Log.Method.REMOVE, (Integer) x, true,
                                timestamp));
                        find(x, preds, succs);
                        return true;
                    } else if (marked[0]) {
                        threadLog[threadId].add(new Log.Entry(Log.Method.REMOVE, (Integer) x, false,
                                timestamp));
                        return false;
                    }
                }
            }
        }
    }

    public boolean contains(int threadId, T x) {
        long timestamp;
        boolean concains = false;
        int bottomLevel = 0;
        int key = x.hashCode();
        boolean[] marked = { false };
        Node<T> pred = head;
        Node<T> curr = null;
        Node<T> succ = null;
        for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
            curr = pred.next[level].getReference();
            while (true) {
                succ = curr.next[level].get(marked);
                while (marked[0]) {
                    curr = succ;
                    succ = curr.next[level].get(marked);
                }
                if (curr.value != null && x.compareTo(curr.value) < 0) {
                    pred = curr;
                    curr = succ;
                } else {
                    break;
                }
            }
        }
        if (curr.value != null && x.compareTo(curr.value) == 0) {
            curr.next[bottomLevel].get(marked);
            concains = !marked[0];
        }
        timestamp = System.nanoTime();
        threadLog[threadId].add(new Log.Entry(Log.Method.CONTAINS, (Integer) x, concains, timestamp));
        return concains;
    }

    private boolean find(T x, Node<T>[] preds, Node<T>[] succs) {
        int bottomLevel = 0;
        boolean[] marked = { false };
        boolean snip;
        Node<T> pred = null;
        Node<T> curr = null;
        Node<T> succ = null;
        retry: while (true) {
            pred = head;
            for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
                curr = pred.next[level].getReference();
                while (true) {
                    succ = curr.next[level].get(marked);
                    while (marked[0]) {
                        snip = pred.next[level].compareAndSet(curr, succ, false, false);
                        if (!snip)
                            continue retry;
                        curr = succ;
                        succ = curr.next[level].get(marked);
                    }
                    if (curr.value != null && x.compareTo(curr.value) < 0) {
                        pred = curr;
                        curr = succ;
                    } else {
                        break;
                    }
                }

                preds[level] = pred;
                succs[level] = curr;
            }
            return curr.value != null && x.compareTo(curr.value) == 0;
        }
    }

    public Log.Entry[] getLog() {
        // This should fetch the log from the skiplist.
        List<Log.Entry> allLogs = new ArrayList<>();

        for (List<Log.Entry> logs : threadLog) {
            allLogs.addAll(logs);
        }
        // https://www.geeksforgeeks.org/java/concurrentlinkedqueue-toarray-method-in-java/
        Log.Entry[] logA = new Log.Entry[allLogs.size()];
        return allLogs.toArray(logA);
    }

    public void reset() {
        for (int i = 0; i < head.next.length; i++) {
            head.next[i] = new AtomicMarkableReference<LockFreeSkipListLocal.Node<T>>(tail, false);
        }
        // TODO: Clear the log if you have one.
        for (List<Log.Entry> logs : threadLog) {
            logs.clear();
        }
    }
}