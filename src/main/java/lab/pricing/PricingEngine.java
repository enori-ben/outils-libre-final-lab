package lab.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PricingEngine {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.08");
    private static final BigDecimal VIP_BASE_DISCOUNT = new BigDecimal("0.05");
    private static final Map<String, BigDecimal> CODE_DISCOUNTS = Map.of(
            "SAVE10", new BigDecimal("0.10"),
            "SAVE20", new BigDecimal("0.20"));

    public enum CustomerType {
        REGULAR,
        VIP
    }

    public record Item(BigDecimal unitPrice, int quantity) {
        public Item {
            if (unitPrice == null || unitPrice.signum() < 0) {
                throw new IllegalArgumentException("Unit price must be non-negative");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }
        }

        BigDecimal lineTotal() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public record Result(
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal taxableAmount,
            BigDecimal tax,
            BigDecimal finalPrice) {
    }

    public Result calculate(List<Item> items, CustomerType customerType, String discountCode) {
        Objects.requireNonNull(items, "items must not be null");
        Objects.requireNonNull(customerType, "customerType must not be null");
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must include at least one item");
        }

        BigDecimal subtotal = items.stream().map(Item::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discountRate = discountRate(customerType, discountCode);
        BigDecimal discountAmount = money(subtotal.multiply(discountRate));
        BigDecimal taxableAmount = money(subtotal.subtract(discountAmount));
        BigDecimal tax = money(taxableAmount.multiply(TAX_RATE));
        BigDecimal finalPrice = money(taxableAmount.add(tax));

        return new Result(money(subtotal), discountAmount, taxableAmount, tax, finalPrice);
    }

    private BigDecimal discountRate(CustomerType customerType, String discountCode) {
        BigDecimal customerDiscount = customerType == CustomerType.VIP ? VIP_BASE_DISCOUNT : BigDecimal.ZERO;
        String normalizedCode = discountCode == null ? "" : discountCode.trim().toUpperCase(Locale.ROOT);
        BigDecimal codeDiscount = CODE_DISCOUNTS.getOrDefault(normalizedCode, BigDecimal.ZERO);
        return customerDiscount.add(codeDiscount).min(new BigDecimal("0.40"));
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
