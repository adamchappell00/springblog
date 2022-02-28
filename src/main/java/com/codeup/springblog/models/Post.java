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

    @OneToOne
    private User owner;
    // Contstructors - No arg, Title+Body, and Id+Title+Body
    public Post(){}
    public Post(User owner, String title, String body){
        this.owner = owner;
        this.title = title;
        this.body = body;
    }
    public Post(long id, User owner, String title, String body){
        this.id = id;
        this.owner = owner;
        this.title = title;
        this.body = body;
    }
    // Getters and Setters
    public long getId(){                        return id;}
    public String getTitle(){                   return title;}
    public User getOwner(){                     return owner;}
    public void setTitle(String title){         this.title = title;}
    public String getBody(){                    return body;}
    public void setBody(String body){           this.body = body;}
}
