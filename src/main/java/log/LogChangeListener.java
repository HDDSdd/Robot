package log;

/**
 * Интерфейс слушателя изменений лога.
 * <p>
 * Реализации этого интерфейса регистрируются в {@link LogWindowSource}
 * для получения уведомлений о появлении новых записей в журнале.
 * Является частью механизма шаблона "Наблюдатель" (Observer).
 * <p>
 * Важно: метод {@link #onLogChanged()} вызывается синхронно в том потоке,
 * который выполнил запись в лог.
 * @see LogWindowSource#registerListener(LogChangeListener)
 * @see LogWindowSource#unregisterListener(LogChangeListener)
 */
public interface LogChangeListener
{
    /**
     * Вызывается при каждом добавлении новой записи в лог.
     * Метод может быть вызван из любого потока, использующего экземпляр
     * {@link LogWindowSource}. Реализация должна быть потокобезопасной
     * в отношении своего внутреннего состояния.
     */
    void onLogChanged();
}