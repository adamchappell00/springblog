package com.codeup.springblog;

import com.codeup.springblog.models.Post;
import com.codeup.springblog.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PostController {

    private final PostRepository postDao;
    private final UserRepository userDao;

    public PostController(PostRepository postDao, UserRepository userDao) {
        this.postDao = postDao;
        this.userDao = userDao;
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
        User tempUser = userDao.getById(1L);
        Post createdPost = postDao.save(new Post(tempUser, title,body));
        return "redirect:/posts";
    }
    @GetMapping( "/posts/show/{id}")
    public String showPostId(@PathVariable long id, Model model){
        Post postShown = postDao.getById(id);
        model.addAttribute("post", postShown);
        return "/posts/show";
    }
    @PostMapping("/posts/delete")
    public String deletePost(@RequestParam("id") long id, Model model){
        postDao.deleteById(id);
        model.addAttribute("id", id);
        return "/posts/deleted";
    }
    @GetMapping("/posts/edit")
    public String showEdit(@RequestParam("id") long id, Model model){
        Post editPost = postDao.getOne(id);
        model.addAttribute("post", editPost);
        return "/posts/edit";
    }
    @PostMapping("/posts/edit")
    public String submitEditPost(@RequestParam("title") String title, @RequestParam("body") String body, @RequestParam long id, Model model){
        Post editedPost = postDao.getOne(id);
        editedPost.setTitle(title);
        editedPost.setBody(body);
        postDao.save(editedPost);
        model.addAttribute("post", editedPost);
        return "/posts/show";
    }
}