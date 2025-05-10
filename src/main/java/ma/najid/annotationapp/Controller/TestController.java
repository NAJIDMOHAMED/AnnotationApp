package ma.najid.annotationapp.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller

public class TestController {

    @GetMapping("/test")
    public String testPage(Model model) {
        model.addAttribute("message", "Welcome to Thymeleaf!");
        model.addAttribute("currentTime", LocalDateTime.now());
        model.addAttribute("items", Arrays.asList("Item 1", "Item 2", "Item 3"));
        return "test/test";
    }

    @GetMapping("/test/hello")
    public String helloWorld(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "test/hello";
    }
} 