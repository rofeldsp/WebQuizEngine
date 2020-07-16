package engine.quiz;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.xml.crypto.Data;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class QuizCompleted {

////    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
////    @ManyToOne
//    @Column (unique = true)
//    public long userId;

//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @ManyToOne
//    @Column (unique = true)
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @GeneratedValue
    private long uniqueId;

    @Column(name = "Id")
    private long id;

    @Column(name = "completedAt")
    private LocalDateTime completedAt;

    public QuizCompleted() {};

    public QuizCompleted(long id, Quiz quiz, User user) {
        this.id = id;
        this.user = user;
        this.quiz = quiz;
//        this.userId = userId;
        completedAt = LocalDateTime.now();
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
