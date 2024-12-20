package com.javarest.socks.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "socks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Socks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String color;

    @Column(name = "cotton_percentage")
    private int cottonPercentage;

    private int quantity;
}
