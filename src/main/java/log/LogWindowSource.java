package log;

import java.util.ArrayList;
import java.util.Collections;

public class LogWindowSource {
    private final MessagesQueue messages;
    private final ArrayList<LogChangeListener> listeners;
    private final Object lock = new Object();

    private volatile LogChangeListener[] cachedListeners;

    public LogWindowSource(int queueLength) {
        this.messages = new MessagesQueue(queueLength);
        this.listeners = new ArrayList<>();
    }

    public void registerListener(LogChangeListener listener) {
        synchronized (lock) {
            listeners.add(listener);
            cachedListeners = null;
        }
    }

    public void unregisterListener(LogChangeListener listener) {
        synchronized (lock) {
            listeners.remove(listener);
            cachedListeners = null;
        }
    }

    public void append(LogLevel logLevel, String strMessage) {
        messages.append(new LogEntry(logLevel, strMessage));
        notifyListeners();
    }

    private void notifyListeners() {
        var currentListeners = getCurrentListeners();
        for (var listener : currentListeners) {
            listener.onLogChanged();
        }
    }

    private LogChangeListener[] getCurrentListeners() {
        var result = cachedListeners;
        if (result == null) {
            synchronized (lock) {
                result = cachedListeners;
                if (result == null) {
                    result = listeners.toArray(new LogChangeListener[0]);
                    cachedListeners = result;
                }
            }
        }
        return result;
    }

    public int size() {
        return messages.size();
    }

    public Iterable<LogEntry> range(int startFrom, int count) {
        if (startFrom < 0 || startFrom >= size()) {
            return Collections.emptyList();
        }
        return messages.range(startFrom, count);
    }

    public Iterable<LogEntry> all() {
        return messages.all();
    }
}