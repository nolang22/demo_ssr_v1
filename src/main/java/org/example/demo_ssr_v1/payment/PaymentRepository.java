package org.example.demo_ssr_v1.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 기본 CRUD 만들어진 상태 ...

    // 포트원 결제 번호로 Payment 정보 조회 쿼리 자동 생성 됨.
//    Optional<Payment> findByImpUid(String impUid);

    // 우리 서버 주문번호로 정보 조회
//    Optional<Payment> findByMerchantImpUid(String impUid);

    // 우리 서버 주문번호로 조회 - 중복 주문 번호 확인 용 (T,F)
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.merchantUid = :merchantUid")
    boolean existsByMerchantUid(@Param("merchantUid") String merchantUid);


    Optional<Payment> findByImpUid(String impUid);

    @Query("""
        SELECT p FROM Payment p
        JOIN FETCH p.user
        WHERE p.user.id = :userId
        ORDER BY p.createdAt DESC 
""")
    List<Payment> findAllByUserId(@Param("userId") Long userId);

    // 추가
    @Query("""
          SELECT p from Payment p 
          JOIN FETCH p.user u
          WHERE p.id = :id
    """)
    Payment findByIdWithUser(@Param("id") Long paymentId);
}
