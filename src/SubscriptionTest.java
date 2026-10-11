import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class SubscriptionTest {

    private static final LocalDate START = LocalDate.of(2026, 3, 1);
    private static final LocalDate END = LocalDate.of(2026, 3, 31);
    private static final Client ANNA = new Client("Анна", true);
    private static final Client BORIS = new Client("Борис", false);

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testTariffCost();
        testYearlyCheaperThanMonthly();
        testEndDate();

        testActiveOnBoundaries();
        testZeroPeriods();
        testStudentOnlyForStudents();

        testRenewActive();
        testRenewExpired();

        testFreezeShiftsEnd();
        testFreezeZeroDays();
        testFreezeSingle();
        testFreezeRegularLimit();
        testFreezeRegularExactLimit();
        testRegularWrongTariff();
        testFreezeExpired();

        testUseExpired();
        testUseFrozen();
        testUseBeforeStart();

        testOperationsCopy();
        testSubscriptionIdBlank();
        testSubscriptionIdEqualsHashCode();

        System.out.println("\n=== ИТОГО ===");
        System.out.println("Пройдено: " + passed);
        System.out.println("Провалено: " + failed);
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static void check(String testName, ThrowingRunnable action, boolean shouldThrow) {
        try {
            action.run();
            if (shouldThrow) {
                System.out.println("[FAIL] " + testName + " — ожидалось исключение");
                failed++;
            } else {
                System.out.println("[OK]   " + testName);
                passed++;
            }
        } catch (Exception | AssertionError e) {
            if (shouldThrow && e instanceof Exception) {
                System.out.println("[OK]   " + testName + " (" + e.getClass().getSimpleName() + ")");
                passed++;
            } else {
                System.out.println("[FAIL] " + testName + " — неожиданное: " + e.getMessage());
                failed++;
            }
        }
    }

    private static Subscription monthly() {
        return new RegularSubscription(new SubscriptionId("S-1"), BORIS, Tariff.MONTHLY, START, 1);
    }

    private static void testTariffCost() {
        check("Тарифы: стоимость", () -> {
            assertEquals(1500, Tariff.SINGLE.cost(3));
            assertEquals(6000, Tariff.MONTHLY.cost(2));
            assertEquals(28800, Tariff.YEARLY.cost(1));
            assertEquals(1500, Tariff.STUDENT.cost(1));
        }, false);
    }

    private static void testYearlyCheaperThanMonthly() {
        check("Тарифы: годовой дешевле 12 месячных", () -> {
            assertTrue(Tariff.YEARLY.cost(1) < Tariff.MONTHLY.cost(12));
        }, false);
    }

    private static void testEndDate() {
        check("Тарифы: дата окончания", () -> {
            assertEquals(START, Tariff.SINGLE.endDate(START, 1));
            assertEquals(END, Tariff.MONTHLY.endDate(START, 1));
            assertEquals(LocalDate.of(2027, 2, 28), Tariff.YEARLY.endDate(START, 1));
        }, false);
    }

    private static void testActiveOnBoundaries() {
        check("Действует: первый и последний день — да, до и после — нет", () -> {
            Subscription s = monthly();
            assertTrue(!s.isActiveOn(START.minusDays(1)));
            assertTrue(s.isActiveOn(START));
            assertTrue(s.isActiveOn(END));
            assertTrue(!s.isActiveOn(END.plusDays(1)));
        }, false);
    }

    private static void testZeroPeriods() {
        check("Покупка на 0 периодов", () -> {
            new RegularSubscription(new SubscriptionId("S-2"), BORIS, Tariff.MONTHLY, START, 0);
        }, true);
    }

    private static void testStudentOnlyForStudents() {
        check("Студенческий не студенту", () -> {
            new StudentSubscription(new SubscriptionId("S-3"), BORIS, START, 1);
        }, true);
    }

    private static void testRenewActive() {
        check("Продление действующего — от даты окончания", () -> {
            Subscription s = monthly();
            s.renew(1, LocalDate.of(2026, 3, 20));
            assertEquals(LocalDate.of(2026, 4, 30), s.getEnd());
            assertEquals(6000, s.totalCost());
        }, false);
    }

    private static void testRenewExpired() {
        check("Продление просроченного — от даты продления", () -> {
            Subscription s = monthly();
            LocalDate date = LocalDate.of(2026, 5, 10);
            s.renew(1, date);
            assertEquals(date, s.getStart());
            assertEquals(LocalDate.of(2026, 6, 9), s.getEnd());
        }, false);
    }

    private static void testFreezeShiftsEnd() {
        check("Заморозка сдвигает окончание, в дни заморозки не действует", () -> {
            Subscription s = monthly();
            s.freeze(LocalDate.of(2026, 3, 10), 5);
            assertEquals(END.plusDays(5), s.getEnd());
            assertTrue(!s.isActiveOn(LocalDate.of(2026, 3, 14)));
            assertTrue(s.isActiveOn(LocalDate.of(2026, 3, 15)));
        }, false);
    }

    private static void testFreezeZeroDays() {
        check("Заморозка на 0 дней", () -> monthly().freeze(START, 0), true);
    }

    private static void testFreezeSingle() {
        check("Заморозка разового", () -> {
            new SingleSubscription(new SubscriptionId("S-4"), BORIS, START, 1).freeze(START, 1);
        }, true);
    }

    private static void testRegularWrongTariff() {
        check("Обычный абонемент с тарифом SINGLE", () -> {
            new RegularSubscription(new SubscriptionId("S-5"), BORIS, Tariff.SINGLE, START, 1);
        }, true);
    }

    private static void testFreezeRegularLimit() {
        check("Обычный: заморозка больше 30 дней", () -> monthly().freeze(START, 31), true);
    }

    private static void testFreezeRegularExactLimit() {
        check("Обычный: заморозка ровно на 30 дней", () -> monthly().freeze(START, 30), false);
    }

    private static void testFreezeExpired() {
        check("Заморозка просроченного", () -> monthly().freeze(END.plusDays(1), 3), true);
    }

    private static void testUseExpired() {
        check("Использование просроченного", () -> {
            monthly().use(END.plusDays(1));
        }, true);
    }

    private static void testUseFrozen() {
        check("Использование замороженного", () -> {
            Subscription s = monthly();
            s.freeze(LocalDate.of(2026, 3, 10), 5);
            s.use(LocalDate.of(2026, 3, 12));
        }, true);
    }

    private static void testUseBeforeStart() {
        check("Использование до начала срока", () -> monthly().use(START.minusDays(1)), true);
    }

    private static void testOperationsCopy() {
        check("История неизменяема снаружи", () -> {
            Subscription s = monthly();
            s.getOperations().add(new Operation(START, OperationType.VISIT, 0));
        }, true);
    }

    private static void testSubscriptionIdBlank() {
        check("SubscriptionId: пустая строка", () -> new SubscriptionId("   "), true);
    }

    private static void testSubscriptionIdEqualsHashCode() {
        check("SubscriptionId: equals/hashCode", () -> {
            SubscriptionId a = new SubscriptionId("S-1");
            SubscriptionId b = new SubscriptionId("S-1");
            assertTrue(a.equals(a));
            assertTrue(a.equals(b) && b.equals(a));
            assertEquals(a.hashCode(), b.hashCode());
            assertTrue(!a.equals(new SubscriptionId("S-2")));
            assertTrue(!a.equals(null));
            Set<SubscriptionId> set = new HashSet<>();
            set.add(a);
            set.add(b);
            assertEquals(1, set.size());
        }, false);
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("ожидалось " + expected + ", получено " + actual);
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("условие не выполнено");
        }
    }
}
