package com.hjs.db.obj;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Saswat on 11/14/2015.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private long id;

    @Column(name = "user_uuid")
    private String uuid;

    @Column(name = "user_track")
    private int track;

    @Column(name = "user_status")
    private int status;

    @Column(name="user_sick_start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sick_start;

    @Column(name="date_created", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date_created;

    protected User() {}

    public User(String uuid) {
        this.uuid = uuid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getSickStart() {
        return sick_start;
    }

    public void setSickStart(Date sick_start) {
        this.sick_start = sick_start;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public Date getDateCreated() {
        return date_created;
    }

    public void setDateCreated(Date date_created) {
        this.date_created = date_created;
    }
}
