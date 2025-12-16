package org.example.demo_ssr_v1.board;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 자동 제공 메서드 (별도 구현 없이 사용 가능)
    // - save(T entity) : (Insert 또는 update)
    // - findById(Id id) : ID로 엔티티 조회 (Optional<T> 변환)
    // - findAll()
    // - deleteById(Id id) : ID로 엔티티 삭제
    // - count() : 존제 개수 조회
    // - existsByID(Id id) : ID 존재 여부 확인

    // 전제 조회
    // SELECT * FROM board_tb ORDER BY created_at DESC
    List<Board> findAllByOrderByCreatedAtDesc();


}
