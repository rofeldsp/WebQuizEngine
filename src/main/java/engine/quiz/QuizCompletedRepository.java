package engine.quiz;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizCompletedRepository extends JpaRepository<QuizCompleted, Long> {

    @Query("SELECT q FROM QuizCompleted q WHERE user_id = ?1")
    Page<QuizCompleted> findAllByUser(long userId, Pageable pageable);

//    Page<QuizCompleted> findAllByUser(User user, Pageable pageable);
}
