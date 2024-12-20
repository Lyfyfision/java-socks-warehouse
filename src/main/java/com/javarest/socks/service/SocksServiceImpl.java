package com.javarest.socks.service;

import com.javarest.socks.dto.CottonPercentageFilter;
import com.javarest.socks.dto.SocksRequest;
import com.javarest.socks.exception.constant.ErrorMessage;
import com.javarest.socks.exception.exceptions.InsufficientStockException;
import com.javarest.socks.exception.exceptions.NoFilterParametersException;
import com.javarest.socks.exception.exceptions.SocksNotFoundException;
import com.javarest.socks.model.Socks;
import com.javarest.socks.repository.SocksRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class SocksServiceImpl implements SocksService {

    private final SocksRepository repository;

    @Override
    public void registerSocksIncome(SocksRequest socks) {
        Socks existingSocks = repository.findByColorAndCottonPercentage(socks.getColor(), socks.getCottonPercentage())
                .orElse(null);

        if (existingSocks == null) {
            existingSocks = Socks.builder()
                    .color(socks.getColor())
                    .cottonPercentage(socks.getCottonPercentage())
                    .quantity(socks.getQuantity())
                    .build();
            log.info("New socks income: Color = {}, Cotton percentage = {}, Quantity = {}",
                    socks.getColor(), socks.getCottonPercentage(), socks.getQuantity());
        } else {
            int previousQuantity = existingSocks.getQuantity();
            existingSocks.setQuantity(existingSocks.getQuantity() + socks.getQuantity());
            log.warn("Existing socks consignment has been updated through income registration: Color = {}, " +
                    "Cotton percentage = {}, Previous quantity = {}, Insertion = {}, New quantity = {}",
                    socks.getColor(), socks.getCottonPercentage(), previousQuantity, socks.getQuantity(),
                    existingSocks.getQuantity());
        }

        repository.save(existingSocks);
    }

    @Override
    public void registerSocksOutcome(SocksRequest socks) {
        Socks existingSocks = repository.findByColorAndCottonPercentage(
                socks.getColor(), socks.getCottonPercentage()
        ).orElseThrow(() -> new SocksNotFoundException(ErrorMessage.SOCKS_NOT_FOUND.getMsg()));

        if (existingSocks.getQuantity() < socks.getQuantity()) {
            throw new InsufficientStockException(ErrorMessage.INSUFFICIENT_STOCK.getMsg());
        }

        existingSocks.setQuantity(existingSocks.getQuantity() - socks.getQuantity());
        repository.save(existingSocks);

        log.info("Socks outcome: Color = {}, Cotton percentage = {}, Quantity = {}",
                socks.getColor(), socks.getCottonPercentage(), socks.getQuantity());
    }

    @Override
    public void updateSocksById(Long id, SocksRequest updatedSocks) {
        Socks existingSocks = repository.findById(id)
                .orElseThrow(() -> new SocksNotFoundException("Socks with ID " + id + " was not found."));

        existingSocks.setColor(updatedSocks.getColor());
        existingSocks.setCottonPercentage(updatedSocks.getCottonPercentage());
        existingSocks.setQuantity(updatedSocks.getQuantity());

        repository.save(existingSocks);

        log.info("Socks consignment has been updated: ID = {}, Color = {}, Cotton percentage = {}, Quantity = {}",
                id, updatedSocks.getColor(), updatedSocks.getCottonPercentage(), updatedSocks.getQuantity());
    }

    @Override
    public List<Socks> getAllSocksSorted(String color, CottonPercentageFilter filter, String sortField, String sortDirection) {
        log.info("Получен запрос на получение носков. Параметры: color={}, cottonPercentageFilter={}, sortField={}, sortDirection={}",
                color, filter, sortField, sortDirection);

        Sort sort = getSortOrder(sortField, sortDirection);

        if (filter == null && color == null) {
            return repository.findAll(sort);
        }

        if (filter != null && filter.isRange()) {
            return repository.findByColorAndCottonPercentageBetween(
                    color, filter.getMinValue(), filter.getMaxValue(), sort
            );
        }

        if (color != null) {
            return repository.findByColor(color, sort);
        }

        return repository.findAll(sort);
    }

    /**
     * Retrieves the total quantity of socks based on filtering parameters: color and/or cotton percentage filter.
     *
     * @param color The color of the socks to filter by (optional). Pass null for no filtering by color.
     * @param cottonPercentageFilter Filter criteria for cotton percentage (optional). Pass null for no filtering.
     * @return The total quantity of socks that match the criteria.
     * @throws NoFilterParametersException Thrown when both filter parameters are missing.
     */
    public int getSocksCount(String color, CottonPercentageFilter cottonPercentageFilter) {
        log.info("Request received to count socks. Parameters: color={}, cottonPercentageFilter={}",
                color, cottonPercentageFilter);

        if((color == null || color.isBlank()) && cottonPercentageFilter == null) {
            throw new NoFilterParametersException("Zero filter parameters");
        }

        if (cottonPercentageFilter == null) {
            return getSumQuantityByColor(color);
        }

        return getSumQuantityByCottonPercentage(color, cottonPercentageFilter);
    }

    /**
     * Counts the total quantity of socks based on a given color.
     *
     * @param color The color of the socks to filter by.
     * @return The total quantity of socks that match the color.
     */
    private int getSumQuantityByColor(String color) {
        if (color == null || color.isBlank()) {
            log.warn("No color filter provided. Returning 0.");
            return 0;
        }
        int sumByColor = repository.sumQuantityByColor(color).orElse(0);
        log.info("Counted socks by color '{}'. Found {} socks.", color, sumByColor);
        return sumByColor;
    }

    /**
     * Counts the total quantity of socks based on cotton percentage, with filtering by color.
     *
     * @param color The color of the socks (optional).
     * @param cottonPercentageFilter The filter criteria for cotton percentage.
     * @return The total quantity of socks that match the filter criteria.
     * @see CottonPercentageFilter
     */
    private int getSumQuantityByCottonPercentage(String color, CottonPercentageFilter cottonPercentageFilter) {
        if (cottonPercentageFilter.isRange()) {
            log.info("Counted socks by cotton percentage and color - {}. Found {} socks.",
                    color, getSumQuantityByCottonRange(color, cottonPercentageFilter));
            return getSumQuantityByCottonRange(color, cottonPercentageFilter);
        } else {
            log.info("Count socks by match operator - {} and color - {}. Found {} socks.",
                    cottonPercentageFilter.getOperator(), color, getSumQuantityByCottonOperator(color, cottonPercentageFilter.getOperator(), cottonPercentageFilter.getMinValue()));
            return getSumQuantityByCottonOperator(color, cottonPercentageFilter.getOperator(), cottonPercentageFilter.getMinValue());
        }
    }

    /**
     * Counts the total quantity of socks in a specific range of cotton percentage, with optional color filtering.
     *
     * @param color The color of the socks to filter by (optional).
     * @param filter The cotton percentage range filter.
     * @return The total quantity of socks that match the criteria.
     * @see CottonPercentageFilter
     */
    private int getSumQuantityByCottonRange(String color, CottonPercentageFilter filter) {
        if (color == null) {
            return repository.sumQuantityByCottonPercentageBetween(filter.getMinValue(), filter.getMaxValue()).orElse(0);
        }
        return repository.sumQuantityByColorAndCottonPercentageBetween(color, filter.getMinValue(), filter.getMaxValue()).orElse(0);
    }

    /**
     * Counts the total quantity of socks using a comparison operator (> / < / =),
     * with optional color filtering.
     *
     * @param color The color of the socks to filter by (optional).
     * @param operator The comparison operator.
     * @param value The cotton percentage value to compare.
     * @return The total quantity of socks that match the criteria.
     */
    private int getSumQuantityByCottonOperator(String color, String operator, int value) {
        return switch (operator) {
            case ">" -> (color == null)
                    ? repository.sumQuantityByCottonPercentageGreaterThan(value).orElse(0)
                    : repository.sumQuantityByColorAndCottonPercentageGreaterThan(color, value).orElse(0);
            case "<" -> (color == null)
                    ? repository.sumQuantityByCottonPercentageLessThan(value).orElse(0)
                    : repository.sumQuantityByColorAndCottonPercentageLessThan(color, value).orElse(0);
            case "=" -> (color == null)
                    ? repository.sumQuantityByCottonPercentage(value).orElse(0)
                    : repository.sumQuantityByColorAndCottonPercentage(color, value).orElse(0);
            default -> {
                log.warn("Unsupported operator: {}", operator);
                throw new UnsupportedOperationException(ErrorMessage.UNSUPPORTED_OPERATOR.getMsg());
            }
        };
    }

    private Sort getSortOrder(String sortField, String sortDirection) {
        if (sortField == null || sortField.isBlank()) {
            sortField = "color";
        }
        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "ASC";
        }
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortField);
    }
}
