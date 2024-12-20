package com.javarest.socks.exception.constant;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    INSUFFICIENT_STOCK("Not enough socks in stock to fulfill your request."),

    INVALID_FILE_FORMAT("Please upload a valid Excel file."),

    FILE_PROCESSING("There was an error processing the file."),

    SOCKS_NOT_FOUND("Socks was not found."),

    UNSUPPORTED_OPERATOR("Unsupported match operator. The correct operators are <, >, =."),

    EMPTY_FILE("File must contain any data."),

    NO_FILTERS("Please provide filter parameters like socks color and/or cotton percentage to continue."),

    GENERIC_ERROR("An unexpected error occurred. Please stand by and wait 2-3 minutes before new try.");

    private final String msg;

    ErrorMessage(String msg) {
        this.msg = msg;
    }
}
