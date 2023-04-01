package server.services;

import java.util.Optional;

import commons.Tag;
import org.springframework.http.HttpStatus;
import server.database.TagRepository;

public class TagService implements StandardEntityService<Tag, Long> {
    private final TagRepository tagRepo;

    public TagService(TagRepository tagRepo) {
        this.tagRepo = tagRepo;
    }

    public HttpStatus add(Tag tag, String username, String password) {
        if (tag == null || tag.parentBoard == null || isNullOrEmpty(tag.name)) {
            return HttpStatus.BAD_REQUEST;
        }

        tagRepo.save(tag);

        //TODO: send a message

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

        //TODO: send a message

        return HttpStatus.OK;
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
