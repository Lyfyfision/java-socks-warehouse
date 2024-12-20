package com.javarest.socks.controller;

import com.javarest.socks.dto.CottonPercentageFilter;
import com.javarest.socks.dto.SocksRequest;
import com.javarest.socks.exception.constant.ErrorMessage;
import com.javarest.socks.exception.exceptions.FileProcessingException;
import com.javarest.socks.model.Socks;
import com.javarest.socks.service.SocksService;
import com.javarest.socks.util.ExcelUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EmptyFileException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/socks")
@AllArgsConstructor
@Slf4j
public class SocksController {

    private final SocksService service;

    @Operation(summary = "Register the income of a new consignment of socks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Socks income successfully registered."),
            @ApiResponse(responseCode = "400", description = "Invalid request format or validation error."),
            @ApiResponse(responseCode = "500", description = "Unexpected error during registration.")
    })
    @PostMapping("/income")
    public ResponseEntity<String> registerSocksIncome(@RequestBody @Valid SocksRequest income) {
        service.registerSocksIncome(income);
        log.info("POST /income received, socks income successfully registered: {}", income);
        return ResponseEntity.ok("Socks income successfully registered.");
    }

    @Operation(summary = "Register the outcome of socks (socks are leaving stock)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Socks outcome successfully registered."),
            @ApiResponse(responseCode = "404", description = "Socks not found for the provided parameters."),
            @ApiResponse(responseCode = "400", description = "More socks requested than stock available."),
            @ApiResponse(responseCode = "500", description = "Unexpected error during operation.")
    })
    @PostMapping("/outcome")
    public ResponseEntity<String> registerSocksOutcome(@RequestBody @Valid SocksRequest outcome) {
        service.registerSocksOutcome(outcome);
        log.info("POST /outcome request received, socks outcome successfully registered: {}", outcome);
        return ResponseEntity.ok("Socks outcome successfully registered.");
    }

    @Operation(summary = "Register a batch of socks from an uploaded Excel file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Socks batch has been successfully registered."),
            @ApiResponse(responseCode = "400", description = "Provided file is empty or incorrectly formatted."),
            @ApiResponse(responseCode = "500", description = "Unexpected internal error while processing the file.")
    })
    @PostMapping("/batch")
    public ResponseEntity<String> registerBatchFromFile(
            @Parameter(description = "MultipartFile containing batch details in Excel format", required = true)
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.error("Error occurred during POST /batch request - File {} is empty.", file);
            throw new EmptyFileException();
        }

        try {
            List<SocksRequest> socksBatch = ExcelUtils.parseExcelFile(file.getResource().getFile());
            socksBatch.forEach(service::registerSocksIncome);
            log.info("POST /batch request received, socks from file {} has been added.", file);
            return ResponseEntity.ok("Socks batch has been successfully registered");
        } catch (Exception e) {
            log.error("Error occurred while processing the file {} with POST /batch request.", file);
            throw new FileProcessingException(ErrorMessage.FILE_PROCESSING.getMsg());
        }
    }

    @Operation(description = "Update socks information by ID in the stock database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Socks information has been successfully updated."),
            @ApiResponse(responseCode = "404", description = "Socks with the specified ID were not found."),
            @ApiResponse(responseCode = "400", description = "Invalid data provided in the request body."),
            @ApiResponse(responseCode = "500", description = "Unexpected server error while updating socks information.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> updateSocks(
            @Parameter(description = "ID of the socks entry to update", required = true, example = "123")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated data for the socks, " +
                    "including color, cotton percentage, and quantity", required = true)
            @RequestBody @Valid SocksRequest dto) {
        service.updateSocksById(id, dto);
        log.info("Socks information has been successfully updated through request - PUT /socks/{}: {}", id, dto);
        return ResponseEntity.ok("Socks information has been successfully updated.");
    }

    @Operation(summary = "Retrieve the total count of socks based on color and/or cotton percentage filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved socks count."),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters provided."),
            @ApiResponse(responseCode = "404", description = "No socks found matching the criteria."),
            @ApiResponse(responseCode = "500", description = "Server error.")
    })
    @GetMapping
    public ResponseEntity<Integer> getSocksCount(
            @Parameter(description = "Filter by color of socks", example = "blue")
            @RequestParam(required = false) String color,
            @Parameter(description = "Filter by cotton percentage of socks", example = "50")
            @RequestParam(required = false, name = "cottonPercentage") CottonPercentageFilter filter) {
        log.info("GET api/socks request received with parameters - color: {}, cottonPercentage: {}",
                color, filter);
        int socksCount = service.getSocksCount(color, filter);
        log.info("GET api/socks response: Retrieved {} socks.", socksCount);
        return ResponseEntity.ok(socksCount);
    }

    @Operation(summary = "Retrieve a list of all socks, optionally sorted and filtered",
            description = "This method allows filtering by color and/or a range of cotton percentages. Sorting by specific fields is also supported.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of socks."),
            @ApiResponse(responseCode = "400", description = "Invalid filter or sorting parameter provided."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping("/all")
    public ResponseEntity<List<Socks>> getSocksSorted(
            @Parameter(description = "Filter by color of socks. If not specified, all colors are included.", example = "red")
            @RequestParam(required = false) String color,
            @Parameter(description = "Filter parameters for cotton percentage (e.g., range or operator)", example = "<")
            @RequestParam(required = false, name = "cottonPercentage") CottonPercentageFilter filter,
            @Parameter(description = "Field by which to sort results (e.g., 'color', 'quantity').", example = "quantity")
            @RequestParam(required = false, name = "sortBy") String sortField,
            @Parameter(description = "Direction to sort: 'asc' for ascending or 'desc' for descending.", example = "asc")
            @RequestParam(required = false) String sortDirection
    ) {
        log.info("GET /all request received with parameters - color: {}, cottonPercentage: {}, sortBy: {}, sortDirection: {}",
                color, filter, sortField, sortDirection);
        List<Socks> socks = service.getAllSocksSorted(color, filter, sortField, sortDirection);
        log.info("GET /all response: Retrieved {} socks.", socks.size());
        return ResponseEntity.ok(socks);
    }
}
