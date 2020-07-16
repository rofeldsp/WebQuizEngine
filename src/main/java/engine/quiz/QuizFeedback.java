package engine.quiz;

import org.springframework.beans.factory.annotation.Autowired;

public class QuizFeedback {
    @Autowired
    QuizCompletedRepository quizCompletedRepository;

    private final boolean isSuccess;
    private final String feedback;

    public QuizFeedback(Quiz quiz, Answer answer) {
        if (quiz.getAnswer().equals(answer.getAnswer())) {
            isSuccess = true;
            feedback = "Congratulations, you're right!";
//            quizCompletedRepository.save(new QuizCompleted());
        } else {
            isSuccess = false;
            feedback = "Wrong answer! Please, try again.";
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getFeedback() {
        return feedback;
    }
}

