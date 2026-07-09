package lv.bootcamp.shelter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AnimalPageController {
    public String index() {
        return "animals";
    }
    @GetMapping("/animals")
    public String animals() {

        return "animals";
    }
    @GetMapping("/animals/new")
    public String newAnimal() {
        return "animals-new";
    }
    @PostMapping("/animals")
    public String createAnimal() {
        return "redirect:animals";
    }
}
