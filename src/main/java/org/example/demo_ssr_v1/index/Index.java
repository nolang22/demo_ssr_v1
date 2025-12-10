package org.example.demo_ssr_v1.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Index {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
