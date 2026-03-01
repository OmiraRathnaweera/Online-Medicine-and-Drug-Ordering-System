package com.example.Medicine.Drug_OS.Controller;

import com.example.Medicine.Drug_OS.Entity.RegularUser;
import com.example.Medicine.Drug_OS.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String viewRoot() {
        return "redirect:/products.html";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new RegularUser());
        return "user-register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegularUser user, @RequestParam String confirmPassword, Model model, RedirectAttributes redirectAttributes) {
        if (userService.isUsernameTaken(user.getUsername())) {
            model.addAttribute("error", "Username is already taken!");
            return "user-register";
        }
        if (userService.isEmailTaken(user.getEmail())) {
            model.addAttribute("error", "Email is already registered.");
            return "user-register";
        }
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "user-register";
        }

        try {
            user.setUserType("REGULAR");
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "User registered successfully! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed due to a system error.");
            return "user-register";
        }
    }
}