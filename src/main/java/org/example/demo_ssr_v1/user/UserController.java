package org.example.demo_ssr_v1.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._core.errors.exception.Exception401;
import org.example.demo_ssr_v1.purchase.PurchaseResponse;
import org.example.demo_ssr_v1.purchase.PurchaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 Controller (표현 계층)
 * 핵심 개념 :
 * - HTTP 요청을 받아서 처리
 * - 요청 데이터 검증 및 파라미터 바인딩
 */

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PurchaseService purchaseService;

    // /user/purchase/list
    @GetMapping("/user/purchase/list")
    public String purchaseList(Model model, HttpSession session) {

        User sessionuser = (User) session.getAttribute("sessionUser");
        List<PurchaseResponse.ListDTO> purchaseList = purchaseService.구매내역조회(sessionuser.getId());

        model.addAttribute("purchaseList", purchaseList);

        return "user/purchase-list";
    }

    // /user/point/charge
    @GetMapping("/user/point/charge")
    public String chargePointForm(Model model, HttpSession session) {

        User sessionUser = (User) session.getAttribute("sessionUser");

        model.addAttribute("user", sessionUser);
        return "/user/charge-point";
    }

    // [흐름] 1. 인가코드받기 -> 2. 토큰 발급 요청 (JWT) -> 3. JWT 으로 사용자 정보 요청 -> 4. 우리 서버에 로그인/ 회원가입 처리
    @GetMapping("/user/kakao")
    public String kakaoCallback(@RequestParam(name = "code") String code, HttpSession session) {
        try {
            // 서비스단에 비즈니즈 로직을 위임 처림
            User user = userService.카카오소셜로그인(code);
            // 세션 정보게 사용자 정보 저장
            session.setAttribute("sessionUser", user);
            return "redirect:/";
        } catch (Exception e) {
            System.out.println("소셜 로그인 실패 " + e.getMessage());
            // alert 창 (에러메세지) --> 로그인 페이지로 이동 처리
            throw new Exception401(e.getMessage());
        }
    }

    // http://localhost:8080/user/detail
    @GetMapping("/user/detail")
    public String detail(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");

        User user = userService.마이페이지(sessionUser.getId());

        model.addAttribute("user", user);

        return "user/detail";
    }

    @PostMapping("/user/profile-image/delete")
    public String deleteProfileImage(HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");

        User updateUser = userService.프로필이미지삭제(sessionUser.getId());
        // 왜 user를 다시 받을까? -> 세션 정보가 (즉 프로필이 삭제 되었기 때문에)
        // 세션 정보 갱신 처리 해주기 위함이다.
        session.setAttribute("sessionUser", updateUser); // 세션 정보 갱신

        // 일반적으로 POST 요청이 오면 PRG 패턴으로 설계 됨
        // POST -> Redirect 처리 ---> Get 요청
        return "redirect:/user/detail";
    }


    // 회원가입 화면 요청
    // http://localhost:8080/join
    @GetMapping("/join")
    public String joinForm() {
        return "user/join-form";
    }

    @PostMapping("/join")
    public String joinProc(UserRequest.JoinDTO joinDTO) {

        // 1. 인증검사 (x) = 필요 없음 (회원가입)
        // 2. 유효성 검사 (엉망인 데이터를 저장할 수 없음)
        // 3. 사용자 이름 중복 체크
        // 4. 저장 요청
        joinDTO.validate();

        User existingUser = userService.회원가입(joinDTO);

        return "redirect:/login";
    }

    // 로그인 화면 요청
    // http://localhost:8080/login
    @GetMapping("/login")
    public String loginForm() {
        return "user/login-form";
    }

    // JWT 토큰 기반 인증 X -> 세션 기반 인증 처리
    @PostMapping("/login")
    public String loginProc(UserRequest.LoginDTO loginDTO, HttpSession session) {
        // 1. 인증검사 X - 로그인 요청
        // 2. 유효성 검사
        // 3. db에 사용자 이름과 비밀번호 확인
        // 4. 로그인 성공 또는 실패 처리
        // 5. 웹 서버는 바보라서 사용자의 정보를 세션 메모리에 저장 시켜야
        //      다음 번 요청이 오더라도 알 수 있음 - 세션 저장 처리
        try {
            loginDTO.validate();
            User sessionUser = userService.로그인(loginDTO);

            // 세션에 저장
            session.setAttribute("sessionUser", sessionUser);


        } catch (Exception e) {
            // 로그인 실패시 다시 로그인 화면으로 처리
            return "user/login-form";
        }

        return "redirect:/";
    }

    // 회원 정보 수정 화면 요청
    // http://localhost:8080/user/update
    @GetMapping("/user/update")
    public String updateForm(Model model, HttpSession session) {
        // HttpServletRequest <---
        // A 사용자가 요청 시 --> 웹서버 --> 톰캑(WAS) Request 객체와 Response 객체를 만들어서
        //  스프링 컨테이너에게 전달해줌

        // 1. 인증 검사 (o)
        // 인증 검사를 하려면 세션 메모리에 접근해서 사용자의 정보가 있는지 없는지 여부 확인
        User sessionUser = (User) session.getAttribute("sessionUser");
        // LoginInterceptor 가 알아서 처리 해줌 !!

        // 2. 인가 처리
        // 게션의 사용자 ID로 화원 정보 조회
        User user = userService.회원정보수정화면(sessionUser.getId());

        model.addAttribute("user", user);

        return "user/update-form";
    }

    // 회원 정보 수정 기능 요청 - 더티 체킹
    @PostMapping("/user/update")
    public String updateProc(UserRequest.UpdateDTO updateDTO, HttpSession session) {
        // 1. 인증 검사
        User sessionUser = (User) session.getAttribute("sessionUser");
        // LoginInterceptor 가 알아서 처리 해줌 !!

        // 2. 유효성 검사
        // 3. 세션 메모리에 있던 기존 상태값을 변경 처리
        try {
            updateDTO.validate();
            User updateUser = userService.회원정보수정(updateDTO, sessionUser.getId());
            // 회원 정보 수정은 - 세션 갱신해 주어야 한다.
            session.setAttribute("sessionUser", updateUser);
            // 수정 후 리다이렉트 처리 - 게시판 목록으로 이동
            return "redirect:/user/detail";
        } catch (Exception e) {
            return "user/update-form";
        }

    }

    // 로그아웃
    // http://localhost:8080/logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 세션 무효화
        session.invalidate();

        return "redirect:/";
    }


}
