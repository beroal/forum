import java.util.Set;
import java.util.HashSet;
public class FingTable {
    Set<Thread> set;
    public FingTable() {
        set = new HashSet<Thread>();
    }
    public synchronized void addAndStart(Thread thread) {
        set.add(thread);
        thread.start();
    }
    public synchronized void remove(Thread thread) { set.remove(thread); }
    public synchronized void wakeAll() {
        for (Thread thread : set) { thread.interrupt(); }
    }
}
