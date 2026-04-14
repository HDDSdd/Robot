package log;

import java.util.*;

/**
 * Класс, который хранит в себе очередь из сообщений в лог
 */
public class ListLog {
    private Deque<LogEntry> deque;
    private final int capacity;

    /**
     * Конструктор класса
     * @param capacity
     */
    public ListLog(int capacity) {
        this.capacity = capacity;
        deque = new ArrayDeque<>();
    }

    /**
     * Добавляет новую запись. Если очередь переполнена, удаляет самую старую.
     */
    public synchronized void add(LogEntry entry) {
        if (deque.size() >= capacity) {
            deque.pollFirst();
        }
        deque.addLast(entry);
    }

    /**
     * Возвращает размер очереди
     */
    public synchronized int size() { return deque.size(); }

    /**
     * Метод, который возвращает subList c указанного индексв до нужного
     */
    public List<LogEntry> subList(int from, int to) {
        List<LogEntry> sublist = new ArrayList<>();
        Iterator<LogEntry> iterator = iterator();

        for (int i = 0; i <= to; i++) {
            if (iterator.hasNext()) {
                LogEntry entry = iterator.next();
                if (i >= from) {
                    sublist.add(entry);
                }
            } else {
                break;
            }
        }
        return sublist;
    }

    /**
     * Возвращает итератор для последовательного перебора записей
     */
    public synchronized Iterator<LogEntry> iterator() {
        return new ArrayList<>(deque).iterator();
    }

}