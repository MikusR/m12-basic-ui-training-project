package lv.bootcamp.shelter.controller;

import lv.bootcamp.shelter.form.AnimalForm;
import lv.bootcamp.shelter.service.AnimalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AnimalPageController {
    private final AnimalService animalService;

    public AnimalPageController(AnimalService animalService) {
        this.animalService = animalService;
    }

    public String index() {
        return "animals";
    }

    @GetMapping("/animals")
    public String listAnimals(Model model) {
        model.addAttribute("animals", animalService.findAll());
        return "animals";
    }

    @GetMapping("/animals/new")
    public String newAnimal(Model model) {
        model.addAttribute("form", new AnimalForm(null, null, null, null, null,null));
        return "animals-new";
    }

    @PostMapping("/animals")
    public String createAnimal(@ModelAttribute AnimalForm form) {
        animalService.createFromForm(form);
        return "redirect:animals";
    }
}
