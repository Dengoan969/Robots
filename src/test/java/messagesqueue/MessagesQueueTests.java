package messagesqueue;

import log.LogEntry;
import log.LogLevel;
import log.MessagesQueue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagesQueueTests {

    private LogEntry createLogEntry(String message) {
        return new LogEntry(LogLevel.Info, message);
    }

    @Test
    void shouldAppend() {
        var queue = new MessagesQueue(3);
        assertEquals(0, queue.size());

        queue.append(createLogEntry("Сообщение 1"));
        assertEquals(1, queue.size());

        queue.append(createLogEntry("Сообщение 2"));
        queue.append(createLogEntry("Сообщение 3"));
        queue.append(createLogEntry("Сообщение 4"));

        assertEquals(3, queue.size());
    }

    @Test
    void shouldAppendConcurrent() throws InterruptedException {
        var queue = new MessagesQueue(1000);
        var threadsCount = 10;
        var threads = new Thread[threadsCount];
        var expected = new ConcurrentLinkedQueue<String>();

        for (var i = 0; i < threadsCount; i++) {
            var threadNum = i;
            threads[i] = new Thread(() -> {
                for (var j = 0; j < 80; j++) {
                    var message = String.format("Сообщение %d - %d", threadNum, j);
                    expected.add(message);
                    queue.append(createLogEntry(message));
                }
            });

            threads[i].start();
        }

        for (var thread : threads) {
            thread.join();
        }

        assertEquals(expected.size(), queue.size());

        var actual = new HashSet<String>();
        for (var entry : queue.all()) {
            actual.add(entry.getMessage());
        }

        for (var message : expected) {
            assertTrue(actual.contains(message));
        }
    }

    @Test
    void shouldRemoveOldest() {
        var queue = new MessagesQueue(3);
        queue.append(createLogEntry("Сообщение 1"));
        queue.append(createLogEntry("Сообщение 2"));
        queue.append(createLogEntry("Сообщение 3"));
        queue.append(createLogEntry("Сообщение 4"));

        assertEquals(3, queue.size());

        var messages = new ArrayList<LogEntry>();
        for (var entry : queue.all()) {
            messages.add(entry);
        }

        assertEquals("Сообщение 2", messages.getFirst().getMessage());
    }

    @Test
    void shouldReturnRange() {
        MessagesQueue queue = new MessagesQueue(5);
        queue.append(createLogEntry("Сообщение 1"));
        queue.append(createLogEntry("Сообщение 2"));
        queue.append(createLogEntry("Сообщение 3"));
        queue.append(createLogEntry("Сообщение 4"));
        queue.append(createLogEntry("Сообщение 5"));


        var result = new ArrayList<LogEntry>();
        for (var entry : queue.range(1, 3)) {
            result.add(entry);
        }

        assertEquals(3, result.size());
        assertEquals("Сообщение 2", result.get(0).getMessage());
        assertEquals("Сообщение 3", result.get(1).getMessage());
        assertEquals("Сообщение 4", result.get(2).getMessage());
    }

    @Test
    void shouldConcurrentReturnRange() throws InterruptedException {
        var queue = new MessagesQueue(1000);
        var threadsCount = 10;
        var threads = new Thread[threadsCount];
        var expectedCount = new AtomicInteger(0);

        for (var i = 0; i < threadsCount; i++) {
            var threadNum = i;
            threads[i] = new Thread(() -> {
                for (var j = 0; j < 100; j++) {
                    var message = String.format("Сообщение %d - %d", threadNum, j);
                    queue.append(createLogEntry(message));
                    expectedCount.incrementAndGet();
                }
            });
            threads[i].start();
        }

        var allMessages = new ConcurrentLinkedQueue<String>();
        var reader = new Thread(() -> {
            while (expectedCount.get() < threadsCount * 100) {
                for (var entry : queue.all()) {
                    allMessages.add(entry.getMessage());
                }
            }
        });
        reader.start();

        for (var thread : threads) {
            thread.join();
        }
        reader.join();

        assertEquals(expectedCount.get(), queue.size());

        var readerMessages = new HashSet<>(allMessages);

        for (var entry : queue.all()) {
            assertTrue(readerMessages.contains(entry.getMessage()));
        }
    }

    @Test
    void shouldReturnRangeWhenElementsLess() {
        var queue = new MessagesQueue(5);
        queue.append(createLogEntry("Сообщение 1"));
        queue.append(createLogEntry("Сообщение 2"));


        var result = new ArrayList<LogEntry>();
        for (var entry : queue.range(0, 5)) {
            result.add(entry);
        }

        assertEquals(2, result.size());
        assertEquals("Сообщение 1", result.get(0).getMessage());
        assertEquals("Сообщение 2", result.get(1).getMessage());
    }

    @Test
    void shouldReturnAllElements() {
        var queue = new MessagesQueue(5);
        queue.append(createLogEntry("Сообщение 1"));
        queue.append(createLogEntry("Сообщение 2"));
        queue.append(createLogEntry("Сообщение 3"));
        queue.append(createLogEntry("Сообщение 4"));
        queue.append(createLogEntry("Сообщение 5"));


        var result = new ArrayList<LogEntry>();
        for (var entry : queue.all()) {
            result.add(entry);
        }

        assertEquals(5, result.size());
        assertEquals("Сообщение 1", result.get(0).getMessage());
        assertEquals("Сообщение 2", result.get(1).getMessage());
        assertEquals("Сообщение 3", result.get(2).getMessage());
        assertEquals("Сообщение 4", result.get(3).getMessage());
        assertEquals("Сообщение 5", result.get(4).getMessage());
    }

    @Test
    void shouldReturnAllWhenAddedNew() {
        var queue = new MessagesQueue(3);
        queue.append(createLogEntry("Сообщение 1"));
        queue.append(createLogEntry("Сообщение 2"));
        queue.append(createLogEntry("Сообщение 3"));

        var iterator = queue.all().iterator();
        assertEquals("Сообщение 1", iterator.next().getMessage());
        assertEquals("Сообщение 2", iterator.next().getMessage());

        queue.append(createLogEntry("Сообщение 4"));
        queue.append(createLogEntry("Сообщение 5"));
        assertEquals("Сообщение 3", iterator.next().getMessage());
        assertEquals("Сообщение 4", iterator.next().getMessage());
        assertEquals("Сообщение 5", iterator.next().getMessage());


        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void shouldConcurrentReturnAllElementsWhenAppend() throws InterruptedException {
        var queue = new MessagesQueue(1000);
        var threadsCount = 10;
        var threads = new Thread[threadsCount];
        var expectedCount = new AtomicInteger(0);

        for (var i = 0; i < threadsCount; i++) {
            var threadNum = i;
            threads[i] = new Thread(() -> {
                for (var j = 0; j < 100; j++) {
                    var message = String.format("Сообщение %d - %d", threadNum, j);
                    queue.append(createLogEntry(message));
                    expectedCount.incrementAndGet();
                }
            });
            threads[i].start();
        }

        var allMessages = new ConcurrentLinkedQueue<String>();

        var reader = new Thread(() -> {
            while (expectedCount.get() < threadsCount * 100) {
                for (var entry : queue.all()) {
                    allMessages.add(entry.getMessage());
                }
            }
        });
        reader.start();

        for (var thread : threads) {
            thread.join();
        }
        reader.join();

        assertEquals(expectedCount.get(), queue.size());

        var readerMessages = new HashSet<>(allMessages);

        for (var entry : queue.all()) {
            assertTrue(readerMessages.contains(entry.getMessage()));
        }
    }
}
