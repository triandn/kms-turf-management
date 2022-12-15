package com.kms.cntt.service.impl;

import com.kms.cntt.common.Common;
import com.kms.cntt.common.CommonFunction;
import com.kms.cntt.common.MessageConstant;
import com.kms.cntt.enums.PaymentStatus;
import com.kms.cntt.exception.NotFoundException;
import com.kms.cntt.model.Payment;
import com.kms.cntt.model.Schedule;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.repository.PaymentRepository;
import com.kms.cntt.repository.ScheduleRepository;
import com.kms.cntt.service.PaymentService;
import com.kms.cntt.service.ScheduleService;
import com.kms.cntt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImp implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final UserService userService;
  private final ScheduleService scheduleService;
  private final ScheduleRepository scheduleRepository;

  @Override
  public ResponseDataAPI makePayment(UUID userId, String language, UUID scheduleId)
      throws UnsupportedEncodingException {
    Schedule schedule = scheduleService.findById(scheduleId);
    if (schedule.getIsPayment()) {
      throw new NotFoundException(MessageConstant.PAYMENT_NOT_FOUND);
    }
    String returnUrl = "https://turf-management-kms.vercel.app/my-schedule";

    String vnp_Version = "2.1.0";
    String vnp_Command = "pay";

    String vnp_TxnRef = String.format("%08d", new SecureRandom().nextInt(10_000_000));
    String vnp_IpAddr = "127.0.0.1";
    String vnp_TmnCode = Common.VNP_CODE;
    String vnp_OrderInfo = schedule.getTitle();
    String orderType = "other";
    String locate = language;
    BigDecimal amount = schedule.getPrice().multiply(new BigDecimal("100"));

    Map vnp_Params = new HashMap<>();
    vnp_Params.put("vnp_Version", vnp_Version);
    vnp_Params.put("vnp_Command", vnp_Command);
    vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
    vnp_Params.put("vnp_Amount", String.valueOf(amount.longValue()));
    vnp_Params.put("vnp_CurrCode", "VND");
    vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
    vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
    vnp_Params.put("vnp_OrderType", orderType);
    vnp_Params.put("vnp_Locale", locate);
    vnp_Params.put("vnp_ReturnUrl", returnUrl);
    vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String vnp_CreateDate = formatter.format(calendar.getTime());
    vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

    calendar.add(Calendar.MINUTE, 30);
    String vnp_ExpireDate = formatter.format(calendar.getTime());
    vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
    List fieldNames = new ArrayList(vnp_Params.keySet());
    Collections.sort(fieldNames);
    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();
    Iterator iterator = fieldNames.iterator();
    while (iterator.hasNext()) {
      String fieldName = (String) iterator.next();
      String fieldValue = (String) vnp_Params.get(fieldName);
      if ((fieldValue != null) && (fieldValue.length() > 0)) {
        hashData.append(fieldName);
        hashData.append('=');
        hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
        query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
        query.append('=');
        query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
        if (iterator.hasNext()) {
          query.append('&');
          hashData.append('&');
        }
      }
    }
    String queryUrl = query.toString();
    String vnp_SecureHash = CommonFunction.hmacSha512(Common.VNP_HASH_SECRET, hashData.toString());
    queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
    String paymentUrl = Common.VNP_URL + "?" + queryUrl;

    Payment payment = new Payment();
    payment.setCreatedAt(Common.getCurrentDateTime());
    payment.setVnpOrderInfo(vnp_OrderInfo);
    payment.setOrderType(orderType);
    payment.setAmount(amount.divide(new BigDecimal("100")));
    payment.setLocate(locate);
    payment.setIpAddress(vnp_IpAddr);
    payment.setPaymentUrl(paymentUrl);
    payment.setStatus(PaymentStatus.WAITING);
    payment.setTxnRef(vnp_TxnRef);
    payment.setTimeOver(calendar.getTime());
    payment.setUser(userService.findById(userId));
    payment.setSchedule(schedule);
    paymentRepository.save(payment);
    return ResponseDataAPI.successWithoutMeta(payment);
  }

  @Override
  public Payment findByTxnRef(String txnRef) {
    return paymentRepository
        .findByTxnRef(txnRef)
        .orElseThrow(() -> new NotFoundException(MessageConstant.PAYMENT_NOT_FOUND));
  }

  @Override
  public ResponseDataAPI paymentResult(String responseCode, String txnRef) {
    Payment payment = this.findByTxnRef(txnRef);

    if (responseCode.equals("00")) {
      payment.setStatus(PaymentStatus.SUCCESS);
      payment.setUpdatedAt(Common.getCurrentDateTime());
      Schedule schedule = scheduleService.findById(payment.getSchedule().getId());
      schedule.setIsPayment(true);
      scheduleRepository.save(schedule);
    } else {
      payment.setStatus(PaymentStatus.FAILURE);
      payment.setUpdatedAt(Common.getCurrentDateTime());
    }
    Payment result = paymentRepository.save(payment);
    return ResponseDataAPI.successWithoutMeta(result);
  }
}
