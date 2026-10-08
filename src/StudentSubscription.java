import java.time.LocalDate;

/**
 * Студенческий абонемент.
 *
 * <p>Продаётся только студентам. Заморозка не больше {@value #MAX_FREEZE_DAYS} дней за раз.</p>
 */
public class StudentSubscription extends Subscription {
    public static final int MAX_FREEZE_DAYS = 7;

    /**
     * Создаёт студенческий абонемент.
     *
     * @param id      идентификатор
     * @param client  клиент, должен быть студентом
     * @param start   первый день действия
     * @param periods число месяцев
     * @throws SubscriptionException если клиент не студент
     */
    public StudentSubscription(SubscriptionId id, Client client, LocalDate start, int periods) {
        super(id, client, Tariff.STUDENT, start, periods);
        if (!client.student()) {
            throw new SubscriptionException(client.name() + " не студент");
        }
    }

    /**
     * Для студенческого абонемента заморозка разрешена не больше чем на 7 дней.
     */
    @Override
    protected void checkFreeze(int days) {
        if (days > MAX_FREEZE_DAYS) {
            throw new SubscriptionException("студенческий абонемент можно заморозить не больше чем на " + MAX_FREEZE_DAYS + " дней");
        }
    }
}
