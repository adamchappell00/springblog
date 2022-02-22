package com.codeup.springblog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PostController {
    @GetMapping(path = "/posts")
    @ResponseBody
    public String posts(){
        return "this is the post index page";
    }
    @GetMapping(path = "/posts/{id}")
    @ResponseBody
    public String postId(@PathVariable int id){
        return "This is the individual page for post " + id + ".";
    }
    @GetMapping(path = "/posts/create")
    @ResponseBody
    public String createForm(){
        return "this is the page for the post creation form";
    }
    @PostMapping(path = "/posts/create")
    @ResponseBody
    public String createPost(){
        return "This is the result of the post creation form submission!";
    }
}