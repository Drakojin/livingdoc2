package org.livingdoc.engine.execution.examples.decisiontables;


import org.livingdoc.api.fixtures.decisiontables.BeforeRow;
import org.livingdoc.api.fixtures.decisiontables.Check;
import org.livingdoc.api.fixtures.decisiontables.Input;

import static strikt.api.ExpectKt.expectThat;
import static strikt.assertions.AnyKt.isEqualTo;


public class CalculatorDecisionTableFixture {

    Calculator sut;

    @BeforeRow
    void beforeRow() {
        sut = new Calculator();
    }

    @Input("a")
    Float valueA;
    Float valueB;

    @Input("b")
    void setValueB(Float valueB) {
        this.valueB = valueB;
    }

    @Check("a + b = ?")
    void checkSum(Float expectedValue) {
        Float result = sut.sum(valueA, valueB);
        isEqualTo(expectThat(result), expectedValue);
    }

    @Check("a - b = ?")
    void checkDiff(Float expectedValue) {
        Float result = sut.diff(valueA, valueB);
        isEqualTo(expectThat(result), expectedValue);
    }

    @Check("a * b = ?")
    void checkMultiply(Float expectedValue) {
        Float result = sut.multiply(valueA, valueB);
        isEqualTo(expectThat(result),expectedValue);
    }

    @Check("a / b = ?")
    void checkDivide(Float expectedValue) {
        Float result = sut.divide(valueA, valueB);
        isEqualTo(expectThat(result),expectedValue);
    }

    private static class Calculator {

        private Float sum(Float a, Float b) {
            return a + b;
        }

        private Float diff(Float a, Float b) {
            return a - b;
        }

        private Float multiply(Float a, Float b) {
            return a * b;
        }

        private Float divide(Float a, Float b) {
            return a / b;
        }

    }

}
