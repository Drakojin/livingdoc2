package org.livingdoc.engine.execution.examples.scenarios;

import static strikt.api.ExpectKt.expectThat;
import static strikt.assertions.AnyKt.isEqualTo;

import java.util.ArrayList;
import java.util.List;

import org.livingdoc.api.fixtures.scenarios.Before;
import org.livingdoc.api.fixtures.scenarios.Binding;
import org.livingdoc.api.fixtures.scenarios.Step;


public class SelfCheckoutScenarioFixture {

    SelfCheckout sut;

    @Before
    void before() {
        sut = new SelfCheckout();
    }

    @Step("when the customer scans a {product} for {price} cents")
    @Step("and a {product} for {price} cents")
    void scanProduct(String product, Integer price) {
        sut.add(product, price);
    }

    @Step("when the customer checks out, the total sum is {expectedSum}")
    void checkout(@Binding("expectedSum") Integer sum) {
        isEqualTo(expectThat(sut.checkout()), sum);
    }

    private static class SelfCheckout {

        List<LineItem> items = new ArrayList<>();

        private void add(String name, Integer priceInCents) {
            items.add(new LineItem(name, priceInCents));
        }

        private Integer checkout() {
            return items.stream().mapToInt(lineItem -> lineItem.priceInCents).sum();
        }

        private static class LineItem {
            String name;
            Integer priceInCents;

            LineItem(String name, Integer priceInCents) {
                this.name = name;
                this.priceInCents = priceInCents;
            }
        }

    }

}
