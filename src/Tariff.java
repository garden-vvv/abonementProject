import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Тариф абонемента.
 *
 * <p>Каждая константа сама считает стоимость в своём теле (метод {@link #cost(int)}).
 * Стоимость в рублях.</p>
 */
public enum Tariff {
    SINGLE(ChronoUnit.DAYS) {
        @Override
        public int cost(int periods) {
            return 500 * periods;
        }
    },
    MONTHLY(ChronoUnit.MONTHS) {
        @Override
        public int cost(int periods) {
            return 3000 * periods;
        }
    },
    YEARLY(ChronoUnit.YEARS) {
        @Override
        public int cost(int periods) {
            return 3000 * 12 * periods * 80 / 100;
        }
    },
    STUDENT(ChronoUnit.MONTHS) {
        @Override
        public int cost(int periods) {
            return 3000 * periods / 2;
        }
    };

    private final ChronoUnit unit;

    Tariff(ChronoUnit unit) {
        this.unit = unit;
    }

    /**
     * Считает стоимость указанного числа периодов.
     *
     * @param periods число периодов
     * @return стоимость в рублях
     */
    public abstract int cost(int periods);

    /**
     * Считает последний день действия абонемента.
     *
     * @param start   первый день действия
     * @param periods число периодов
     * @return последний день действия (включительно)
     */
    public LocalDate endDate(LocalDate start, int periods) {
        return start.plus(periods, unit).minusDays(1);
    }
}
