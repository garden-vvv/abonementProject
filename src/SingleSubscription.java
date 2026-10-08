import java.time.LocalDate;

/**
 * Разовый абонемент.
 *
 * <p>Заморозка запрещена.</p>
 */
public class SingleSubscription extends Subscription {
    /**
     * Создаёт разовый абонемент.
     *
     * @param id      идентификатор
     * @param client  клиент
     * @param start   первый день действия
     * @param periods число дней
     */
    public SingleSubscription(SubscriptionId id, Client client, LocalDate start, int periods) {
        super(id, client, Tariff.SINGLE, start, periods);
    }

    /**
     * Для разового абонемента заморозка всегда запрещена.
     */
    @Override
    protected void checkFreeze(int days) {
        throw new SubscriptionException("разовый абонемент нельзя заморозить");
    }
}
