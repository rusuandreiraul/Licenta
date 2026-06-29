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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<PostResponseDTO> findAllPostsByDate(String username,LocalDate now) {
        User u=userRepository.findByEmailOrUsername(username, username);
        if(u==null){
             return new ArrayList<>();
        }
        List<PostResponseDTO> listFinal=new ArrayList<>();
        Set<User> follow=u.getFollowing();
        List<Post> l=postRepository.findAllByPublishDateAndUserIn(now, follow);
        for(Post p: l){
            PostResponseDTO post=postMapper.toDto(p);
            post.setUrlImage(p.getUser().getUrlProfileImage());
            listFinal.add(post);
        }
        return listFinal;
    }

    public List<PostResponseDTO> findAllPostsByUser(String username) {
        User u=userRepository.findByEmailOrUsername(username,username);
        if(u==null){
            return new ArrayList<>();
        }
        List<Post> postList=postRepository.findAllByUser(u);
        return postList.stream().map(post->postMapper.toDto(post)).toList();
    }
}
