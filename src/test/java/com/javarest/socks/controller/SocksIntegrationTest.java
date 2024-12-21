package com.javarest.socks.controller;

import com.javarest.socks.dto.CottonPercentageFilter;
import com.javarest.socks.dto.SocksRequest;
import com.javarest.socks.model.Socks;
import com.javarest.socks.repository.SocksRepository;
import com.javarest.socks.service.SocksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SocksIntegrationTest {

    @Autowired
    private SocksRepository repository;

    @Autowired
    private SocksService service;

    @BeforeEach
    void setUp() {
        repository.save(Socks.builder()
                .color("red")
                .cottonPercentage(35)
                .quantity(15)
                .build());
        repository.save(Socks.builder()
                .color("white")
                .cottonPercentage(60)
                .quantity(10)
                .build());
        repository.save(Socks.builder()
                .color("yellow")
                .cottonPercentage(20)
                .quantity(43)
                .build());
    }

    @Test
    @DisplayName("Socks can be inserted in DB")
    void shouldInsertValidSocks() {
        // Assert
        String expectedColor = "violet";

        // Act
        Socks savedSock = repository.save(Socks.builder()
                .color("violet")
                .cottonPercentage(55)
                .quantity(150)
                .build());

        Socks sock = repository.findById(savedSock.getId()).orElse(null);

        // Assert
        assertNotNull(sock);
        assertEquals(expectedColor, sock.getColor());
    }

    @Test
    @DisplayName("Should sort socks by color in descending order")
    void shouldSortSocksByColorDescending() {
        // Act
        List<Socks> result = service.getAllSocksSorted(null, null, "color", "DESC");

        // Assert
        assertEquals(3, result.size());
        assertEquals("yellow", result.get(0).getColor());
        assertEquals("white", result.get(1).getColor());
        assertEquals("red", result.get(2).getColor());
    }

    @Test
    @DisplayName("Return right socks quantity after applying filter by cotton percentage")
    void shouldReturnFilteredByCottonSocksQuantity() {
        //Arrange
        int expectedQuantity = 25;

        //Act
        int actualQuantity = service.getSocksCount(null, new CottonPercentageFilter(">20"));

        //Assert
        assertEquals(expectedQuantity, actualQuantity);
    }

    @Test
    @DisplayName("Socks can be updated by id")
    void shouldUpdateSocksById() {
        //Arrange
        SocksRequest updatedSocks = SocksRequest.builder()
                .color("blue")
                .cottonPercentage(55)
                .quantity(20)
                .build();
        String expectedColor = "blue";

        //Act
        service.updateSocksById(1L, updatedSocks);
        Socks actualSocks = repository.findById(1L).orElse(null);

        //Assert
        assertNotNull(actualSocks);
        assertEquals(expectedColor, actualSocks.getColor());
    }
}
