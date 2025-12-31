package org.example.demo_ssr_v1.payment;

import lombok.Data;

public class PaymentResponse {

    @Data
    public static class PrepareDTO {
        private String merchantUid; // 생선된 우리 서버 주문번호
        private Integer amount; // 결제 금액
        private String impKey; // 프론트 REST API 키 (필수)

        public PrepareDTO(String merchantUid, Integer amount, String impKey) {
            this.merchantUid = merchantUid;
            this.amount = amount;
            this.impKey = impKey;
        }
    }
}
