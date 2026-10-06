/**
 * Идентификатор абонемента.
 *
 * <p>Неизменяемое значение. Гарантирует, что идентификатор не null
 * и не пустая строка. equals и hashCode сгенерированы record по значению.</p>
 *
 * @param id строковый идентификатор, не null и не пустой
 */
public record SubscriptionId(String id) {
    public SubscriptionId {
        if (id == null || id.isBlank()) {
            throw new SubscriptionException("некорректный ID");
        }
    }
}
