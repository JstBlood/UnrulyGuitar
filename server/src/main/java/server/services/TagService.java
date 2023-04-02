package server.services;

import java.util.Optional;

import commons.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import server.database.TagRepository;

@Service
public class TagService implements StandardEntityService<Tag, Long> {
    private final TagRepository tagRepo;
    private final BoardsService boardsService;

    public TagService(TagRepository tagRepo, BoardsService boardsService) {
        this.tagRepo = tagRepo;
        this.boardsService = boardsService;
    }

    public HttpStatus add(Tag tag, String username, String password) {
        if (tag == null || tag.parentBoard == null || isNullOrEmpty(tag.name)) {
            return HttpStatus.BAD_REQUEST;
        }

        System.out.println(tag.id);

        tagRepo.save(tag);

        //forceRefresh(tag);

        return HttpStatus.CREATED;
    }

    public HttpStatus update(Long id, String component, Object newValue, String username, String password) {
        return null;
    }

    public HttpStatus delete(Long id, String username, String password) {
        Optional<Tag> optionalTag = tagRepo.findById(id);

        if(optionalTag.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        Tag tag = optionalTag.get();

        tagRepo.deleteById(id);

        forceRefresh(tag);

        return HttpStatus.OK;
    }

    private void forceRefresh(Tag tag) {
        boardsService.forceRefresh(tag.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
