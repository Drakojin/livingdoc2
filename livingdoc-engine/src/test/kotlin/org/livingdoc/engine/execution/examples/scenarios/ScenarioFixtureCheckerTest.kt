package org.livingdoc.engine.execution.examples.scenarios

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import kotlin.reflect.KClass

internal class ScenarioFixtureCheckerTest {

    val classPrefix = "org.livingdoc.engine.execution.examples.scenarios.MalformedFixtures"
    val methodPrefix = "void $classPrefix\$"

    @Test fun `fixture must have default constructor`() {
        val errors = executeCheck(MalformedFixtures.NoDefaultConstructor::class)
        expectThat(errors).containsExactly("The fixture class <$classPrefix.NoDefaultConstructor> has no default constructor!")
    }

    @Test fun `before methods must not have parameters`() {
        val errors = executeCheck(MalformedFixtures.BeforeWithParameter::class)
        expectThat(errors).containsExactly("@Before method <${methodPrefix}BeforeWithParameter.before(java.lang.String)> has 1 parameter(s) - must not have any!")
    }

    @Test fun `before methods must not be static`() {
        val errors = executeCheck(MalformedFixtures.StaticBeforeMethod::class)
        expectThat(errors).containsExactly("@Before method <static ${methodPrefix}StaticBeforeMethod.before()> must not be static!")
    }

    @Test fun `step methods cannot share an alias`() {
        val errors = executeCheck(MalformedFixtures.StepMethodsWithSameAlias::class)
        expectThat(errors).containsExactly("Alias <step> is used multiple times!")
    }

    @Test fun `step methods must not be static`() {
        val errors = executeCheck(MalformedFixtures.StaticStepMethod::class)
        expectThat(errors).containsExactly("@Step method <static ${methodPrefix}StaticStepMethod.step()> must not be static!")
    }

    @Test fun `step templates and annotated methods must have same number of parameters`() {
        val errors = executeCheck(MalformedFixtures.MismatchingParameterNumber::class)
        expectThat(errors).containsExactly("Method <${methodPrefix}MismatchingParameterNumber.step()> is annotated with a step template which has wrong parameter count: 'step with {parameter}'")
    }

    @Test fun `after methods must not have parameters`() {
        val errors = executeCheck(MalformedFixtures.AfterWithParameter::class)
        expectThat(errors).containsExactly("@After method <${methodPrefix}AfterWithParameter.after(java.lang.String)> has 1 parameter(s) - must not have any!")
    }

    @Test fun `after methods must not be static`() {
        val errors = executeCheck(MalformedFixtures.StaticAfterMethod::class)
        expectThat(errors).containsExactly("@After method <static ${methodPrefix}StaticAfterMethod.after()> must not be static!")
    }

    private fun executeCheck(fixtureClass: KClass<*>): List<String> {
        val model = ScenarioFixtureModel(fixtureClass.java)
        return ScenarioFixtureChecker.check(model)
    }
}
