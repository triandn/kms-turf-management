package com.kms.cntt.service;

import com.kms.cntt.model.Payment;
import com.kms.cntt.payload.general.ResponseDataAPI;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public interface PaymentService {
  ResponseDataAPI makePayment(UUID userId, String language, UUID scheduleId)
      throws UnsupportedEncodingException;

  Payment findByTxnRef(String txnRef);

  ResponseDataAPI paymentResult(String responseCode, String txnRef);
}
