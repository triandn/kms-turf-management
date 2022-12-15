package com.kms.cntt.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kms.cntt.payload.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
public final class CommonFunction {
  private static final String ERROR_FILE = "errors.yml";
  private static final String VALIDATION_FILE = "validations.yml";

  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  @SuppressWarnings("unchecked")
  public static ErrorResponse getExceptionError(String error) {
    ReadYAML readYAML = new ReadYAML();
    Map<String, Object> errors = readYAML.getValueFromYAML(ERROR_FILE);
    Map<String, Object> objError = (Map<String, Object>) errors.get(error);
    String code = (String) objError.get("code");
    String message = (String) objError.get("message");
    return new ErrorResponse(code, message);
  }

  public static String convertToSnakeCase(String input) {
    return input.replaceAll("([^_A-Z])([A-Z])", "$1_$2").toLowerCase();
  }

  @SuppressWarnings("unchecked")
  public static ErrorResponse getValidationError(String resource, String fieldName, String error) {
    if (fieldName.contains("[")) {
      fieldName = handleFieldName(fieldName);
    }

    ReadYAML readYAML = new ReadYAML();
    Map<String, Object> errors = readYAML.getValueFromYAML(VALIDATION_FILE);
    Map<String, Object> fields = (Map<String, Object>) errors.get(resource);
    Map<String, Object> objErrors = (Map<String, Object>) fields.get(fieldName);
    Map<String, Object> objError = (Map<String, Object>) objErrors.get(error);
    String code = (String) objError.get("code");
    String message = (String) objError.get("message");
    return new ErrorResponse(code, message);
  }

  public static String handleFieldName(String fieldName) {
    String index = fieldName.substring(fieldName.indexOf("[") + 1, fieldName.indexOf("]"));
    return fieldName.replaceAll(index, "");
  }

  public static Timestamp getCurrentDateTime() {
    Date date = new Date();
    return new Timestamp(date.getTime());
  }

  public static String convertToJSONString(Object ob) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(ob);
    } catch (Exception e) {
      log.error(e.getMessage());
      return null;
    }
  }

  public static Timestamp yyyyMMddHHmmSSFormat(String inputDate) throws ParseException {
    Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(inputDate);
    return new Timestamp(date.getTime());
  }

  public static String hmacSha512(String key, String message) {
    Mac sha512Hmac;
    String result;

    try {
      final byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
      sha512Hmac = Mac.getInstance("HmacSHA512");
      SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA512");
      sha512Hmac.init(keySpec);
      byte[] macData = sha512Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));

      // Can either base64 encode or put it right into hex
      result = Base64.getEncoder().encodeToString(macData);
      result = bytesToHex(macData);
      return result;
    } catch (InvalidKeyException | NoSuchAlgorithmException e) {
      e.printStackTrace();
    } finally {
      // Put any cleanup here
      System.out.println("Done");
    }
    return "";
  }

  public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }
}
