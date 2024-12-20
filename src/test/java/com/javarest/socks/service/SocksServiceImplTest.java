package com.javarest.socks.service;

import com.javarest.socks.dto.SocksRequest;
import com.javarest.socks.exception.exceptions.InsufficientStockException;
import com.javarest.socks.model.Socks;
import com.javarest.socks.repository.SocksRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocksServiceImplTest {

    @Mock
    private SocksRepository repository;

    @InjectMocks
    private SocksServiceImpl service;

    private SocksRequest sampleRequest;

    private Socks sampleSocks;

    @BeforeEach
    void setUp() {
        sampleRequest = SocksRequest.builder()
                .color("red")
                .cottonPercentage(60)
                .quantity(50)
                .build();

        sampleSocks = Socks.builder()
                .id(1L)
                .color("red")
                .cottonPercentage(60)
                .quantity(50)
                .build();
    }

    @Test
    @DisplayName("Socks can be registered as income")
    void shouldRegisterNewSocksWithoutErrors() {
        // Arrange
        when(repository.findByColorAndCottonPercentage("red", 60)).thenReturn(Optional.empty());

        // Act
        service.registerSocksIncome(sampleRequest);

        // Assert
        verify(repository, times(1)).save(any(Socks.class));
    }

    @Test
    @DisplayName("Existing socks can be updated through income")
    void shouldUpdateExistingSocksQuantity() {
        // Arrange
        int expectedQuantity = 60;
        when(repository.findByColorAndCottonPercentage("red", 60)).thenReturn(Optional.of(sampleSocks));
        SocksRequest additionalSocks = SocksRequest.builder()
                .color("red")
                .cottonPercentage(60)
                .quantity(10)
                .build();

        // Act
        service.registerSocksIncome(additionalSocks);

        // Assert
        ArgumentCaptor<Socks> captor = ArgumentCaptor.forClass(Socks.class);
        verify(repository, times(1)).save(captor.capture());

        Socks updatedSocks = captor.getValue();
        assertEquals(expectedQuantity, updatedSocks.getQuantity());
    }

    @Test
    @DisplayName("Throwing exception when can't fulfill outcome request")
    void shouldThrowExceptionWhenOutcomeExceedsStock() {
        // Arrange
        when(repository.findByColorAndCottonPercentage("red", 60)).thenReturn(Optional.of(sampleSocks));
        SocksRequest outcomeRequest = SocksRequest.builder()
                .color("red")
                .cottonPercentage(60)
                .quantity(100)
                .build();

        // Act
        assertThrows(InsufficientStockException.class, () -> service.registerSocksOutcome(outcomeRequest));

        // Verify save was never called
        verify(repository, never()).save(any(Socks.class));
    }
}
