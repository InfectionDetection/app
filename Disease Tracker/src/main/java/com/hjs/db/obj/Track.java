package com.hjs.db.obj;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Saswat on 11/14/2015.
 */
@Entity
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "track_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "track_sick_status")
    private int sick_status;

    @Column(name = "track_lat", precision=15, scale=7)
    private BigDecimal lat;

    @Column(name = "track_lng", precision=15, scale=7)
    private BigDecimal lng;

    @Column(name="date_created", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date_created;

    protected Track() {}

    public Track(User user, int sick_status, BigDecimal lat, BigDecimal lng) {
        this.user = user;
        this.sick_status = sick_status;
        this.lat = lat;
        this.lng = lng;
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

    public int getSickStatus() {
        return sick_status;
    }

    public void setSickStatus(int sick_status) {
        this.sick_status = sick_status;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public Date getDateCreated() {
        return date_created;
    }

    public void setDateCreated(Date date_created) {
        this.date_created = date_created;
    }
}
