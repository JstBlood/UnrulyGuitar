package server.services;

import java.util.Objects;
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

        tagRepo.save(tag);

        forceRefresh(tag);

        return HttpStatus.CREATED;
    }

    public HttpStatus update(Long id, String component, Object newValue, String username, String password) {
        Optional<Tag> optionalTag = tagRepo.findById(id);

        if(optionalTag.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        Tag tag = optionalTag.get();

        HttpStatus res = handleSwitch(component, newValue, tag);

        if (res.equals(HttpStatus.BAD_REQUEST))
            return res;

        tagRepo.saveAndFlush(tag);

        forceRefresh(tag);

        return res;
    }

    private HttpStatus handleSwitch(String component, Object newValue, Tag tag) {
        HttpStatus res = null;

        switch (component) {
            case "name":
                res = updateName(newValue, tag);
                break;

            default:
                res = HttpStatus.BAD_REQUEST;
                break;

        }
        return res;
    }

    private HttpStatus updateName(Object newValue, Tag tag) {
        if(Objects.isNull(newValue)) {
            return HttpStatus.BAD_REQUEST;
        }

        String newValueString = String.valueOf(newValue).trim();

        if(isNullOrEmpty(newValueString)) {
            return HttpStatus.BAD_REQUEST;
        }

        tag.name = newValueString;

        return HttpStatus.OK;
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
