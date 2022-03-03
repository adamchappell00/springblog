package com.codeup.springblog;

import com.codeup.springblog.models.Post;
import com.codeup.springblog.models.User;
import com.codeup.springblog.repositories.PostRepository;
import com.codeup.springblog.repositories.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import javax.servlet.http.HttpSession;
import static javax.swing.UIManager.get;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = SpringblogApplication.class)
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
        testUser = userDao.findByUsername("testUser");
        if(testUser == null){
            User newUser = new User("testUser","testUser@codeup.com","password");
            newUser.setUsername("testUser");
            newUser.setPassword(passwordEncoder.encode("password"));
            newUser.setEmail("testUser@codeup.com");
            testUser = userDao.save(newUser);
        }
        httpSession = this.mvc.perform(post("/login").with(csrf())
                        .param("username", "testUser")
                        .param("password", "password"))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(redirectedUrl("/posts"))
                .andReturn()
                .getRequest()
                .getSession();
    }



    // Further Tests Below

    // User session Loads
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
    // Makes a Post request to /posts/create and expect a redirection to the Post
    @Test
    public void testCreatePost() throws Exception {
        this.mvc.perform(
                        post("/posts/create").with(csrf())
                                .session((MockHttpSession) httpSession)
                                // Add all the required parameters to your request like this
                                .param("title", "test")
                                .param("body", "WooCreateTEST"))
                .andExpect(status().is3xxRedirection());
    }

    // Tests that a particular post will be shown from /posts/{id}/show
    @Test
    public void testShowPost() throws Exception {
        Post existingPost = postDao.findAll().get(0);

        // Makes a Get request to /posts/{id}/show and expect a redirection to the Post show page
        this.mvc.perform(get("/ads/" + existingPost.getId() + "/show"))
                .andExpect(status().isOk())
                // Test the dynamic content of the page
                .andExpect(content().string(containsString(existingPost.getBody())));
    }
    // Test the posts index
    @Test
    public void testPostsIndex() throws Exception {
        Post existingPost = postDao.findAll().get(0);

        // Makes a Get request to /posts and verifies that we get some of the static text of the posts/index.html template and at least the title from the first Post is present in the template.
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
        this.mvc.perform(
                        post("/posts/create").with(csrf())
                                .session((MockHttpSession) httpSession)
                                .param("title", "Post to be deleted")
                                .param("body", "won't last long"))
                .andExpect(status().is3xxRedirection());
        // Get the recent Post that matches the title
        Post existingPost = postDao.findByTitle("Post to be deleted");
        // Makes a Post request to /posts/{id}/delete and expect a redirection to the Posts index
        this.mvc.perform(
                        post("/posts/" + existingPost.getId() + "/delete").with(csrf())
                                .session((MockHttpSession) httpSession)
                                .param("id", String.valueOf(existingPost.getId())))
                .andExpect(status().is3xxRedirection());
    }



}
