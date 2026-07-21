package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFoundException_shouldReturnNotFound() {
        NotFoundException exception = new NotFoundException("Item not found");

        ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo("Item not found");
    }

    @Test
    void handleNotFoundException_shouldHaveCorrectStatus() {
        NotFoundException exception = new NotFoundException("Item not found");

        ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo("Item not found");
    }

    @Test
    void handleValidationException_shouldReturnBadRequest() {
        ValidationException exception = new ValidationException("Validation error");

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo("Validation error");
    }

    @Test
    void handleEmailException_shouldReturnConflict() {
        EmailException exception = new EmailException("Email already exists");

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo("Email already exists");
    }

    @Test
    void handleAccessException_shouldReturnForbidden() {
        AccessException exception = new AccessException("Access denied");

        ErrorResponse response = errorHandler.handleAccessException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo("Access denied");
    }

    @Test
    void handleNotFoundException_withNullMessage_shouldReturnNullError() {
        NotFoundException exception = new NotFoundException(null);

        ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isNull();
    }

    @Test
    void handleValidationException_withNullMessage_shouldReturnNullError() {
        ValidationException exception = new ValidationException(null);

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isNull();
    }

    @Test
    void handleEmailException_withNullMessage_shouldReturnNullError() {
        EmailException exception = new EmailException(null);

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isNull();
    }

    @Test
    void handleAccessException_withNullMessage_shouldReturnNullError() {
        AccessException exception = new AccessException(null);

        ErrorResponse response = errorHandler.handleAccessException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isNull();
    }

    @Test
    void handleValidationException_withEmailException_shouldReturnConflictMessage() {
        String message = "Email is already registered";
        EmailException exception = new EmailException(message);

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo(message);
    }

    @Test
    void handleValidationException_withValidationException_shouldReturnBadRequestMessage() {
        String message = "Invalid input data";
        ValidationException exception = new ValidationException(message);

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo(message);
    }
}