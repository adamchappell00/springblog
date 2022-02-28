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
    public String showCreateForm(Model model){
        model.addAttribute("post", new Post());
        return "/posts/create";
    }
    @PostMapping("/posts/create")
    public String submitCreatePost(@RequestParam("title") String title, @RequestParam("body") String body) {
        User tempUser = userDao.getById(1L);
        postDao.save(new Post(tempUser, title,body));
        return "redirect:/posts";
    }
    @GetMapping( "/posts/{id}/show")
    public String showPostId(@PathVariable long id, Model model){
        Post postShown = postDao.getById(id);
        model.addAttribute("post", postShown);
        return "/posts/show";
    }
    @PostMapping("/posts/{id}/delete")
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
    public String submitEditPost(@PathVariable long id, @RequestParam("title") String title, @RequestParam("body") String body, Model model){
        Post editedPost = postDao.getOne(id);
        editedPost.setTitle(title);
        editedPost.setBody(body);
        postDao.save(editedPost);
        model.addAttribute("post", editedPost);
        return "redirect:/posts/"+id+"/show";
    }
}