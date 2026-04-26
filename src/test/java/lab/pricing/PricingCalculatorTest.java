package lab.pricing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PricingCalculatorTest {

    private final PricingEngine engine = new PricingEngine();

    @Test
    void calculatesRegularCustomerWithSave10() {
        List<PricingEngine.Item> items = List.of(
                new PricingEngine.Item(new BigDecimal("100.00"), 2),
                new PricingEngine.Item(new BigDecimal("50.00"), 1));

        PricingEngine.Result result = engine.calculate(items, PricingEngine.CustomerType.REGULAR, "SAVE10");

        assertEquals(new BigDecimal("250.00"), result.subtotal());
        assertEquals(new BigDecimal("25.00"), result.discountAmount());
        assertEquals(new BigDecimal("225.00"), result.taxableAmount());
        assertEquals(new BigDecimal("18.00"), result.tax());
        assertEquals(new BigDecimal("243.00"), result.finalPrice());
    }

    @Test
    void stacksVipAndCodeDiscount() {
        List<PricingEngine.Item> items = List.of(new PricingEngine.Item(new BigDecimal("80.00"), 1));

        PricingEngine.Result result = engine.calculate(items, PricingEngine.CustomerType.VIP, "SAVE20");

        assertEquals(new BigDecimal("20.00"), result.discountAmount());
        assertEquals(new BigDecimal("4.80"), result.tax());
        assertEquals(new BigDecimal("64.80"), result.finalPrice());
    }

    @Test
    void rejectsEmptyOrder() {
        assertThrows(IllegalArgumentException.class,
                () -> engine.calculate(List.of(), PricingEngine.CustomerType.REGULAR, "SAVE10"));
    }
}
