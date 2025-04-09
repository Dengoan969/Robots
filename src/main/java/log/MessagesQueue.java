package log;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MessagesQueue {

    private final LogEntry[] buffer;
    private final int capacity;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private int head = 0;
    private int tail = -1;
    private int size = 0;


    public MessagesQueue(int capacity) {
        this.capacity = capacity;
        this.buffer = new LogEntry[capacity];
    }

    public void append(LogEntry element) {
        lock.writeLock().lock();
        try {
            tail = (tail + 1) % capacity;
            buffer[tail] = element;

            if (size < capacity) {
                size++;
            } else {
                head = (head + 1) % capacity;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Iterable<LogEntry> all() {
        return () -> new LogIterator(0, null);
    }

    public Iterable<LogEntry> range(int startFrom, Integer count) {
        return () -> new LogIterator(startFrom, count);
    }

    private class LogIterator implements Iterator<LogEntry> {
        private int previousHead;
        private int currentIndex;
        private Integer takeCount;

        public LogIterator(int startFrom, Integer count) {
            lock.readLock().lock();
            try {
                this.previousHead = head;
                this.currentIndex = startFrom;
                this.takeCount = count;
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public boolean hasNext() {
            lock.readLock().lock();
            try {

                if (head != previousHead) {
                    currentIndex -= (Math.abs(head - previousHead));
                    previousHead = head;
                }

                return currentIndex < size && (takeCount == null || takeCount > 0);
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public LogEntry next() {
            lock.readLock().lock();
            try {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                var actualIndex = (head + currentIndex) % capacity;
                var element = buffer[actualIndex];

                currentIndex++;
                if (takeCount != null)
                    takeCount--;
                return element;
            } finally {
                lock.readLock().unlock();
            }
        }
    }
}
