package org.example.demo_ssr_v1.board;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    // 게시글 수정 폼 페이지 요청
    // http://localhost:8080/board/1/update
    @GetMapping("/board/{id}/update")
    public String updateForm(@PathVariable Long id, Model model) {

        Board board = repository.findById(id);
        if (board == null) {
            throw new RuntimeException("수정할 게시글을 찾을 수 없어요");
        }

        model.addAttribute("board", board);

        return "board/update-form";
    }

    // 게시글 수정 요청 (기능요청)
    // http://localhost:8080/board/1/update
    @PostMapping("/board/{id}/update")
    public String updateProc(@PathVariable Long id, BoardRequest.UpdateDTO updateDTO) {

        try {
            repository.updateById(id, updateDTO);
        } catch (Exception e) {
            throw new RuntimeException("게시글 수정 실패");
        }

        return "redirect:/board/list";
    }

    // 게시글 목록 요청
    // http://localhost:8080/board/list
    @GetMapping({"/board/list", "/"})
    public String boardList(Model model) {
        List<Board> boardList = repository.findAll();
        model.addAttribute("boardList", boardList);
        return "board/list";
    }

    // 게시글 저장 화면 요청
    // http://localhost:8080/board/save
    @GetMapping("/board/save")
    public String saveFrom() {

        return "board/save-form";
    }

    // 게시글 저장 요청 (기능 요청)
    // http://localhost:8080/board/save
    @PostMapping("/board/save")
    public String saveProc(BoardRequest.SaveDTO saveDTO) {

        Board board = repository.save(saveDTO.toEntity());

        return "redirect:/board/list";
    }
    
}
