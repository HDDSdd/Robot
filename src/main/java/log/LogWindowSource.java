package log;

import java.util.Collections;
import java.util.List;

/**
 * Источник данных для компонента отображения логов.
 * <p>
 * Управляет коллекцией записей журнала ({@link LogEntry}) и уведомляет
 * зарегистрированных слушателей ({@link LogChangeListener}) о появлении новых сообщений.
 * Реализует механизм ограниченной очереди: при превышении лимита, заданного в конструкторе,
 * наиболее старые записи автоматически удаляются, предотвращая бесконтрольное потребление памяти.
 * <p>
 * Класс потокобезопасен. Методы регистрации/отмены слушателей и добавления записей
 * синхронизированы, а итерация по слушателям выполняется над моментальным снимком (snapshot),
 * что гарантирует корректную работу в многопоточной среде без блокировок на этапе оповещения.
 * <p>
 * <b>Управление жизненным циклом слушателей:</b> внутреннее хранилище слушателей использует
 * слабые ссылки ({@link WeakRefList}), что снижает риск утечек памяти.
 *
 * @see LogEntry
 * @see LogChangeListener
 * @see WeakRefList
 */
public class LogWindowSource
{
    private ListLog messages;
    private final List<LogChangeListener> listeners;
    private volatile List<LogChangeListener> activeListeners;

    /**
     * Создаёт экземпляр источника логов с заданным максимальным размером очереди.
     *
     * @param iQueueLength максимальное количество сообщений, хранимых в логе.
     *                     При добавлении новых записей сверх этого лимита наиболее
     *                     старые записи автоматически удаляются. Должно быть {@code > 0}.
     */
    public LogWindowSource(int iQueueLength)
    {
        messages = new ListLog(iQueueLength);
        listeners = new WeakRefList<>();
    }

    /**
     * Регистрирует слушатель для получения уведомлений об изменении лога.
     * Слушатель будет вызываться синхронно при каждом добавлении новой записи.
     *
     * @param listener экземпляр {@link LogChangeListener} для регистрации.
     *                 Если {@code null}, вызов игнорируется или бросает
     *                 {@link NullPointerException} (зависит от реализации {@code WeakRefList}).
     */
    public void registerListener(LogChangeListener listener)
    {
        synchronized(listeners)
        {
            listeners.add(listener);
            activeListeners = null;
        }
    }

    /**
     * Отменяет регистрацию ранее добавленного слушателя.
     * <p>
     * Вызов этого метода гарантирует, что слушатель больше не будет получать уведомления
     * и позволит сборщику мусора освободить связанные ресурсы.
     *
     * @param listener ранее зарегистрированный {@link LogChangeListener}.
     */
    public void unregisterListener(LogChangeListener listener)
    {
        synchronized(listeners)
        {
            listeners.remove(listener);
            activeListeners = null;
        }
    }

    /**
     * Добавляет новую запись в лог и уведомляет всех активных слушателей.
     * <p>
     * Метод потокобезопасен. Если количество записей превышает лимит, указанный
     * в конструкторе, наиболее старые записи автоматически удаляются.
     * Оповещение слушателей происходит на моментальном снимке списка, что предотвращает
     * блокировки во время вызова {@code listener.onLogChanged()}.
     *
     * @param logLevel   уровень важности сообщения (INFO, WARN, ERROR и т.д.)
     * @param strMessage текст сообщения лога
     */
    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        messages.add(entry);
        List<LogChangeListener> activeListeners = this.activeListeners;
        if (activeListeners == null)
        {
            synchronized (listeners)
            {
                if (this.activeListeners == null)
                {
                    activeListeners = new WeakRefList<>(listeners);
                    this.activeListeners = activeListeners;
                }
            }
        }
        for (LogChangeListener listener : activeListeners)
        {
            listener.onLogChanged();
        }
    }

    /**
     * Возвращает текущее количество записей в логе.
     *
     * @return число хранимых {@link LogEntry}, {@code >= 0}
     */
    public int size()
    {
        return messages.size();
    }

    /**
     * Возвращает подмножество записей лога, начиная с указанной позиции.
     *
     * @param startFrom индекс первой записи (начиная с {@code 0}). Если выходит за
     *                  границы допустимого диапазона, метод вернёт пустой {@link Iterable}.
     * @param count     максимальное количество записей для возврата. Если {@code count}
     *                  превышает оставшееся количество записей, вернётся доступный диапазон.
     * @return {@link Iterable} содержащий до {@code count} записей, начиная с {@code startFrom}.
     *         Возвращаемый объект отражает состояние лога на момент вызова.
     */
    public Iterable<LogEntry> range(int startFrom, int count)
    {
        if (startFrom < 0 || startFrom >= messages.size())
        {
            return Collections.emptyList();
        }
        int indexTo = Math.min(startFrom + count, messages.size());
        return messages.subList(startFrom, indexTo);
    }

    /**
     * Возвращает итератор по всем записям лога в порядке добавления.
     *
     * @return {@link Iterable}, содержащий все текущие записи лога.
     *         Итерация безопасна в рамках одного вызова, но не гарантирует
     *         согласованность при параллельном изменении лога.
     */
    public Iterable<LogEntry> all()
    {
        return () -> messages.iterator();
    }
}