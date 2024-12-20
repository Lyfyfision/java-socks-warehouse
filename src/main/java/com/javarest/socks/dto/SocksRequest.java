package com.javarest.socks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;

@Data
@Builder
@Schema(description = "Model for socks request data, which contains basic info about color, cotton percentage, and quantity")
public class SocksRequest {

    @NotBlank(message = "Color must be provided")
    @NonNull
    @Schema(description = "Color of the socks", example = "red")
    private String color;

    @Min(value = 0, message = "Color percentage must be greater than 0")
    @Max(value = 100, message = "Color percentage must be lower than 100")
    @NonNull
    @Schema(description = "Cotton percentage of the socks", example = "80")
    private int cottonPercentage;

    @Positive(message = "Quantity must be a positive number")
    @NonNull
    @Schema(description = "Quantity of socks", example = "100")
    private int quantity;
}
