import java.time.LocalDate;
import java.util.List;

public class Main {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 12);

    public static void main(String[] args) {
        System.out.println("=== ДЕМОНСТРАЦИЯ МОДЕЛИ АБОНЕМЕНТОВ ===");
        System.out.println("Сегодня в сценарии: " + TODAY + "\n");

        System.out.println("1. Клиенты");
        Client anna = new Client("Анна", true);
        Client boris = new Client("Борис", false);
        Client vika = new Client("Вика", true);
        Client gleb = new Client("Глеб", false);
        Client darya = new Client("Дарья", false);
        Client egor = new Client("Егор", true);
        Client zhanna = new Client("Жанна", false);
        Client zakhar = new Client("Захар", false);
        Client irina = new Client("Ирина", true);
        Client kirill = new Client("Кирилл", false);
        System.out.println("Создано 10 клиентов, студенты: Анна, Вика, Егор, Ирина.\n");

        System.out.println("2. Покупка абонементов");
        Subscription s1 = new StudentSubscription(new SubscriptionId("S-01"), anna, LocalDate.of(2026, 9, 1), 3);
        Subscription s2 = new RegularSubscription(new SubscriptionId("S-02"), boris, Tariff.MONTHLY, LocalDate.of(2026, 9, 15), 1);
        Subscription s3 = new StudentSubscription(new SubscriptionId("S-03"), vika, LocalDate.of(2026, 10, 1), 1);
        Subscription s4 = new RegularSubscription(new SubscriptionId("S-04"), gleb, Tariff.YEARLY, LocalDate.of(2026, 1, 10), 1);
        Subscription s5 = new RegularSubscription(new SubscriptionId("S-05"), darya, Tariff.MONTHLY, LocalDate.of(2026, 8, 1), 1);
        Subscription s6 = new StudentSubscription(new SubscriptionId("S-06"), egor, LocalDate.of(2026, 9, 20), 2);
        Subscription s7 = new SingleSubscription(new SubscriptionId("S-07"), zhanna, TODAY, 1);
        Subscription s8 = new RegularSubscription(new SubscriptionId("S-08"), zakhar, Tariff.MONTHLY, LocalDate.of(2026, 7, 1), 1);
        Subscription s9 = new RegularSubscription(new SubscriptionId("S-09"), irina, Tariff.YEARLY, LocalDate.of(2025, 11, 1), 1);
        Subscription s10 = new RegularSubscription(new SubscriptionId("S-10"), kirill, Tariff.MONTHLY, LocalDate.of(2026, 10, 1), 6);
        Subscription s11 = new SingleSubscription(new SubscriptionId("S-11"), boris, LocalDate.of(2026, 10, 20), 1);
        Subscription s12 = new SingleSubscription(new SubscriptionId("S-12"), darya, LocalDate.of(2026, 10, 5), 3);
        Subscription s13 = new RegularSubscription(new SubscriptionId("S-13"), gleb, Tariff.MONTHLY, LocalDate.of(2026, 10, 10), 3);
        Subscription s14 = new RegularSubscription(new SubscriptionId("S-14"), kirill, Tariff.YEARLY, LocalDate.of(2026, 6, 1), 2);
        Subscription s15 = new StudentSubscription(new SubscriptionId("S-15"), irina, LocalDate.of(2026, 10, 12), 1);
        Subscription s16 = new SingleSubscription(new SubscriptionId("S-16"), anna, LocalDate.of(2026, 10, 11), 1);
        Subscription s17 = new RegularSubscription(new SubscriptionId("S-17"), zhanna, Tariff.MONTHLY, LocalDate.of(2026, 11, 1), 1);
        Subscription s18 = new RegularSubscription(new SubscriptionId("S-18"), zakhar, Tariff.YEARLY, LocalDate.of(2024, 5, 1), 1);
        Subscription s19 = new StudentSubscription(new SubscriptionId("S-19"), egor, LocalDate.of(2026, 3, 1), 1);
        Subscription s20 = new RegularSubscription(new SubscriptionId("S-20"), vika, Tariff.MONTHLY, LocalDate.of(2026, 9, 25), 2);
        List<Subscription> all = List.of(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10,
                s11, s12, s13, s14, s15, s16, s17, s18, s19, s20);
        for (Subscription s : all) {
            printSubscription(s);
        }
        System.out.println("Создано 20 абонементов. Вместе с клиентами — 30 объектов.");
        try {
            new StudentSubscription(new SubscriptionId("S-XX"), boris, TODAY, 1);
        } catch (SubscriptionException e) {
            System.out.println("Отказ в покупке студенческого: " + e.getMessage());
        }
        System.out.println();

        System.out.println("3. Стоимость покупки");
        for (Tariff tariff : Tariff.values()) {
            System.out.println(tariff + ": 1 период = " + tariff.cost(1) + " руб., 3 периода = "
                    + tariff.cost(3) + " руб.");
        }
        System.out.println();

        System.out.println("4. Действует ли абонемент на " + TODAY);
        for (Subscription s : all) {
            System.out.println(s.getId().id() + ": " + (s.isActiveOn(TODAY) ? "да" : "нет"));
        }
        System.out.println();

        System.out.println("5. Заморозка на N дней (сдвиг даты окончания)");
        System.out.println("S-02 до:    " + s2.getStart() + " - " + s2.getEnd());
        s2.freeze(LocalDate.of(2026, 10, 13), 10);
        System.out.println("S-02 после: " + s2.getStart() + " - " + s2.getEnd() + " (заморозка 13.10 - 22.10)");
        System.out.println("S-02 действует 2026-10-15? " + s2.isActiveOn(LocalDate.of(2026, 10, 15)));
        System.out.println("S-02 действует 2026-10-24? " + s2.isActiveOn(LocalDate.of(2026, 10, 24)));
        s4.freeze(TODAY, 14);
        System.out.println("S-04 заморожен на 14 дней, окончание " + s4.getEnd());
        System.out.println();

        System.out.println("6. Заморозка: недопустимо для типа (непроверяемое)");
        tryFreeze(s7, TODAY, 3);
        tryFreeze(s10, TODAY, 40);
        tryFreeze(s1, TODAY, 10);
        tryFreeze(s5, TODAY, 5);
        System.out.println();

        System.out.println("7. Продление");
        System.out.println("S-02 (действует) до:    " + s2.getEnd());
        s2.renew(1, LocalDate.of(2026, 10, 20));
        System.out.println("S-02 (действует) после: " + s2.getEnd() + " — месяц добавлен после окончания");
        System.out.println("S-05 (истёк) до:    " + s5.getStart() + " - " + s5.getEnd());
        s5.renew(1, TODAY);
        System.out.println("S-05 (истёк) после: " + s5.getStart() + " - " + s5.getEnd() + " — отсчёт от даты продления");
        System.out.println();

        System.out.println("8. Использование (проверяемые исключения)");
        use(s1, TODAY);
        use(s7, TODAY);
        use(s4, LocalDate.of(2026, 10, 15));
        use(s8, TODAY);
        use(s12, TODAY);
        System.out.println();

        System.out.println("9. Некорректные данные (непроверяемые)");
        try {
            new SubscriptionId("   ");
        } catch (SubscriptionException e) {
            System.out.println("Поймано: " + e.getMessage());
        }
        try {
            new RegularSubscription(new SubscriptionId("S-YY"), boris, Tariff.MONTHLY, TODAY, 0);
        } catch (SubscriptionException e) {
            System.out.println("Поймано: " + e.getMessage());
        }
        try {
            s3.freeze(TODAY, -1);
        } catch (SubscriptionException e) {
            System.out.println("Поймано: " + e.getMessage());
        }
        System.out.println();

        System.out.println("10. История операций");
        printOperations(s2);
        printOperations(s8);
        System.out.println();

        System.out.println("ИТОГО ОПЛАЧЕНО");
        int total = 0;
        for (Subscription s : all) {
            total += s.totalCost();
        }
        System.out.println("По всем абонементам: " + total + " руб.");
    }

    private static void tryFreeze(Subscription s, LocalDate from, int days) {
        try {
            s.freeze(from, days);
            System.out.println(s.getId().id() + ": заморожен на " + days + " дн.");
        } catch (SubscriptionException e) {
            System.out.println(s.getId().id() + ": непроверяемое: " + e.getMessage());
        }
    }

    private static void use(Subscription s, LocalDate date) {
        String prefix = s.getId().id() + " " + date + ": ";
        try {
            s.use(date);
            System.out.println(prefix + "посещение отмечено");
        } catch (Exception e) {
            System.out.println(prefix + "проверяемое: " + e.getMessage());
            if (date.isAfter(s.getEnd())) {
                s.renew(1, date);
                System.out.println(prefix + "-> абонемент продлён до " + s.getEnd() + ", можно приходить");
            }
        }
    }

    private static void printSubscription(Subscription s) {
        System.out.println(s.getId().id() + " | " + s.getClient().name() + " | " + s.getTariff()
                + " | " + s.getStart() + " - " + s.getEnd() + " | " + s.totalCost() + " руб.");
    }

    private static void printOperations(Subscription s) {
        System.out.println("Операции по " + s.getId().id() + ":");
        for (Operation operation : s.getOperations()) {
            System.out.println("  " + operation.date() + " | " + operation.operationType() + " | " + operation.value());
        }
    }
}
