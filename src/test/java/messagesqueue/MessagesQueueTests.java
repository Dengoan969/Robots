package messagesqueue;

import log.LogEntry;
import log.LogLevel;
import log.MessagesQueue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
