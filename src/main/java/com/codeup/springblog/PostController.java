package com.codeup.springblog;

import com.codeup.springblog.models.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PostController {

    private final PostRepository postDao;

    public PostController(PostRepository postDao) {
        this.postDao = postDao;
    }

    @GetMapping( "/posts")
    public String postsIndex(Model model){
        model.addAttribute("allPosts", postDao.findAll());
        return "/posts/index";
    }

    @GetMapping("/posts/create")
    public String showCreateForm(){
        return "/posts/create";
    }
    @PostMapping("/posts/create")
    public String submitCreatePost(@RequestParam("title") String title, @RequestParam("body") String body, Model model) {
        Post createdPost = postDao.save(new Post(title,body));
        model.addAttribute("post", createdPost);
        model.addAttribute("id", createdPost.getId());
        return "/posts/show";
    }
    @GetMapping( "/posts/show/{id}")
    public String showPostId(@PathVariable long id, Model model){
        Post postShown = postDao.getById(id);
        model.addAttribute("post", postShown);
        return "/posts/show";
    }
    @PostMapping("/posts/delete")
    public String deletePost(@PathVariable long id, Model model){
        postDao.deleteById(id);
        model.addAttribute("id", id);
        return "/posts/deleted";
    }
}