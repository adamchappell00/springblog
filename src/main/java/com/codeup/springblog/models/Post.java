package com.codeup.springblog.models;
import javax.persistence.*;

@Entity
@Table(name="posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String body;

    @ManyToOne
    @JoinColumn (name = "user_id")
    private User user;

    // Contstructors - No arg, Title+Body, and Id+Title+Body
    public Post(){}
    public Post(User user, String title, String body){
        this.user = user;
        this.title = title;
        this.body = body;
    }
    public Post(long id, User owner, String title, String body){
        this.id = id;
        this.user = user;
        this.title = title;
        this.body = body;
    }
    // Getters and Setters
    public long getId(){                        return id;}
    public String getTitle(){                   return title;}
    public String getBody(){                    return body;}
    public User getUser(){                      return user;}
    public void setId(long id){                 this.id = id;}
    public void setBody(String body){           this.body = body;}
    public void setTitle(String title){         this.title = title;}
    public void setUser(User user){             this.user = user;}
}
