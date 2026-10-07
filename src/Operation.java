import java.time.LocalDate;

/**
 * Запись об одной операции с абонементом.
 *
 * <p>Неизменяемое значение. Содержит дату, тип операции и значение:
 * сумму в рублях для покупки и продления, число дней для заморозки, 0 для посещения.</p>
 *
 * @param date          дата операции
 * @param operationType тип операции
 * @param value         сумма или число дней, не отрицательное
 */
public record Operation(LocalDate date, OperationType operationType, int value) {
    public Operation {
        if (date == null || operationType == null) {
            throw new SubscriptionException("дата и тип операции обязательны");
        }
        if (value < 0) {
            throw new SubscriptionException("значение операции не может быть отрицательным");
        }
    }
}
