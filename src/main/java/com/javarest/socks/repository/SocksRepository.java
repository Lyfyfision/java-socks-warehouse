package com.javarest.socks.repository;

import com.javarest.socks.model.Socks;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocksRepository extends JpaRepository<Socks, Long> {

    Optional<Socks> findByColorAndCottonPercentage(String color, int cottonPercentage);

    @Override
    Optional<Socks> findById(Long id);

    List<Socks> findByColor(String color, Sort sort);
    List<Socks> findByColorAndCottonPercentageBetween(String color, int minPercentage, int maxPercentage, Sort sort);
    List<Socks> findAll(Sort sort);

    @Query("SELECT SUM(s.quantity) FROM Socks s WHERE s.color = :color AND s.cottonPercentage > :cottonPercentage")
    Optional<Integer> sumQuantityByColorAndCottonPercentageGreaterThan(@Param("color") String color, @Param("cottonPercentage") int cottonPercentage);

    @Query("SELECT SUM(s.quantity) FROM Socks s WHERE s.color = :color AND s.cottonPercentage < :cottonPercentage")
    Optional<Integer> sumQuantityByColorAndCottonPercentageLessThan(@Param("color") String color, @Param("cottonPercentage") int cottonPercentage);

    @Query("SELECT SUM(s.quantity) FROM Socks s WHERE s.color = :color AND s.cottonPercentage = :cottonPercentage")
    Optional<Integer> sumQuantityByColorAndCottonPercentage(@Param("color") String color, @Param("cottonPercentage") int cottonPercentage);

    @Query("SELECT SUM(s.quantity) FROM Socks s WHERE s.cottonPercentage BETWEEN :minCottonPercentage AND :maxCottonPercentage")
    Optional<Integer> sumQuantityByCottonPercentageBetween(@Param("minCottonPercentage") int minCottonPercentage, @Param("maxCottonPercentage") int maxCottonPercentage);

    @Query("SELECT SUM(s.quantity) FROM Socks s WHERE s.color = :color")
    Optional<Integer> sumQuantityByColor(@Param("color") String color);

    @Query("SELECT SUM(s.quantity) FROM Socks s WHERE s.color = :color AND s.cottonPercentage BETWEEN :minCottonPercentage AND :maxCottonPercentage")
    Optional<Integer> sumQuantityByColorAndCottonPercentageBetween(@Param("color") String color, @Param("minCottonPercentage") int minCottonPercentage, @Param("maxCottonPercentage") int maxCottonPercentage);

    @Query("SELECT SUM(s.quantity) FROM Socks s WHERE s.cottonPercentage > :cottonPercentage")
    Optional<Integer> sumQuantityByCottonPercentageGreaterThan(@Param("cottonPercentage") int cottonPercentage);

    @Query("SELECT SUM(s.quantity) FROM Socks s WHERE s.cottonPercentage < :cottonPercentage")
    Optional<Integer> sumQuantityByCottonPercentageLessThan(@Param("cottonPercentage") int cottonPercentage);

    @Query("SELECT SUM(s.quantity) FROM Socks s WHERE s.cottonPercentage = :cottonPercentage")
    Optional<Integer> sumQuantityByCottonPercentage(@Param("cottonPercentage") int cottonPercentage);
}
