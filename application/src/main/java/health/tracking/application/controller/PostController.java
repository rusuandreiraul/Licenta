package health.tracking.application.controller;

import health.tracking.application.dto.PostRequestDTO;
import health.tracking.application.dto.PostResponseDTO;
import health.tracking.application.repository.PostRepository;
import health.tracking.application.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/add-post")
    public ResponseEntity<?> addPost(@RequestBody PostRequestDTO dto){
        try {
            PostResponseDTO res = postService.add(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
