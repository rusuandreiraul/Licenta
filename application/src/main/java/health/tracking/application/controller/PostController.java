package health.tracking.application.controller;

import health.tracking.application.dto.PostRequestDTO;
import health.tracking.application.dto.PostResponseDTO;
import health.tracking.application.repository.PostRepository;
import health.tracking.application.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/get-posts/{username}")
    public ResponseEntity<?> getPosts(@PathVariable String username){
        List<PostResponseDTO> posts=postService.findAllPostsByDate(username,LocalDate.now());
        if(posts.isEmpty()){
            return ResponseEntity.ok(new ArrayList<PostResponseDTO>());
        }
        return ResponseEntity.ok(posts);
    }

    @GetMapping("posts/{username}")
    public ResponseEntity<?> getPostsUser(@PathVariable String username){
        List<PostResponseDTO> posts=postService.findAllPostsByUser(username);
        if(posts.isEmpty()){
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(posts);
    }

}
