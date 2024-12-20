package com.javarest.socks.util;

import com.javarest.socks.dto.CottonPercentageFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CottonPercentageFilterConverterTest {

    private CottonPercentageFilterConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CottonPercentageFilterConverter();
    }

    @Test
    @DisplayName("Convert range to percentage properly")
    void shouldConvertStringWithRangeOperatorToCottonPercentageFilterObject() {
        // Arrange
        String input = "30-70";
        String expectedRangeOperator = "range";
        int expectedMinValue = 30;
        int expectedMaxValue = 70;

        // Act
        CottonPercentageFilter result = converter.convert(input);

        // Assert
        assertNotNull(result);
        assertEquals(expectedRangeOperator, result.getOperator());
        assertEquals(expectedMinValue, result.getMinValue());
        assertEquals(expectedMaxValue, result.getMaxValue());
        assertTrue(result.isRange());
    }

    @Test
    @DisplayName("Convert > operator properly")
    void shouldConvertStringWithGreaterThanOperatorToCottonPercentageFilterObject() {
        // Arrange
        String input = ">50";
        int expectedMinValue = 50;
        String expectedOperator = ">";

        // Act
        CottonPercentageFilter result = converter.convert(input);

        // Assert
        assertNotNull(result);
        assertEquals(expectedOperator, result.getOperator());
        assertEquals(expectedMinValue, result.getMinValue());
        assertNull(result.getMaxValue());
        assertFalse(result.isRange());
    }

    @Test
    @DisplayName("Throwing exception on wrong operator")
    void shouldThrowExceptionWhenInputStringHasInvalidFormat() {
        // Arrange
        String input = "invalid_filter";

        // Act and assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> converter.convert(input));
        assertEquals("Invalid operator in filter: invalid_filter", exception.getMessage());
    }
}
