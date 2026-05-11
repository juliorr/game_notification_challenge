package com.globalli.notifications.api;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleValidationReturns400WithFieldViolations() throws NoSuchMethodException {
    BindingResult bindingResult = new BeanPropertyBindingResult(new SamplePayload(), "payload");
    bindingResult.rejectValue("name", "NotBlank", "must not be blank");
    MethodParameter parameter =
        new MethodParameter(SamplePayload.class.getDeclaredMethod("setName", String.class), 0);
    MethodArgumentNotValidException ex =
        new MethodArgumentNotValidException(parameter, bindingResult);

    ResponseEntity<ApiError> response = handler.handleValidation(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    ApiError body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.code()).isEqualTo("VALIDATION_FAILED");
    assertThat(body.errors())
        .singleElement()
        .satisfies(
            v -> {
              assertThat(v.field()).isEqualTo("name");
              assertThat(v.message()).isEqualTo("must not be blank");
            });
  }

  @Test
  void handleConstraintViolationReturns400WithViolations() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Set<ConstraintViolation<SamplePayload>> violations = validator.validate(new SamplePayload());
    ConstraintViolationException ex = new ConstraintViolationException(violations);

    ResponseEntity<ApiError> response = handler.handleConstraintViolation(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    ApiError body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.code()).isEqualTo("CONSTRAINT_VIOLATION");
    assertThat(body.errors()).isNotEmpty();
  }

  @Test
  void handleBadRequestReturns400ForIllegalArgument() {
    ResponseEntity<ApiError> response =
        handler.handleBadRequest(new IllegalArgumentException("bad input"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    ApiError body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.code()).isEqualTo("BAD_REQUEST");
    assertThat(body.message()).isEqualTo("bad input");
  }

  @Test
  void handleBadRequestReturns400ForUnreadableMessage() {
    ResponseEntity<ApiError> response =
        handler.handleBadRequest(new HttpMessageNotReadableException("malformed json"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().code()).isEqualTo("BAD_REQUEST");
  }

  @Test
  void handleNotFoundReturns404() {
    ResponseEntity<ApiError> response =
        handler.handleNotFound(new NoSuchElementException("user 42 missing"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    ApiError body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.code()).isEqualTo("NOT_FOUND");
    assertThat(body.message()).isEqualTo("user 42 missing");
  }

  @Test
  void handleUnexpectedReturns500WithGenericMessage() {
    ResponseEntity<ApiError> response = handler.handleUnexpected(new RuntimeException("kaboom"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    ApiError body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.code()).isEqualTo("INTERNAL_ERROR");
    assertThat(body.message()).isEqualTo("Unexpected error");
  }

  static class SamplePayload {
    @NotBlank private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
