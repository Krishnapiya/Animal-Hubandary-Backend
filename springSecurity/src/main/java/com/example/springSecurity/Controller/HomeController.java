package com.example.springSecurity.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Home Page";
    }

    @GetMapping("/employee")
    public String employee() {
        return "Employee Page";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/admin/home")
	public String getAdminHome() {
		return "Admin Home Page";
	}

	@GetMapping("/client/home")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public String getClientHome() {
        return "Client Home Page";
    }
    

    }