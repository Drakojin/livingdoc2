package org.livingdoc.engine.fixtures

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.livingdoc.engine.fixtures.FixtureFieldInjector.FixtureFieldInjectionException
import org.livingdoc.engine.fixtures.FixtureFieldInjector.NoTypeConverterFoundException
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import java.lang.reflect.Field

internal class FixtureFieldInjectorTest {

    val cut = FixtureFieldInjector(null)

    val fixture = TestFixture()

    @Test fun `field value can be injected`() {
        val field = getField("testField")
        cut.inject(field, fixture, "true")
        expectThat(getFieldValue(field)).isEqualTo(true)
        cut.inject(field, fixture, "false")
        expectThat(getFieldValue(field)).isEqualTo(false)
    }

    @Test fun `custom exception in case type converter could not be found`() {
        val field = getField("typeConverterNotFound")
        val exception = assertThrows(FixtureFieldInjectionException::class.java) {
            cut.inject(field, fixture, "foo")
        }
        expectThat(exception.cause).isA<NoTypeConverterFoundException>()
    }

    @ValueSource(strings = ["privateField", "protectedField", "publicField"])
    @ParameterizedTest fun `method visibility is ignored`(fieldName: String) {
        val field = getField(fieldName)
        cut.inject(field, fixture, "true")
        val fieldValue = getFieldValue(field)
        expectThat(fieldValue).isEqualTo(true)
    }

    fun getField(name: String): Field {
        return TestFixture::class.java.getDeclaredField(name)
    }

    fun getFieldValue(field: Field) = field.get(fixture)

    open class TestFixture {

        var testField: Boolean? = null

        private var privateField: Boolean? = null
        protected var protectedField: Boolean? = null
        var publicField: Boolean? = null

        var typeConverterNotFound: CustomType? = null
    }

    class CustomType
}
