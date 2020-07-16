package engine.quiz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Generated;
//import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Quiz {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    public User user;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String text;
    @NotNull
    @Size(min = 2)
    private String[] options;
    @ElementCollection
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Integer> answer = new HashSet<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<QuizCompleted> quizzesCompleted;

    public Quiz() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public Set<Integer> getAnswer() {
        return answer;
    }

    public void setAnswer(Set<Integer> answer) {
        this.answer = answer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

//@Entity
//@Validated
//@Entity
//public class Quiz {
//    @Id
//    private int id;
//
//    @Pattern(regexp = ".+")
//    @NotNull
//    private String title;
//
//    @Pattern(regexp = ".+")
//    @NotNull
//    private String text;
//
//    @NotNull
//    private String[] options;
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
////    @NotNull
//
//    private int[] answer;
//
//    @Column(name = "Answer to Quiz")
//    public int[] getAnswer() {
//        return answer;
//    }
//
//    @Column(name = "QuizId")
//    public int getId() {
//        return id;
//    }
//
//    @Column(name = "Quiz Title")
//    public String getTitle() {
//        return title;
//    }
//
//    @Column(name = "Question")
//    public String getText() {
//        return text;
//    }
//
//    @Column(name = "Quiz Options")
//    public String[] getOptions() {
//        return options;
//    }
//
//    public void setAnswer(int[] answer) {
//        this.answer = answer;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    public void setOptions(String[] options) {
//        this.options = options;
//    }
//}
//
//class ReturnBody {
//    private int id;
//    private String title;
//    private String text;
//    private String[] options;
//
//    public ReturnBody(int id, String title, String text, String[] options) {
//       this.id = id;
//       this.title = title;
//       this.text = text;
//       this.options = options;
//    }
//}
