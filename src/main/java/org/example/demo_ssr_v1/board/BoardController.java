package org.example.demo_ssr_v1.board;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._core.errors.exception.Exception401;
import org.example.demo_ssr_v1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor // 세번째 방법
public class BoardController {

    // @Autowired  두번째 방법
//    private BoardPersistRepository boardPersistRepository;

    // DI (첫 번째 방법)
//    public BoardController(BoardPersistRepository boardPersistRepository) {
//        this.boardPersistRepository = boardPersistRepository;
//    }

    private final BoardPersistRepository repository;

    /**
     * 게시글 수정 화면 요청
     *
     * @param id
     * @param model
     * @param session
     * @return
     */
    // http://localhost:8080/board/1/update
    @GetMapping("/board/{id}/update")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {

        // 1. 인증 검사 (o)
        User sessionUser = (User) session.getAttribute("sessionUser"); // sessionUser -> 상수
        // LoginInterceptor 가 알아서 처리 해줌 !!

        // 2. 인가 검사 (O)
        Board board = repository.findById(id);
        if (board == null) {
            throw new RuntimeException("수정할 게시글을 찾을 수 없어요");
        }

        if (board.isOwner(sessionUser.getId()) == false) {
            throw new Exception403("게시글 수정 권한이 없습니다.");
        }


        model.addAttribute("board", board);

        return "board/update-form";
    }

    /**
     * 게시글 화면 기능 요청
     *
     * @param id
     * @param updateDTO
     * @param session
     * @return
     */
    // http://localhost:8080/board/1/update
    @PostMapping("/board/{id}/update")
    public String updateProc(@PathVariable Long id, BoardRequest.UpdateDTO updateDTO, HttpSession session) {

        User sessionUser = (User) session.getAttribute("sessionUser");
        // LoginInterceptor 가 알아서 처리 해줌 !!

        // 조회
        Board board = repository.findById(id);
        if (board.isOwner(sessionUser.getId()) == false) {
            throw new Exception403("게시글 수정 권한이 없습니다.");
        }

        try {
            repository.updateById(id, updateDTO);
        } catch (Exception e) {
            throw new RuntimeException("게시글 수정 실패");
        }

        return "redirect:/board/list";
    }

    /**
     * 게시글 목록 화면 요청
     *
     * @param model
     * @return
     */
    // http://localhost:8080/board/list
    @GetMapping("/board/list")
    public String boardList(Model model) {

        List<Board> boardList = repository.findAll();
        model.addAttribute("boardList", boardList);
        return "board/list";
    }

    /**
     * 게시글 작성 화면 오청
     *
     * @param session
     * @return
     */
    // http://localhost:8080/board/save
    @GetMapping("/board/save")
    public String saveFrom(HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        // LoginInterceptor 가 알아서 처리 해줌 !!

        return "board/save-form";
    }

    /**
     * 게시글 작성 요청 기능
     *
     * @param saveDTO
     * @param session
     * @return
     */
    // http://localhost:8080/board/save
    @PostMapping("/board/save")
    public String saveProc(BoardRequest.SaveDTO saveDTO, HttpSession session) {
        // 1. 인증 처리 확인
        User sessionUser = (User) session.getAttribute("sessionUser");
        // LoginInterceptor 가 알아서 처리 해줌 !!

        Board board = repository.save(saveDTO.toEntity(sessionUser));

        return "redirect:/board/list";
    }

    /**
     * 게시글 상세 보기 화면 요청
     * @param id
     * @param model
     * @return
     */
    // http://localhost:8080/board/1
    @GetMapping("/board/{id}")
    public String detail(@PathVariable Long id, Model model) {

        Board board = repository.findById(id);
        if (board == null) {
            throw new Exception403("게시글을 찾을 수 없어요");
        }
        model.addAttribute("board", board);

        return "board/detail";
    }

    /**
     * 게시글 삭제 기능 요청
     *
     * @param id
     * @param session
     * @return
     */
    // http://localhost:8080/board/1/delete
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        // 1. 인증 처리 (o)
        User sessionUser = (User) session.getAttribute("sessionUser");
        // LoginInterceptor 가 알아서 처리 해줌 !!

        // 2. 인가 처리 (o) || 관리자 권한
        Board board = repository.findById(id);
        if (board.isOwner(sessionUser.getId()) == false) {
            throw new Exception401("삭제 권한이 없습니다.");
        }

        repository.deleteById(id);

        return "redirect:/board/list";
    }
}