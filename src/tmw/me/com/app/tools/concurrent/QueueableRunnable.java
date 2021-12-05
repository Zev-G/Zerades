package tmw.me.com.app.tools.concurrent;

import java.util.ArrayList;

public class QueueableRunnable implements Runnable {

    private volatile ArrayList<Runnable> futureRunnables = new ArrayList<>();
    private volatile boolean locked = false;
    private volatile boolean finalized = false;

    public QueueableRunnable(Runnable defaultRunnable) {
        futureRunnables.add(defaultRunnable);
    }
    public QueueableRunnable() {}

    @Override
    public void run() {
        while (!finalized) {
            if (!locked && futureRunnables != null && !futureRunnables.isEmpty()) {
                futureRunnables.get(0).run();
                futureRunnables.remove(0);
            }
        }
    }

    public ArrayList<Runnable> getFutureRunnables() {
        return futureRunnables;
    }

    public void addFutureRunnable(Runnable runnable) {
        futureRunnables.add(runnable);
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setFutureRunnables(ArrayList<Runnable> futureRunnables) {
        this.futureRunnables = futureRunnables;
    }

    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }

    public boolean isFinalized() {
        return finalized;
    }

}
