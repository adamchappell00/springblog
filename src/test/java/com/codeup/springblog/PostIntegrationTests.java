package com.codeup.springblog;


import com.codeup.springblog.models.Post;
import com.codeup.springblog.models.User;
import com.codeup.springblog.repositories.PostRepository;
import com.codeup.springblog.repositories.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.matchers.JUnitMatchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringblogApplication.class)
@AutoConfigureMockMvc
public class PostIntegrationTests {

    private User testUser;
    private HttpSession httpSession;

    @Autowired
    private MockMvc mvc;
    @Autowired
    UserRepository userDao;
    @Autowired
    PostRepository postDao;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setup() throws Exception {
        testUser = userDao.findByUsername("integrationTestUser");
        if(testUser == null){
            User newUser = new User("integrationTestUser","testUser@codeup.com",passwordEncoder.encode("password"));
            testUser = userDao.save(newUser);
        }
        httpSession = this.mvc.perform( post("/login").with(csrf())
                        .param("username", "integrationTestUser")
                        .param("password", "password"))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(redirectedUrl("/profile"))
                .andReturn()
                .getRequest()
                .getSession();
    }

    // Sanity Tests - User Session and Context loads
    @Test
    public void contextLoads() {
        // Sanity Test, just to make sure the MVC bean is working
        assertNotNull(mvc);
    }
    @Test
    public void testIfUserSessionIsActive() throws Exception {
        // It makes sure the returned session is not null
        assertNotNull(httpSession);
    }
    // CRUD Tests for /posts functions
    @Test
    public void testCreatePostFailWithAnonymous() throws Exception {
        int random = (int)(Math.random()*(100)+1);
        this.mvc.perform(
                        post("/posts/create")
                                // Add all the required parameters to your request like this
                                .param("title", "test"+random)
                                .param("body", "WooCreateTEST"+random))
                .andExpect(status().isForbidden());
    }
    // Makes a Post request to /posts/create and expect a redirection to the Post
    @Test
    public void testCreatePostWithAuth() throws Exception {
        int random = (int)(Math.random()*(100)+1);
        this.mvc.perform(
                        post("/posts/create").with(csrf())
                                .session((MockHttpSession) httpSession)
                                // Add all the required parameters to your request like this
                                .param("title", "test"+random)
                                .param("body", "WooCreateTEST"+random))
                .andExpect(status().is3xxRedirection());
    }

    // Tests that a particular post will be shown from /posts/{id}/show
    @Test
    public void testShowPost() throws Exception {
        Post existingPost = postDao.findAll().get(0);
        // Makes a Get request to /posts/{id}/show and expect a redirection to the Post show page
        this.mvc.perform(get("/posts/" + existingPost.getId())
                        .with(csrf())
                        .session((MockHttpSession) httpSession))
                .andExpect(status().isOk())
                // Test the dynamic content of the page
                .andExpect(content().string(containsString(existingPost.getBody())));
    }
    // Test the posts index
    @Test
    public void testPostsIndex() throws Exception {
        Post existingPost = postDao.findAll().get(0);
        // Makes a Get request to /posts and verifies that we get the static text of the posts/index.html template and at least the title from the first Post is present in the template.
        this.mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                // Test the static content of the page (In this case the h1)
                .andExpect(content().string(containsString("all of the Blog Posts")))
                // Test the dynamic content of the page
                .andExpect(content().string(containsString(existingPost.getTitle())));
    }
    //
    @Test
    public void testDeletePost() throws Exception {
        // Creates a test Post to be deleted
        int random = (int)(Math.random()*(100)+1);

        this.mvc.perform(
                        post("/posts/create").with(csrf())
                                .session((MockHttpSession) httpSession)
                                .param("title", "Post to be deleted"+random)
                                .param("body", "won't last long"))
                .andExpect(status().is3xxRedirection());
        // Get the recent Post that matches the title
        Post existingPost = postDao.findByTitle("Post to be deleted"+random);
        // Makes a Post request to /posts/{id}/delete and expect a redirection to the Posts index
        this.mvc.perform(
                        delete("/posts/" + existingPost.getId() + "/delete").with(csrf())
                                .session((MockHttpSession) httpSession)
                                .param("id", String.valueOf(existingPost.getId())))
                .andExpect(status().is2xxSuccessful());
    }


}