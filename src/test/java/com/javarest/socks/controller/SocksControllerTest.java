package com.javarest.socks.controller;

import com.javarest.socks.config.WebConfig;
import com.javarest.socks.dto.CottonPercentageFilter;
import com.javarest.socks.dto.SocksRequest;
import com.javarest.socks.exception.GlobalExceptionHandler;
import com.javarest.socks.exception.exceptions.InsufficientStockException;
import com.javarest.socks.service.SocksService;
import com.javarest.socks.util.CottonPercentageFilterConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(SocksController.class)
@Import({WebConfig.class, CottonPercentageFilterConverter.class, GlobalExceptionHandler.class})
@ImportAutoConfiguration(GlobalExceptionHandler.class)
class SocksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SocksService service;

    @Test
    @DisplayName("Request parameters converting into filter properly")
    void shouldHandleRequestParamAndConvertToCottonPercentageFilterObject() throws Exception {
        // Arrange
        int expectedMinPercent = 30;
        int expectedMaxPercent = 70;
        when(service.getSocksCount(eq("blue"), any(CottonPercentageFilter.class))).thenReturn(25);

        // Act
        mockMvc.perform(get("/api/socks")
                        .param("color", "blue")
                        .param("cottonPercentage", "30-70")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(25));

        // Assert
        ArgumentCaptor<CottonPercentageFilter> captor = ArgumentCaptor.forClass(CottonPercentageFilter.class);
        verify(service).getSocksCount(eq("blue"), captor.capture());

        CottonPercentageFilter filter = captor.getValue();
        assertEquals("range", filter.getOperator());
        assertEquals(expectedMinPercent, filter.getMinValue());
        assertEquals(expectedMaxPercent, filter.getMaxValue());
    }

    @Test
    @DisplayName("Should return 400 when invalid socks income request is provided")
    void shouldFailOnInvalidSocksIncomeRequest() throws Exception {
        mockMvc.perform(post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "color": "blue",
                                    "cottonPercentage": -10,
                                    "quantity": 0
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when requested outcome exceeds stock")
    void shouldReturnBadRequestForOverspendingOutcome() throws Exception {
        // Arrange
        SocksRequest invalidRequest = SocksRequest.builder()
                .color("red")
                .cottonPercentage(50)
                .quantity(50000)
                .build();
        doThrow(new InsufficientStockException("Not enough socks available."))
                .when(service).registerSocksOutcome(invalidRequest);

        // Act & Assert
        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "color": "red",
                                    "cottonPercentage": 50,
                                    "quantity": 50000
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "errorMsg": "Not enough socks in stock to fulfill your request."
                        }
                        """));
    }
}
