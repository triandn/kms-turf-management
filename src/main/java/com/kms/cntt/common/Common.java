package com.kms.cntt.common;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public final class Common {
  public static final String SUCCESS = "success";
  public static final String FAILURE = "failure";
  public static final String EXCHANGE_NAME_PREFIX = "notifications/kms/";
  public static final String TOKEN_SECRET = "token_secret";
  public static final String REFRESH_TOKEN_SECRET = "refresh_token_secret";
  public static final String VNP_CODE = "JWSKG3D4";
  public static final String VNP_HASH_SECRET = "QLERTFWCDDPVBGTGODGTMSLWISDOSRYD";
  public static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
  public static final int TOKEN_EXPIRATION = 7200000;
  public static final int REFRESH_TOKEN_EXPIRATION = 1296000000;
  public static final String RULE_ROLE = "ROLE_USER|ROLE_ADMIN|ROLE_REFEREE";
  public static final String RULE_TURF_TYPE = "FIVE_SIDE|SEVEN_SIDE";
  public static final String RULE_PASSWORD = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,32})";
  public static String generateQueueName(UUID userId) {
    return generateCode(64) + "|" + userId;
  }

  public static String generateCode(int length) {
    List<CharacterRule> rules =
        Arrays.asList(
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1));
    PasswordGenerator generator = new PasswordGenerator();
    return generator.generatePassword(length, rules);
  }

  public static Timestamp getCurrentDateTime() {
    Date date = new Date();
    return new Timestamp(date.getTime());
  }
}
