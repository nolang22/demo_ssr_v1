package org.example.demo_ssr_v1.board;

/**
 * 사용자 권한(역할)을 나타내는 Enum 타입
 * - ADMIN : 괸라자
 * - MANAGER : 일반 사용자
 * - USER : 일반 사용자
 * 데이터의 범주화 할때 사용한다.
 * 레벨 1 ~ 무한대 ...
 * 1 ~ 5까지만 범주화 하고 싶을때
 */
public enum Role {
    ADMIN, USER
}
