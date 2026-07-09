package lv.bootcamp.shelter.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lv.bootcamp.shelter.form.AnimalForm;
import lv.bootcamp.shelter.model.AnimalType;
import lv.bootcamp.shelter.service.AnimalService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AnimalPageController {
    private final AnimalService animalService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/animals")
    public String listAnimals(Model model, HttpServletRequest request) {
        model.addAttribute("animals", animalService.findAll());
        return "animals";
    }

    @GetMapping("/animals/new")
    public String newAnimal(Model model, HttpServletRequest request) {
        if (!request.isUserInRole("ADMIN")){return "redirect:animals";}
        model.addAttribute("types", AnimalType.values());
        model.addAttribute("form", new AnimalForm(null, null, null, null, null, null));
        return "animals-new";
    }

    @PostMapping("/animals")
    public String createAnimal(@ModelAttribute("form") @Valid AnimalForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("types", AnimalType.values());
            return "animals-new";
        }
        animalService.createFromForm(form);
        return "redirect:animals";
    }
    @ModelAttribute
    public void addSecurityAttributes(Model model, Authentication authentication) {
        if (authentication != null) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            boolean isUser = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isUser", isUser);
        } else {
            model.addAttribute("isAdmin", false);
            model.addAttribute("isUser", false);
        }
    }
}
