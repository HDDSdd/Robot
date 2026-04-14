package log;

/**
 * Статический фасад для упрощённого логирования в приложении.
 * <p>
 * Предоставляет удобные методы для записи сообщений различных уровней
 * ({@code debug}, {@code info}, {@code error}) в глобальный источник логов.
 * Класс реализует паттерн "Утилитный класс" с приватным конструктором,
 * предотвращающим создание экземпляров.
 * <b>Конфигурация по умолчанию:</b>
 * <ul>
 *   <li>Размер очереди сообщений: {@code 5} (настраивается при создании {@link LogWindowSource})</li>
 *   <li>Потокобезопасность: гарантируется делегированием вызовов в {@link LogWindowSource}</li>
 *   <li>Управление слушателями: через {@link #getDefaultLogSource()}</li>
 * </ul>
 * @see LogWindowSource
 * @see LogLevel
 * @see LogChangeListener
 */
public final class Logger {
    /**
     * Глобальный источник логов, используемый статическими методами этого класса.
     * Инициализируется при первом обращении к классу с ограничением очереди
     * в {@code 5} сообщений.
     */
    private static final LogWindowSource DEFAULT_LOG_SOURCE = new LogWindowSource(5);

    /**
     * Приватный конструктор предотвращает создание экземпляров класса.
     * <p>
     * Все методы класса являются статическими, поэтому инстанцирование
     * не имеет смысла и намеренно запрещено.
     */
    private Logger() {}

    /**
     * Записывает отладочное сообщение в лог.
     * <p>
     * Использует уровень {@link LogLevel#Debug} для сообщений,
     * полезных при отладке приложения, но не необходимых в рабочей среде.
     *
     * @param strMessage текст сообщения для логирования.
     *                   Если {@code null}, будет записано как строка {@code "null"}.
     * @see LogWindowSource#append(LogLevel, String)
     */
    public static void debug(String strMessage) {
        DEFAULT_LOG_SOURCE.append(LogLevel.Debug, strMessage);
    }

    /**
     * Записывает сообщение об ошибке в лог.
     * <p>
     * Использует уровень {@link LogLevel#Error} для критических событий,
     * требующих внимания разработчика или администратора.
     *
     * @param strMessage текст сообщения об ошибке.
     *                   Рекомендуется включать контекст: имя метода,
     *                   параметры, стек вызовов (при необходимости).
     * @see LogWindowSource#append(LogLevel, String)
     */
    public static void error(String strMessage) {
        DEFAULT_LOG_SOURCE.append(LogLevel.Error, strMessage);
    }

    /**
     * Записывает информационное сообщение в лог.
     * <p>
     * Использует уровень {@link LogLevel#Info} для фиксации значимых
     * событий жизненного цикла приложения: запуск, завершение,
     * успешное выполнение ключевых операций.
     *
     * @param strMessage текст информационного сообщения.
     * @see LogWindowSource#append(LogLevel, String)
     */
    public static void info(String strMessage) {
        DEFAULT_LOG_SOURCE.append(LogLevel.Info, strMessage);
    }

    /**
     * Возвращает глобальный источник логов для расширенной конфигурации.
     * <p>
     * Через возвращаемый объект можно:
     * <ul>
     *   <li>Зарегистрировать/отменить слушателей через
     *       {@link LogWindowSource#registerListener(LogChangeListener)}</li>
     *   <li>Получить доступ к истории сообщений через
     *       {@link LogWindowSource#all()} или {@link LogWindowSource#range(int, int)}</li>
     *   <li>Получить текущий размер очереди через {@link LogWindowSource#size()}</li>
     * </ul>
     * <p>
     */
    public static LogWindowSource getDefaultLogSource() {
        return DEFAULT_LOG_SOURCE;
    }
}