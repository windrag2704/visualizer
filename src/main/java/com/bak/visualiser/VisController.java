package com.bak.visualiser;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class VisController {
   @RequestMapping("/")
   public String ind() {
       return "redirect:/index.html";
   }
}
