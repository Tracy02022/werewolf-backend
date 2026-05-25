package com.tracy.werewolf.controller;

import com.tracy.werewolf.dto.JudgeRuleResponse;
import com.tracy.werewolf.service.BoardService;
import com.tracy.werewolf.service.RoleCatalogService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rules")
@CrossOrigin(origins = "*")
public class RulesController {
    private final RoleCatalogService roleCatalogService;
    private final BoardService boardService;

    public RulesController(RoleCatalogService roleCatalogService, BoardService boardService) {
        this.roleCatalogService = roleCatalogService;
        this.boardService = boardService;
    }

    @GetMapping
    public JudgeRuleResponse getRules() {
        return new JudgeRuleResponse(
                "法官负责推进游戏流程、宣布天黑天亮、引导角色行动、公布死亡和投票结果。当前版本前端使用浏览器 AI 语音播报，后续可以替换为真人配音音频。",
                List.of(
                        "天黑请闭眼",
                        "首夜特殊角色行动：盗贼、丘比特、混血儿、暗恋者等按板子需要行动",
                        "狼人请睁眼，确认队友并选择击杀目标",
                        "女巫请睁眼，选择是否使用解药或毒药",
                        "预言家请睁眼，查验一名玩家身份阵营",
                        "守卫、摄梦人、禁言长老、乌鸦等其他角色按本局板子行动",
                        "天亮了，法官公布昨夜死亡信息并进入发言"
                ),
                Map.of(
                        "狼人阵营", "屠边：杀光所有神职或所有平民。",
                        "好人阵营", "放逐或击杀所有狼人。",
                        "第三方阵营", "根据角色特殊规则独立获胜。"
                ),
                roleCatalogService.getRoles(null),
                boardService.getBoards(null)
        );
    }
}
