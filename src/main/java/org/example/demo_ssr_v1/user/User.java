package org.example.demo_ssr_v1.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_ssr_v1.board.Role;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_tb")
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private String email;

    @CreationTimestamp
    private Timestamp createdAt;

    private String profileImage; // 추가

    /**
     * User(1) : UserRole(N)
     * User가 UserRole 리스트를 관리합니다 (단방향)
     * 실제 DB의 "user_role_tb" 테이블 user_id 라는 fk 컬럼 생깁니다.
     * <p>
     * CascadeType.All
     * - 운영 공동체 User 를 저장하면 Role 로 자동 저자되고,
     * Uesr 를 삭제하면 가지고 있던 Role ㄷ르도 다 같이 삭제됩니다.
     * (홍길동 (관리자, 일반사용자) 삭제하면 userRole 2가지 row도 자동 삭제 됩니다.
     * <p>
     * orphanRemoval = true
     * 리스와 DB의 동기화 입니다.
     * Java의 roles 리스에서 요소(Role)를 .remove() 하거나 .clear() 하면
     * DB에서 해당 데이터(DELETE)가 실제로 처리 됩니다.
     */
    // 나중에 다른 캐발자가 finById(쿼리 메서드 호출할 때 신경 쓸 필요없이 전부 role 까지 반환 해줌)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private List<UserRole> roles = new ArrayList<>();

    @Builder
    public User(Long id, String username, String password, String email, Timestamp createdAt, String profileImage) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
        this.profileImage = profileImage;
    }

    // 회원정보 수정 비즈니스 로직 추가
    // 추후 DTO 설계
    public void update(UserRequest.UpdateDTO updateDTO) {
        // 유효성 검사
        updateDTO.validate();

        this.password = updateDTO.getPassword();
        this.profileImage = updateDTO.getProfileImageFilename();

        // 더티 채킹 (변경 감지)
        // 트랜잭션이 끝나면 자동으로 update 쿼리 진행
    }

    // 회원 정보 소유자 확인 로직
    public boolean isOwner(Long userId) {
        return this.id.equals(userId);
    }

    // 새로운 역할을 추가하는 기능
    public void addRole(Role role) {
        this.roles.add(UserRole.builder().role(role).build());
    }

    // 해당 역할을 기자고 있는지 확인 하는 기능
    public boolean hasRole(Role role) {
        // roles (리스트)에 컬렉션이 없거나 비어 있으면 역할이 없는 것
        if (this.roles == null || this.roles.isEmpty()) {
            return false;
        }

        // 즉시 로딩이라서 바로 사용해도 LAZY 초기화 예외 안 터짐
        // any(어떤 것이든), Match(일치하다) 즉, 리스트 안에 있는 것들 중 단 하나라도 조건이 맞는게 있다면
        // true를 반환해라!!
        return this.roles.stream()
                .anyMatch(r -> r.getRole() == role);
    }

    // 관리자 인지 여부를 반환 합니다.
    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    // 템플릿에서 {{isAdmin}}... {{/isAdmin}} 형대로 사용하는 편의 메서드 설계
    public boolean getIsAdmin() {
        return isAdmin();
    }

    // 화면에 표시할 역할 문자열 제공
    // - ADMIN 이면 "ADMIN" 제공
    public String getRoleDisplay() {
        return isAdmin() ? "ADMIN" : "USER";
    }
}

