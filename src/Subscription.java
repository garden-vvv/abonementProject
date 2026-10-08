import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Базовый класс абонемента.
 *
 * <p>Хранит клиента, тариф, дату начала и окончания и историю операций
 * (заморозки тоже хранятся как операции FREEZE).
 * Конкретные типы абонементов (разовый, обычный, студенческий) наследуются
 * от этого класса и переопределяют метод {@link #checkFreeze(int)},
 * задавая собственные правила заморозки.</p>
 */
public abstract class Subscription {
    private final SubscriptionId id;
    private final Client client;
    private final Tariff tariff;
    private LocalDate start;
    private LocalDate end;
    private final List<Operation> operations = new ArrayList<>();

    /**
     * Покупает абонемент: считает дату окончания и записывает покупку в историю.
     *
     * @param id      идентификатор абонемента, не null
     * @param client  клиент, не null
     * @param tariff  тариф
     * @param start   первый день действия, не null
     * @param periods число периодов, больше нуля
     * @throws SubscriptionException если аргументы некорректны
     */
    protected Subscription(SubscriptionId id, Client client, Tariff tariff, LocalDate start, int periods) {
        if (id == null || client == null || start == null) {
            throw new SubscriptionException("ID, клиент и дата начала обязательны");
        }
        checkPositive(periods);
        this.id = id;
        this.client = client;
        this.tariff = tariff;
        this.start = start;
        this.end = tariff.endDate(start, periods);
        operations.add(new Operation(start, OperationType.PURCHASE, tariff.cost(periods)));
    }

    /**
     * Продлевает абонемент. Если на дату продления абонемент ещё действует,
     * новые периоды добавляются после даты окончания; если истёк — отсчитываются от даты продления.
     *
     * @param periods число периодов, больше нуля
     * @param date    дата продления
     * @throws SubscriptionException если periods <= 0
     */
    public void renew(int periods, LocalDate date) {
        checkPositive(periods);
        if (date.isAfter(end)) {
            start = date;
            end = tariff.endDate(date, periods);
        } else {
            end = tariff.endDate(end.plusDays(1), periods);
        }
        operations.add(new Operation(date, OperationType.RENEWAL, tariff.cost(periods)));
    }

    /**
     * Замораживает абонемент на {@code days} дней со сдвигом даты окончания.
     *
     * <p>Сначала проверяется число дней, затем — правила заморозки
     * конкретного типа абонемента.</p>
     *
     * @param from первый день заморозки, абонемент должен действовать в этот день
     * @param days число дней, больше нуля
     * @throws SubscriptionException если заморозка недопустима
     */
    public void freeze(LocalDate from, int days) {
        checkPositive(days);
        checkFreeze(days);
        if (!isActiveOn(from)) {
            throw new SubscriptionException("нельзя заморозить: абонемент не действует на " + from);
        }
        end = end.plusDays(days);
        operations.add(new Operation(from, OperationType.FREEZE, days));
    }

    /**
     * Проверяет, действует ли абонемент на дату: дата в сроке действия и не в заморозке.
     *
     * @param date дата
     * @return true, если абонементом можно воспользоваться
     */
    public boolean isActiveOn(LocalDate date) {
        return !date.isBefore(start) && !date.isAfter(end) && !isFrozenOn(date);
    }

    /**
     * Использует абонемент (отмечает посещение).
     *
     * @param date дата посещения
     * @throws Exception если абонемент заморожен или не действует на дату
     */
    public void use(LocalDate date) throws Exception {
        if (isFrozenOn(date)) {
            throw new Exception("абонемент заморожен на " + date);
        }
        if (!isActiveOn(date)) {
            throw new Exception("абонемент не действует на " + date + " (срок: " + start + " - " + end + ")");
        }
        operations.add(new Operation(date, OperationType.VISIT, 0));
    }

    /**
     * Считает, сколько всего заплачено: покупка и продления.
     *
     * @return сумма в рублях
     */
    public int totalCost() {
        int total = 0;
        for (Operation operation : operations) {
            if (operation.operationType() == OperationType.PURCHASE
                    || operation.operationType() == OperationType.RENEWAL) {
                total += operation.value();
            }
        }
        return total;
    }

    /**
     * Проверяет, можно ли заморозить абонемент данного типа на {@code days} дней.
     *
     * @param days число дней заморозки
     * @throws SubscriptionException если заморозка недопустима для этого типа
     */
    protected abstract void checkFreeze(int days);

    /**
     * Возвращает идентификатор абонемента.
     *
     * @return идентификатор
     */
    public SubscriptionId getId() {
        return id;
    }

    /**
     * Возвращает клиента.
     *
     * @return клиент
     */
    public Client getClient() {
        return client;
    }

    /**
     * Возвращает тариф.
     *
     * @return тариф
     */
    public Tariff getTariff() {
        return tariff;
    }

    /**
     * Возвращает первый день действия.
     *
     * @return дата начала
     */
    public LocalDate getStart() {
        return start;
    }

    /**
     * Возвращает последний день действия.
     *
     * @return дата окончания (включительно)
     */
    public LocalDate getEnd() {
        return end;
    }

    /**
     * Возвращает историю операций.
     *
     * @return неизменяемый список операций
     */
    public List<Operation> getOperations() {
        return List.copyOf(operations);
    }

    private boolean isFrozenOn(LocalDate date) {
        for (Operation operation : operations) {
            if (operation.operationType() == OperationType.FREEZE) {
                LocalDate freezeEnd = operation.date().plusDays(operation.value() - 1);
                if (!date.isBefore(operation.date()) && !date.isAfter(freezeEnd)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void checkPositive(int value) {
        if (value <= 0) {
            throw new SubscriptionException("значение должно быть больше нуля: " + value);
        }
    }
}
