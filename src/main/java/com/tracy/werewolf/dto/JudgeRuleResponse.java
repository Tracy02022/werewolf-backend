package com.tracy.werewolf.dto;

import com.tracy.werewolf.model.Board;
import com.tracy.werewolf.model.RoleInfo;
import java.util.List;
import java.util.Map;

public class JudgeRuleResponse {
    private String judgeIntro;
    private List<String> nightOrder;
    private Map<String, String> winCondition;
    private List<RoleInfo> roles;
    private List<Board> boards;

    public JudgeRuleResponse(String judgeIntro, List<String> nightOrder, Map<String, String> winCondition, List<RoleInfo> roles, List<Board> boards) {
        this.judgeIntro = judgeIntro;
        this.nightOrder = nightOrder;
        this.winCondition = winCondition;
        this.roles = roles;
        this.boards = boards;
    }

    public String getJudgeIntro() { return judgeIntro; }
    public List<String> getNightOrder() { return nightOrder; }
    public Map<String, String> getWinCondition() { return winCondition; }
    public List<RoleInfo> getRoles() { return roles; }
    public List<Board> getBoards() { return boards; }
}
