/**
 * 
 */
package com.tienda.demo.HomeController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 
 */
@Controller
public class HomeController {
	
	@GetMapping("/")
	public String home() {
		return "index"; //Busca el template index.html en la ruta /	
	}
}
