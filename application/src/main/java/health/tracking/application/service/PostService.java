package health.tracking.application.service;


import health.tracking.application.dto.PostRequestDTO;
import health.tracking.application.dto.PostResponseDTO;
import health.tracking.application.entities.Post;
import health.tracking.application.entities.User;
import health.tracking.application.mapper.PostMapper;
import health.tracking.application.repository.PostRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostMapper postMapper;

    @Autowired
    UserRepository userRepository;

    public PostResponseDTO add(PostRequestDTO dto) {
        Post p=new Post();
        User u=userRepository.findByEmailOrUsername(dto.getUsername(), dto.getUsername());
        if(u==null) {
            throw new RuntimeException("Utilizatorul cu username: " + dto.getUsername() + " nu a fost gasit!");
        }
            p.setContent(dto.getContent());
            p.setPublishDate(LocalDate.now());
            p.setUser(u);
           Post savedPost= postRepository.save(p);
            return postMapper.toDto(savedPost);
        }
}
