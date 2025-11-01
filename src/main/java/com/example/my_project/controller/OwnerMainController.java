//ownerController
package com.example.my_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OwnerMainController {
    @RequestMapping("/")
    @ResponseBody

    // Method
    public String helloGFG() {
        return "Hello GeeksForGeeks";
    }

}