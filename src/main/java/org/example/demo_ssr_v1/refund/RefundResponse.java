package org.example.demo_ssr_v1.refund;

import lombok.Data;
import org.example.demo_ssr_v1._core.utiles.MyDateUtil;

public class RefundResponse {

    @Data
    public static class ListDTO {
        private Long id; // 환불 요청 테이블 PK
        private Long paymentId;
        private Integer amount;
        private String reason; // 본인이 작성한 환불 사유
        private String rejectReason; // 괸리자가 환불 거절 사유 , (승인은 필요 없음)
        private String statusDisplay; // 화면 표시용 (대기중, 승인, 거절)

        // 상태별 플래그 변수 사용 (화면 표시용)
        private boolean isPending; // 대기중
        private boolean isApproved; // 승인 완료
        private boolean isRejected; // 거절

        public ListDTO(RefundRequest refund) {
            this.id = refund.getId();
            this.paymentId = refund.getPayment().getId();
            this.amount = refund.getPayment().getAmount();
            this.reason = refund.getReason();
            this.rejectReason = refund.getRejectReason() == null ? "" : refund.getRejectReason();

            switch (refund.getStatus()) {
                case PENDING -> this.statusDisplay = "대기중";
                case APPROVED -> this.statusDisplay = "승인됨";
                case REJECTED -> this.statusDisplay = "거절됨";
            }
            this.isPending = (refund.getStatus() == RefundStatus.PENDING);
            this.isApproved = (refund.getStatus() == RefundStatus.APPROVED);
            this.isRejected = (refund.getStatus() == RefundStatus.REJECTED);
        }
    }

    @Data
    public static class AdminListDTO {
        private Long id; // 환불 요청 테이블 PK
        private String username;
        private Long paymentId; // 결제 PK
        private String impUid; // 포트원으로 승인 요청 할 때
        private String merchantUid; // 주문 번호 (가맹점)
        private Integer amount;
        private String requestAt; // 환불 요청 일시
        private RefundStatus status; // 환불 상태
        private String statusDisplay; // 머스테치용 표시
        private String reason; // 본인이 작성한 환불 사유
        private String rejectReason; // 괸리자가 환불 거절 사유 , (승인은 필요 없음)

        private boolean isPending; // 대기중
        private boolean isApproved; // 승인 완료
        private boolean isRejected; // 거절

        public AdminListDTO(RefundRequest refundRequest) {
            // 우리는 한방에 User와 Payment 가지고 옴 ()
            this.id = refundRequest.getId();
            this.username = refundRequest.getUser().getUsername();
            this.paymentId = refundRequest.getPayment().getId();
            this.impUid = refundRequest.getPayment().getImpUid();
            this.merchantUid = refundRequest.getPayment().getMerchantUid();
            this.amount = refundRequest.getPayment().getAmount();
            this.status = refundRequest.getStatus();
            this.reason = refundRequest.getReason();
            this.rejectReason = refundRequest.getRejectReason();

            // 날짜 포맷팅
            // refundRequest.getCreatedAt() -> pc -> DB
            // 테스트 -> 샘플을 직접 insert 처리
            if (refundRequest.getCreatedAt() != null) {
                this.requestAt = MyDateUtil.timestampFormat(refundRequest.getCreatedAt());
            }

            // 변환 -> 대기중/승인됨/거절됨
            // 스위치 표현식 사용 - jdk 14버전 이상 부터 사용 가능
            switch (refundRequest.getStatus()) {
                case PENDING -> this.statusDisplay = "대기중";
                case APPROVED -> this.statusDisplay = "승인됨";
                case REJECTED -> this.statusDisplay = "거절됨";
            }

            this.isPending = (refundRequest.getStatus() == RefundStatus.PENDING);
            this.isApproved = (refundRequest.getStatus() == RefundStatus.APPROVED);
            this.isRejected = (refundRequest.getStatus() == RefundStatus.REJECTED);
        }
    }
}
