package com.codeup.springblog;

import com.codeup.springblog.models.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PostController {
    @GetMapping( "/posts")
    public String postsIndex(Model model){
        List<Post> allPosts = new ArrayList<>();
        Post first = new Post(1, "Hello World!", "This is my new Blog, it is awesome.");
        Post second = new Post(2, "Blog 2: Electric Boogaloo", "I am writing a new post about more cool things.");
        allPosts.add(first);
        allPosts.add(second);
        model.addAttribute("allPosts", allPosts);
        return "/posts/index";
    }
    @GetMapping( "/posts/show/{id}")
    public String showPostId(@PathVariable int id, Model model){
        Post post1 = new Post(id,"I love Tacos", "Seriously let me tell you how much I love them...");
        model.addAttribute("post", post1);
        model.addAttribute("id", id);
        return "/posts/show";
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