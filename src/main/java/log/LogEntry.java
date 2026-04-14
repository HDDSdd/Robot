package log;

/**
 * Представляет одну запись (сообщение) в журнале логирования.
 * <p>
 * Является контейнером данных (DTO), хранящим уровень важности
 * и текстовое содержание сообщения. Класс не предоставляет методов
 * изменения состояния после создания, что делает его фактически
 * неизменяемым (immutable) и потокобезопасным.
 * <p>
 * Экземпляры данного класса создаются при добавлении новых записей
 * через {@link LogWindowSource#append(LogLevel, String)} и могут
 * безопасно передаваться между потоками, храниться в коллекциях
 * или сериализовываться для вывода в интерфейс.
 *
 * @see LogLevel
 * @see LogWindowSource
 */
public class LogEntry
{
    private LogLevel logLevel;
    private String strMessage;

    /**
     * Создаёт новую запись лога с указанным уровнем важности и сообщением.
     *
     * @param logLevel   уровень важности сообщения (например, INFO, WARN, ERROR).
     *                   Допускается {@code null}, но для консистентности данных
     *                   рекомендуется передавать валидный экземпляр {@link LogLevel}.
     * @param strMessage текстовое содержание сообщения. Может быть {@code null}
     *                   или пустой строкой.
     */
    public LogEntry(LogLevel logLevel, String strMessage)
    {
        this.strMessage = strMessage;
        this.logLevel = logLevel;
    }

    /**
     * Возвращает текстовое содержание сообщения лога.
     *
     * @return строка сообщения или {@code null}, если при создании
     *         было передано значение {@code null}.
     */
    public String getMessage()
    {
        return strMessage;
    }

    /**
     * Возвращает уровень важности данной записи лога.
     *
     * @return экземпляр {@link LogLevel} или {@code null}, если уровень
     *         не был указан при создании записи.
     */
    public LogLevel getLevel()
    {
        return logLevel;
    }
}