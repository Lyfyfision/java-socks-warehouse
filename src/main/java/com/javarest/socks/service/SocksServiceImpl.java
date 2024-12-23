package com.javarest.socks.service;

import com.javarest.socks.dto.CottonPercentageFilter;
import com.javarest.socks.dto.SocksRequest;
import com.javarest.socks.exception.constant.ErrorMessage;
import com.javarest.socks.exception.exceptions.*;
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

    /**
     * Retrieves a sorted list of socks based on the specified filter parameters.
     * The method handles different cases of filtering:
     * - If neither `color` nor `filter` is provided, it returns all socks.
     * - If only `color` is provided, it filters by color.
     * - If `filter` specifies a range (min and max values), it filters by the range.
     * - If `filter` specifies an operator (e.g., "<", ">", "="), it filters using the operator.
     *
     * @param color          The color of the socks to filter (optional).
     * @param filter         A CottonPercentageFilter object containing the percentage filter criteria (optional).
     *                       Can be a range or include a comparison operation.
     * @param sortField      The field by which to sort the socks (e.g., "color", "quantity").
     * @param sortDirection  The sort direction, either "asc" (ascending) or "desc" (descending).
     * @return A list of socks filtered and sorted according to the provided parameters.
     * @throws com.javarest.socks.exception.exceptions.UnsupportedOperatorException If the operator in the `filter` is unsupported.
     */
    @Override
    public List<Socks> getAllSocksSorted(String color, CottonPercentageFilter filter, String sortField, String sortDirection) {
        log.info("Received a socks Get request. Parameters: color={}, cottonPercentageFilter={}, sortField={}, sortDirection={}",
                color, filter, sortField, sortDirection);

        Sort sort = getSortOrder(sortField, sortDirection);

        if (filter == null && color == null) {
            return repository.findAll(sort);
        }

        if (filter == null) {
            return handleColorFilterOnly(color, sort);
        }

        if (filter.isRange()) {
            return handleRangeFilter(color, filter, sort);
        }

        return handleOperatorFilter(color, filter, sort);
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
                throw new UnsupportedOperatorException(ErrorMessage.UNSUPPORTED_OPERATOR.getMsg());
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
        if (!sortDirection.equalsIgnoreCase("DESC") && !sortDirection.equalsIgnoreCase("ASC")) {
            throw new InvalidSortDirectionException("Unsupported sorting direction - " + sortDirection);
        }
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortField);
    }

    private List<Socks> handleColorFilterOnly(String color, Sort sort) {
        return repository.findByColor(color, sort);
    }

    private List<Socks> handleRangeFilter(String color, CottonPercentageFilter filter, Sort sort) {
        if (color != null) {
            return repository.findByColorAndCottonPercentageBetween(color, filter.getMinValue(), filter.getMaxValue(), sort);
        }
        return repository.findByCottonPercentageBetween(filter.getMinValue(), filter.getMaxValue(), sort);
    }

    private List<Socks> handleOperatorFilter(String color, CottonPercentageFilter filter, Sort sort) {
        return switch (filter.getOperator()) {
            case ">" -> color != null
                    ? repository.findByColorAndCottonPercentageGreaterThan(color, filter.getMinValue(), sort)
                    : repository.findByCottonPercentageGreaterThan(filter.getMinValue(), sort);
            case "<" -> color != null
                    ? repository.findByColorAndCottonPercentageLessThan(color, filter.getMaxValue(), sort)
                    : repository.findByCottonPercentageLessThan(filter.getMaxValue(), sort);
            case "=" -> color != null
                    ? repository.findByColorAndCottonPercentage(color, filter.getMinValue(), sort)
                    : repository.findByCottonPercentage(filter.getMinValue(), sort);
            default -> throw new UnsupportedOperatorException(ErrorMessage.UNSUPPORTED_OPERATOR.getMsg());
        };
    }
}
