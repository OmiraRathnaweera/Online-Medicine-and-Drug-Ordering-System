package com.example.Medicine.Drug_OS.Controller;

import com.example.Medicine.Drug_OS.Entity.Seller;
import com.example.Medicine.Drug_OS.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.regex.Pattern;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    private UserService userService;

    // Inject the AuthenticationManager
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/registration")
    public String showSellerRegistrationForm(Model model) {
        model.addAttribute("seller", new Seller());
        return "seller-registration";
    }

    @PostMapping("/registration")
    public String registerSeller(@ModelAttribute Seller seller, @RequestParam String confirmPassword, @RequestParam String password, HttpServletRequest request, Model model) {
        // --- Validation remains the same ---
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "seller-registration";
        }
        if (!isValidPassword(password)) {
            model.addAttribute("error", "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.");
            return "seller-registration";
        }
        if (userService.isUsernameTaken(seller.getUsername())) {
            model.addAttribute("error", "Username already taken.");
            return "seller-registration";
        }
        if (userService.isEmailTaken(seller.getEmail())) {
            model.addAttribute("error", "Email already registered.");
            return "seller-registration";
        }

        try {
            // Set password and save user
            seller.setPassword(password);
            seller.setUserType("SELLER");
            userService.saveUser(seller);

            // --- Automatic Login Logic ---
            // 1. Create an authentication token with the new user's raw credentials
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(seller.getUsername(), password);

            // 2. Authenticate the token
            Authentication authentication = authenticationManager.authenticate(token);

            // 3. Set the authentication in the security context
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // 4. Save the security context to the HTTP session
            HttpSession session = request.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);

            // --- Redirect to the desired page ---
            return "redirect:/product-admin.html";

        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "seller-registration";
        }
    }

    private boolean isValidPassword(String password) {
        if (password == null) return false;
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$";
        return Pattern.compile(regex).matcher(password).matches();
    }
}