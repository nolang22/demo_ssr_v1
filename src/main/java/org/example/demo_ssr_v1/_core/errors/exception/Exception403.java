package org.example.demo_ssr_v1._core.errors.exception;

/**
 * 403 Forbidden 권한 없음
 */
public class Exception403 extends RuntimeException {

    public Exception403(String msg) {
        super(msg);
    }

}
