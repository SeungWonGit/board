package com.lightboard.board.controller;

import com.lightboard.board.entity.Board;
import com.lightboard.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BoardController {
    //step.1
    /*
        @GetMapping("/")
        @ResponseBody

        public String main() {
            return "Hello World";
        }
    */

    //step.2

    @Autowired
    private BoardService boardService;

    @GetMapping("board/write")
    public String boardWriteForm(){
        return "boardwrite";
    }

    @PostMapping("board/writedo")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception{
        boardService.write(board, file);

        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }

    /* 페이징 처리 전 LIST 조회
    @GetMapping("board/list")
    public String boardlist(Model model){ // 데이터를 담아 페이지로 보내기 위해 Model 자료형을 인자로
        model.addAttribute("list", boardService.boardlist()); // boardService에서 생성한 boardlist메소드를 통해 list가 반환되는데 해당 list를 "list"라는 이름으로 넘겨주겠다는 것(html에 나올 수 있게)
        return "boardlist";
    }
     */

    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
                            Pageable pageable,
                            String searchKeyword){ // 데이터를 담아 페이지로 보내기 위해 Model 자료형을 인자로 , 검색할 때 (searchKeyword가 있을 떄) 안할 때 구분해 if문 사용

        Page<Board> list = null;

        if(searchKeyword != null){
            list = boardService.boardSearchList(searchKeyword, pageable);
        } else {
            list = boardService.boardlist(pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1; // 현재 페이지를 가져옴 , 0에서 시작하기에 처리를 위해 + 1
        int startPage = Math.max(nowPage - 4, 1); // Math.max(a, b) -- a 와 b 중 큰 값을 반환 --> 그냥 nowPAge - 4만 하면 nowpage가 1인 경우 -3도 가능하기에 이를 방지하기 위함
        int endPage = Math.min(nowPage + 5, list.getTotalPages()); // totalPage보다 크면 안되기에 두개 중 최소값 반환하는 Math.min을 사용

        model.addAttribute("list", list ); // boardService에서 생성한 boardlist메소드를 통해 list가 반환되는데 해당 list를 "list"라는 이름으로 넘겨주겠다는 것(html에 나올 수 있게)
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "boardlist";
    }


    @GetMapping("board/view")
    public String boardview(Model model, Integer id){ //localhost:8080/board/view?id=1 이런 식 (get 방식)
        model.addAttribute("board", boardService.boardview(id));
        return "boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Integer id){
        boardService.boardDelete(id);
        return "redirect:/board/list"; // 삭제 처리 후, list로 이동 - redirect 해줌
    }

    @GetMapping("/board/modify/{id}") // 여기서 id는 path variable (주소 변수, 경로 변수)
    public String boardModify(@PathVariable("id") Integer id, Model model){
        model.addAttribute("board", boardService.boardview(id));
        return "boardModify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception{

        Board boardTemp = boardService.boardview(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent((board.getContent())); //수정한 내용을 board 자료형의 boardTemp변수의 title과 content로 설정해준다.

        boardService.write(boardTemp, file); //설정한 boardTemp의 내용을 다시 해당 게시물 title 과 content에 덮어써주는 코드

        model.addAttribute("message", "글 수정이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }
}