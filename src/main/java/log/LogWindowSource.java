package log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LogWindowSource {
    private final MessagesQueue messages;
    private final ArrayList<WeakReference<LogChangeListener>> listeners;
    private final Object lock = new Object();

    private volatile LogChangeListener[] cachedListeners;

    public LogWindowSource(int queueLength) {
        this.messages = new MessagesQueue(queueLength);
        this.listeners = new ArrayList<>();
    }

    public void registerListener(LogChangeListener listener) {
        synchronized (lock) {
            listeners.add(new WeakReference<>(listener));
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
                if (result == null)
                    result = updateCachedListeners();
            }
        }
        return result;
    }

    private LogChangeListener[] updateCachedListeners() {
        listeners.removeIf(x -> x.get() == null);

        var newCachedListeners = new ArrayList<LogChangeListener>();
        for (var ref : listeners) {
            var listener = ref.get();
            if (listener != null) {
                newCachedListeners.add(listener);
            }
        }

        cachedListeners = newCachedListeners.toArray(new LogChangeListener[0]);
        return cachedListeners;
    }

    public Iterable<LogEntry> all() {
        return messages.all();
    }
}