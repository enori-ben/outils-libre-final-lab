package lab.pricing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class App {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: <CUSTOMER_TYPE> <DISCOUNT_CODE|NONE> <price>x<qty> [<price>x<qty> ...]");
            System.exit(1);
        }

        PricingEngine.CustomerType customerType = PricingEngine.CustomerType.valueOf(args[0].trim().toUpperCase());
        String discountCode = "NONE".equalsIgnoreCase(args[1]) ? "" : args[1];

        List<PricingEngine.Item> items = new ArrayList<>();
        for (int i = 2; i < args.length; i++) {
            String[] parts = args[i].split("x");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid item format: " + args[i]);
            }
            items.add(new PricingEngine.Item(new BigDecimal(parts[0]), Integer.parseInt(parts[1])));
        }

        PricingEngine.Result result = new PricingEngine().calculate(items, customerType, discountCode);
        System.out.printf(
                "subtotal=%s discount=%s tax=%s final=%s%n",
                result.subtotal(),
                result.discountAmount(),
                result.tax(),
                result.finalPrice());
    }
}
