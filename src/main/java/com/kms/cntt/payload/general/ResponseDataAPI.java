package com.kms.cntt.payload.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kms.cntt.common.Common;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDataAPI {

  private String status;

  private Object data;

  private Object error;

  private Object meta;

  public static ResponseDataAPI success(Object data, Object meta) {
    return ResponseDataAPI.builder().status(Common.SUCCESS).data(data).meta(meta).build();
  }

  public static ResponseDataAPI successWithoutMeta(Object data) {
    return ResponseDataAPI.builder().status(Common.SUCCESS).data(data).build();
  }

  public static ResponseDataAPI error(Object error) {
    return ResponseDataAPI.builder().status(Common.FAILURE).error(error).build();
  }
}
