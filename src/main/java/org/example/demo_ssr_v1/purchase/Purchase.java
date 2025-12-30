package org.example.demo_ssr_v1.purchase;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1.board.Board;
import org.example.demo_ssr_v1.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "purchase_tb",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_board", columnNames = {"user_id", "board_id"})
        })
@NoArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 단방향 관계 : Purchase : User (N : 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // 구매 시 지불한 포인트
    private Integer price;

    @CreationTimestamp
    private Timestamp timestamp;

    @Builder
    public Purchase(User user, Board board, Integer price) {
        this.user = user;
        this.board = board;
        this.price = price;
    }
}
