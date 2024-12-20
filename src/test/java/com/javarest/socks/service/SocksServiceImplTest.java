package com.javarest.socks.service;

import com.javarest.socks.dto.SocksRequest;
import com.javarest.socks.model.Socks;
import com.javarest.socks.repository.SocksRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SocksServiceImplTest {

    @Mock
    private SocksRepository repository;

    @InjectMocks
    private SocksServiceImpl service;

    // Helper objects for tests
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
}