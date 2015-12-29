package entities;

import javax.persistence.*;


@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "hours")
    private Double hours;

    @ManyToOne
    @JoinColumn(name="user_id",columnDefinition = "int")
    private TimeUser user;

    private Long timestamp;

    public Attendance() {
    }

    public Attendance(Double hours, TimeUser user, Long timestamp) {
        this.hours = hours;
        this.user = user;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TimeUser getUser() {
        return user;
    }

    public void setUser(TimeUser user) {
        this.user = user;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return user.getName()+" "+hours;
    }
}
