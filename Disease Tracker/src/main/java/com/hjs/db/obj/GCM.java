package com.hjs.db.obj;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Saswat on 11/15/2015.
 */
@Entity
public class GCM {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "gcm_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="gcm_registration")
    private String registration;

    protected GCM() {}

    public GCM(User user, String registration) {
        this.user = user;
        this.registration = registration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }
}
