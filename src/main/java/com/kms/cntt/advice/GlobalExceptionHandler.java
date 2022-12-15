package com.kms.cntt.advice;

import com.kms.cntt.common.CommonFunction;
import com.kms.cntt.common.MessageConstant;
import com.kms.cntt.exception.BadRequestException;
import com.kms.cntt.exception.ForbiddenException;
import com.kms.cntt.exception.NotFoundException;
import com.kms.cntt.exception.UnauthorizedException;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import java.rmi.ServerError;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private static final String SERVER_ERROR_CODE = "ERR.SERVER";
  private static final String ACCESS_DENIED_ERROR = "forbidden_error";

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ResponseDataAPI> notFoundException(
      NotFoundException ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ResponseDataAPI> badRequestException(
      BadRequestException ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ResponseDataAPI> forbiddenException(
      ForbiddenException ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ResponseDataAPI> unauthorizedException(
      UnauthorizedException ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ServerError.class)
  public ResponseEntity<ResponseDataAPI> serverError(ServerError ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    String msg =
        ex.getName()
            + " should be of type "
            + Objects.requireNonNull(ex.getRequiredType()).getName();

    ErrorResponse error = new ErrorResponse(SERVER_ERROR_CODE, msg);
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ResponseDataAPI> accessDeniedExceptionHandle(
      AccessDeniedException ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ACCESS_DENIED_ERROR);
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.FORBIDDEN);
  }

  //  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResponseDataAPI> globalExceptionHandler(
      Exception ex, HttpServletRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    List<ObjectError> listError = ex.getBindingResult().getAllErrors();
    ObjectError objectError = listError.get(listError.size() - 1);
    String error = CommonFunction.convertToSnakeCase(Objects.requireNonNull(objectError.getCode()));
    String fieldName = CommonFunction.convertToSnakeCase(((FieldError) objectError).getField());
    String resource = CommonFunction.convertToSnakeCase(objectError.getObjectName());

    ErrorResponse errorResponse = CommonFunction.getValidationError(resource, fieldName, error);

    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(errorResponse);

    return new ResponseEntity<>(responseDataAPI, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    ErrorResponse error = CommonFunction.getExceptionError(MessageConstant.PAGE_NOT_FOUND);
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.NOT_FOUND);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    ErrorResponse error = new ErrorResponse(SERVER_ERROR_CODE, ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getMethod());
    builder.append(" method is not supported for this request. Supported methods are ");
    Objects.requireNonNull(ex.getSupportedHttpMethods())
        .forEach(t -> builder.append(t).append(" "));

    ErrorResponse error = new ErrorResponse(SERVER_ERROR_CODE, builder.toString());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);

    return new ResponseEntity<>(responseDataAPI, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));

    ErrorResponse error = new ErrorResponse(SERVER_ERROR_CODE, builder.toString());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    ErrorResponse error = new ErrorResponse(SERVER_ERROR_CODE, ex.getMessage());
    ResponseDataAPI responseDataAPI = ResponseDataAPI.error(error);
    return new ResponseEntity<>(responseDataAPI, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
