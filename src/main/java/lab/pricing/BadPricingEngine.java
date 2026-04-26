package lab.pricing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Intentionally poor baseline implementation kept for refactoring comparison.
 */
public class BadPricingEngine {

    public List<BigDecimal> doEverything(List<BigDecimal> prices, List<Integer> qty, String ct, String dc) {
        if (prices == null || qty == null || prices.size() != qty.size()) {
            throw new RuntimeException("bad input");
        }
        BigDecimal subtotal = BigDecimal.ZERO;
        for (int i = 0; i < prices.size(); i++) {
            subtotal = subtotal.add(prices.get(i).multiply(BigDecimal.valueOf(qty.get(i))));
        }

        BigDecimal discountRate = BigDecimal.ZERO;
        if ("VIP".equals(ct)) {
            discountRate = discountRate.add(new BigDecimal("0.05"));
        }
        if ("SAVE10".equals(dc)) {
            discountRate = discountRate.add(new BigDecimal("0.10"));
        } else if ("SAVE20".equals(dc)) {
            discountRate = discountRate.add(new BigDecimal("0.20"));
        }

        BigDecimal discount = subtotal.multiply(discountRate);
        BigDecimal tax = subtotal.subtract(discount).multiply(new BigDecimal("0.08"));
        BigDecimal total = subtotal.subtract(discount).add(tax);

        List<BigDecimal> out = new ArrayList<>();
        out.add(subtotal);
        out.add(discount);
        out.add(tax);
        out.add(total);
        return out;
    }
}
