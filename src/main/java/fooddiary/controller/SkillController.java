package fooddiary.controller;

import fooddiary.service.SkillService;
import fooddiary.yacloud.Event;
import fooddiary.yacloud.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/skill")
public class SkillController {
    private final SkillService skill;

    @PostMapping
    public Response skill(@RequestBody Event event) {
        return skill.getResponse(event);
    }
}
