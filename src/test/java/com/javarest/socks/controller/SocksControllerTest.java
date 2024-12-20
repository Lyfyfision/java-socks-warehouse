package com.javarest.socks.controller;

import com.javarest.socks.config.WebConfig;
import com.javarest.socks.dto.CottonPercentageFilter;
import com.javarest.socks.service.SocksService;
import com.javarest.socks.util.CottonPercentageFilterConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(SocksController.class)
@Import({WebConfig.class, CottonPercentageFilterConverter.class})
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
    @DisplayName("Should return 400 if invalid sorting direction provided")
    void shouldReturnBadRequestOnInvalidSortDirection() throws Exception {
        mockMvc.perform(get("/all")
                        .param("sortDirection", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "errorMsg": "Invalid sorting direction. Please pick 'asc' for ascending and 'desc' for descending"
                        }
                        """));
    }

    @Test
    @DisplayName("Should return 400 when invalid socks income request is provided")
    void shouldFailOnInvalidSocksIncomeRequest() throws Exception {
        mockMvc.perform(post("/income")
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
}
