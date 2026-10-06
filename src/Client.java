/**
 * Клиент.
 *
 * @param name    имя клиента, не пустое
 * @param student является ли клиент студентом
 */
public record Client(String name, boolean student) {
    public Client {
        if (name == null || name.isBlank()) {
            throw new SubscriptionException("имя клиента обязательно");
        }
    }
}
