package org.example.demo_ssr_v1.admin;

import jakarta.servlet.http.HttpSession;
import org.example.demo_ssr_v1._core.errors.exception.Exception401;
import org.example.demo_ssr_v1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1.refund.RefundResponse;
import org.example.demo_ssr_v1.refund.RefundService;
import org.example.demo_ssr_v1.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {

    private final RefundService refundService;

    public AdminController(RefundService refundService) {
        this.refundService = refundService;
    }

    // http://localhost:8080/admin/dashboard
    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {

        User sessionUser = (User) session.getAttribute("sessionUser");

        model.addAttribute("user", sessionUser);

        return "admin/dashboard";
    }

    // 관리자 환불 요청 관리 목록 페이지
    @GetMapping("/admin/refund/list")
    public String refundManagement(Model model) {

        List<RefundResponse.AdminListDTO> refundList = refundService.관리자환불요청목록조회();
        model.addAttribute("refundList", refundList);

        return "admin/admin-refund-list";
    }

    // /admin/refund/${id}/reject
    @PostMapping("/admin/refund/{id}/reject")
    //  @RequestParam(name = "rejectReason") 이렇게 쓰면 변수명 변경 가능
    public String rejectRefund(@PathVariable Long id,
                               @RequestParam(name = "rejectReason") String rejectReason
    ) {
        refundService.환불거절(id, rejectReason);

        return "redirect:/admin/refund/list";
    }

    // /admin/refund/${id}/approve
    @PostMapping("/admin/refund/{id}/approve")
    public String approveRefund(@PathVariable Long id) {

        refundService.환불승인(id);

        return "redirect:/admin/refund/list";
    }
}
