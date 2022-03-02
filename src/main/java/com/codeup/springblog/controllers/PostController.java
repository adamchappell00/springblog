package com.codeup.springblog.controllers;

import com.codeup.springblog.services.EmailService;
import com.codeup.springblog.models.Post;
import com.codeup.springblog.models.User;
import com.codeup.springblog.repositories.PostRepository;
import com.codeup.springblog.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {

    private final PostRepository postDao;
    private final UserRepository userDao;
    private final EmailService emailService;

    public PostController(PostRepository postDao, UserRepository userDao, EmailService emailService) {
        this.postDao = postDao;
        this.userDao = userDao;
        this.emailService = emailService;
    }

    @GetMapping( "/posts")
    public String postsIndex(Model model){
        model.addAttribute("allPosts", postDao.findAll());
        return "/posts/index";
    }
    @GetMapping("/profile")
    public String profileView(Model model){
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Post> userPosts = postDao.findAllByUser(currentUser);
        model.addAttribute("userPosts", userPosts);
        return "users/profile";
    }
    @GetMapping("/posts/create")
    public String showCreateForm(Model model){
        model.addAttribute("post", new Post());
        return "/posts/create";
    }
    @PostMapping("/posts/create")
    public String submitCreatePost(@ModelAttribute Post post) {
        post.setUser( (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Post createdPost = postDao.save(post);
        emailService.sendPostCreation(createdPost);
        return "redirect:/posts";
    }
    @GetMapping( "/posts/{id}/show")
    public String showPostId(@PathVariable long id, Model model){
        Post postShown = postDao.getById(id);
        model.addAttribute("post", postShown);
        return "/posts/show";
    }
    @DeleteMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable long id, Model model){
        postDao.deleteById(id);
        model.addAttribute("id", id);
        return "/posts/deleted";
    }
    @GetMapping("/posts/{id}/edit")
    public String showEdit(@PathVariable long id, Model model){
        Post editPost = postDao.getOne(id);
        model.addAttribute("post", editPost);
        return "/posts/edit";
    }
    @PostMapping("/posts/{id}/edit")
    public String submitEditPost(@PathVariable long id, @ModelAttribute Post post, Model model){
        Post editedPost = postDao.getOne(id);
        editedPost.setTitle(post.getTitle());
        editedPost.setBody(post.getBody());
        postDao.save(editedPost);
        model.addAttribute("post", editedPost);
        return "redirect:/posts/"+id+"/show";
    }
}