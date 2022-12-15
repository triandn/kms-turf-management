package com.kms.cntt.controller;

import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.security.annotations.CurrentUser;
import com.kms.cntt.security.oauth2.UserPrincipal;
import com.kms.cntt.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.UUID;

@RequestMapping("/api/v1/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {
  private final PaymentService paymentService;

  @PostMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<ResponseDataAPI> payment(
      @RequestParam(value = "language", defaultValue = "vn") String language,
      @PathVariable UUID id,
      @CurrentUser UserPrincipal userPrincipal)
      throws UnsupportedEncodingException {
    return ResponseEntity.ok(paymentService.makePayment(userPrincipal.getId(), language, id));
  }

  @GetMapping()
  public ResponseEntity<ResponseDataAPI> paymentResult(
      @RequestParam(value = "vnp_ResponseCode", defaultValue = "") String responseCode,
      @RequestParam(value = "vnp_TxnRef", defaultValue = "") String txnRef) {
    return ResponseEntity.ok(paymentService.paymentResult(responseCode, txnRef));
  }
}
