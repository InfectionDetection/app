package com.hjs.db.obj;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Saswat on 11/14/2015.
 */
@Entity
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "history_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="history_start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Column(name="history_end")
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    @Column(name="date_created", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date_created;

    protected History() {}

    public History(User user, Date start, Date end) {
        this.user = user;
        this.start = start;
        this.end = end;
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

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getDateCreated() {
        return date_created;
    }

    public void setDateCreated(Date date_created) {
        this.date_created = date_created;
    }
}
