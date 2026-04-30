package com.tracy.werewolf.controller;
import com.tracy.werewolf.model.Board; import com.tracy.werewolf.service.BoardService; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/boards") @CrossOrigin(origins="*")
public class BoardController { private final BoardService boardService; public BoardController(BoardService boardService){this.boardService=boardService;} @GetMapping public List<Board> getBoards(@RequestParam(required=false) Integer playerCount){return boardService.getBoards(playerCount);} }
