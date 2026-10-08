import java.time.LocalDate;

/**
 * Обычный абонемент: месячный или годовой.
 *
 * <p>Заморозка не больше {@value #MAX_FREEZE_DAYS} дней за раз.</p>
 */
public class RegularSubscription extends Subscription {
    public static final int MAX_FREEZE_DAYS = 30;

    /**
     * Создаёт месячный или годовой абонемент.
     *
     * @param id      идентификатор
     * @param client  клиент
     * @param tariff  MONTHLY или YEARLY
     * @param start   первый день действия
     * @param periods число месяцев или лет
     * @throws SubscriptionException если тариф не MONTHLY и не YEARLY
     */
    public RegularSubscription(SubscriptionId id, Client client, Tariff tariff, LocalDate start, int periods) {
        super(id, client, tariff, start, periods);
        if (tariff != Tariff.MONTHLY && tariff != Tariff.YEARLY) {
            throw new SubscriptionException("обычный абонемент бывает только месячный или годовой");
        }
    }

    /**
     * Для обычного абонемента заморозка разрешена не больше чем на 30 дней.
     */
    @Override
    protected void checkFreeze(int days) {
        if (days > MAX_FREEZE_DAYS) {
            throw new SubscriptionException("абонемент можно заморозить не больше чем на " + MAX_FREEZE_DAYS + " дней");
        }
    }
}
