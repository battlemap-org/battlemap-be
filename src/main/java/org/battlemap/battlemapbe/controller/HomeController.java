package org.battlemap.battlemapbe.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping(value = "/", produces = "text/plain")
    public String home() {
        return "Server is running!";
    }
}
