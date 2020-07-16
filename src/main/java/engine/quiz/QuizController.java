package engine.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.*;


@RestController
@RequestMapping("/api")
@Validated
public class QuizController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizCompletedRepository quizCompletedRepository;

    private ArrayList<Quiz> quizzes = new ArrayList<>();

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User newUser) {
        User foundUser = userRepository.findByEmail(newUser.getEmail());
        if (foundUser == null) {
            newUser.setPassword(
                    bCryptPasswordEncoder.encode(newUser.getPassword())
            );
            userRepository.save(newUser);
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(newUser, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/quizzes", consumes = "application/json")
    public ResponseEntity<Quiz> addQuiz(@Valid @RequestBody Quiz quiz) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currUser = userRepository.findByEmail(
                ((UserDetails) principal).getUsername()
        );
        quiz.setUser(currUser);

        quizRepository.save(quiz);
        return new ResponseEntity<>(quiz, HttpStatus.OK);
    }

    @GetMapping(path = "/quizzes/{id}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable Long id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        return quiz == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(quiz, HttpStatus.OK);
    }

    @GetMapping(path = "/quizzes")
    public ResponseEntity<Page<Quiz>> getQuizzes(@RequestParam(value = "page", required = false) Integer page) {
        Pageable paging;
        if (page == null) {
            paging = PageRequest.of(0, 10);
        } else {
            paging = PageRequest.of(page, 10);
        }
        Page<Quiz> pagedResult = quizRepository.findAll(paging);
        return new ResponseEntity<>(pagedResult, HttpStatus.OK);
//        return new ResponseEntity(quizRepository.findAll(paging)), HttpStatus.OK);
    }

    @PostMapping(path = "/quizzes/{id}/solve")
    public ResponseEntity<QuizFeedback> solveQuiz(@PathVariable Long id, @RequestBody Answer answer) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currUser = userRepository.findByEmail(
                ((UserDetails) principal).getUsername());
        if (quiz == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            if (quiz.getAnswer().equals(answer.getAnswer())) {
                System.out.println("check");
                quizCompletedRepository.save(new QuizCompleted(quiz.getId(), quiz, currUser));
            }
            return new ResponseEntity<>(new QuizFeedback(quiz, answer), HttpStatus.OK);
        }
//        return quiz == null
//                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
//                : new ResponseEntity<>(new QuizFeedback(quiz, answer), HttpStatus.OK);
    }

    @GetMapping(path = "/quizzes/completed")
    public ResponseEntity<Page<QuizCompleted>> getQuizzesCompleted(@RequestParam(value = "page", required = false) Integer page) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
//        If (userRepository
//            .findByEmail(((UserDetails) principal).getUsername()).getId()
//            .equals()
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currUser = userRepository.findByEmail(
                ((UserDetails) principal).getUsername()
        );
        Pageable paging;
        if (page == null) {
            paging = PageRequest.of(0, 10, Sort.by("completedAt").descending());
        } else {
            paging = PageRequest.of(page, 10, Sort.by("completedAt").descending());
        }
        Page<QuizCompleted> pagedResult = quizCompletedRepository.findAllByUser(currUser.getId(), paging);
        return new ResponseEntity<>(pagedResult, HttpStatus.OK);
//        return new ResponseEntity(quizRepository.findAll(paging)), HttpStatus.OK);
    }

//    @GetMapping(path = "/quizzes")
//    public ResponseEntity<Page<Quiz>> getQuizzesByPage(@RequestParam int page) {
//        Pageable paging = PageRequest.of(page - 1, 10);
//        Page<Quiz> pagedResult = quizRepository.findAll(paging);
//        if (pagedResult.getTotalElements() == 0) {
//            return new ResponseEntity<>(pagedResult, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(pagedResult, HttpStatus.OK);
////        return new ResponseEntity(quizRepository.findAll(paging)), HttpStatus.OK);
//    }


    @DeleteMapping("/quizzes/{id}")
    public void deleteQuiz(@PathVariable Long id) {
        if (quizRepository.existsById(id)) {
            if (canCurrUserDeleteQuiz(id)) {
                quizRepository.deleteById(id);
                throw new ResponseStatusException(HttpStatus.NO_CONTENT);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private boolean canCurrUserDeleteQuiz(Long quizId) {
        return quizRepository.findById(quizId).get().getUser().getId()
                .equals(
                        userRepository.findByEmail(
                                ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                                        .getUsername()
                        ).getId()
                );
    }
}


//    @PostMapping (path = "api/quizzes")
//    public Quiz createQuiz(@Valid @RequestBody Quiz quizBody) throws Exception {
//        if (quizBody.getOptions().length < 2 || quizBody.getText().equals("") || quizBody.getTitle().equals("")) {
//           throw new ResponseStatusException(
//                   HttpStatus.BAD_REQUEST
//           );
//        }
////        quizzes.add(quizBody);
//        quizBody.setId(questionRepository.findAll().size() + 1);
//        if (quizBody.getAnswer() == null) {
//            quizBody.setAnswer(new int[0]);
//        }
//        questionRepository.save(quizBody);
//        return quizBody;
//    }
//
//    @PostMapping(path = "/api/quizzes/{id}/solve")
//    public ResponseAnswer postAnswer(@PathVariable("id") @Min(1) int id, @RequestBody(required = false) Answer answer) {
////        if (quizzes.size() < id) {
//        if (questionRepository.findAll().size() < id) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "Quiz not found"
//            );
//        }
////        if (answer == null && quizzes.get(id - 1).getAnswer() == null) {
////            return new ResponseAnswer(true, "Congratulations, you're right!");
////        }
////        if (answer == null && quizzes.get(id - 1).getAnswer().length == 0) {
////            return new ResponseAnswer(true, "Congratulations, you're right!");
////        }
////        if (answer.answer.length == 0 && quizzes.get(id - 1).getAnswer() == null) {
////            return new ResponseAnswer(true, "Congratulations, you're right!");
////        }
////        if (answer == null && quizzes.get(id - 1).getAnswer() != null) {
////            return new ResponseAnswer(false, "Wrong answer! Please, try again.");
////        }
//
//        Quiz quiz = questionRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
//        if (answer == null && quiz.getAnswer() == null) {
//            return new ResponseAnswer(true, "Congratulations, you're right!");
//        }
//        if (answer == null && quiz.getAnswer().length == 0) {
//            return new ResponseAnswer(true, "Congratulations, you're right!");
//        }
//        if (answer.answer.length == 0 && quiz.getAnswer() == null) {
//            return new ResponseAnswer(true, "Congratulations, you're right!");
//        }
//        if (answer == null && quiz.getAnswer() != null) {
//            return new ResponseAnswer(false, "Wrong answer! Please, try again.");
//        }
//        Arrays.sort(answer.answer);
//        Arrays.sort(quiz.getAnswer());
//        if (Arrays.equals(answer.answer, quiz.getAnswer())) {
//            return new ResponseAnswer(true, "Congratulations, you're right!");
//        } else {
//            return new ResponseAnswer(false, "Wrong answer! Please, try again.");
//        }
//    }
//
//    @GetMapping(path = "/api/quizzes")
//    public List<Quiz> getQuiz() {
////        return quizzes;
//        return questionRepository.findAll();
//    }
//
//    @GetMapping(path = "/api/quizzes/{id}")
//    public ResponseEntity<Quiz> getQuiz(@PathVariable int id) {
//        if (questionRepository.findAll().size() < id) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "Quiz not found"
//            );
//        }
////        QuizBody quiz = questionRepository.getOne((id));
////        System.out.println("0");
////        questionRepository.findById(id);
////        System.out.println("1");
//        Quiz quiz = questionRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
////        System.out.println("2");
//        return ResponseEntity.ok(quiz);
////        return quiz;
//    }
//
//    @PostMapping (path = "/actuator/shutdown")
//    public void exitApplication() {
//        System.exit(0);
//    }
//
//    @DeleteMapping (path = "/api/quizzes/{id}")
//    public void deleteQuiz(@PathVariable int id) {
//
//    }
//
//    private static final String BAD_REQUEST_MESSAGE = "Exception: bad quiz body";
//
//
////    @ExceptionHandler(MethodArgumentNotValidException.class)
////    @ResponseStatus(HttpStatus.BAD_REQUEST)
////    public void except() {
////        throw new ResponseStatusException(
////                HttpStatus.BAD_REQUEST, "Quiz not found");
////    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public HashMap<String, String> handleMethodArgumentNotValidException(Exception e) {
//        HashMap<String, String> response = new HashMap<>();
//        response.put("message", BAD_REQUEST_MESSAGE);
//        response.put("error", e.getClass().getSimpleName());
//        return response;
//    }
//
////    @ResponseStatus(HttpStatus.BAD_REQUEST)
////    @ExceptionHandler(MethodArgumentNotValidException.class)
////    public Map<String, String> handleValidationExceptions(
////            MethodArgumentNotValidException ex) {
////        Map<String, String> errors = new HashMap<>();
////        ex.getBindingResult().getAllErrors().forEach((error) -> {
////            String fieldName = ((FieldError) error).getField();
////            String errorMessage = error.getDefaultMessage();
////            errors.put(fieldName, errorMessage);
////        });
////        return errors;
////    }

//}

//class Answer {
//    public int[] answer;
//
//    public int[] getAnswer() {
//        return answer;
//    }
//
//    public void setAnswer(int[] answer) {
//        this.answer = answer;
//    }
//}

//class ResponseAnswer {
//    private boolean success = false;
//    private String feedback;
//
//    ResponseAnswer(boolean success, String feedback) {
//        this.success = success;
//        this.feedback = feedback;
//    }
//
//    public void setFeedback(String feedback) {
//        this.feedback = feedback;
//    }
//
//    public String getFeedback() {
//        return feedback;
//    }
//
//    public boolean isSuccess() {
//        return success;
//    }
//
//    public void setSuccess(boolean success) {
//        this.success = success;
//    }
//}
