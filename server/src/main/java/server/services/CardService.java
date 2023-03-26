package server.services;

import commons.Card;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import server.database.CardRepository;

@Service
public class CardService implements StandardEntityService<Card, Long> {
    private final CardRepository cardRepo;
    private final BoardsService boards;

    public CardService(CardRepository cardRepo, BoardsService boards) {
        this.cardRepo = cardRepo;
        this.boards = boards;
    }

    public HttpStatus add(Card card, String username, String password) {
        if (card == null || isNullOrEmpty(card.title) || card.parentCardList == null) {
            return HttpStatus.BAD_REQUEST;
        }

        cardRepo.saveAndFlush(card);
        forceRefresh(card);

        return HttpStatus.OK;
    }

    public HttpStatus update(Long id, String component, Object newValue, String username, String password) {
        if(cardRepo.findById(id) == null)
            return HttpStatus.NOT_FOUND;

        Card edit = cardRepo.findById((long)id);

        try {
            edit.getClass().getField(component).set(edit, newValue);
        } catch (Exception e) {
            return HttpStatus.BAD_REQUEST;
        }

        cardRepo.saveAndFlush(edit);

        forceRefresh(edit);

        return HttpStatus.OK;
    }

    public HttpStatus delete(Long id, String username, String password) {
        if(cardRepo.findById(id) == null)
            return HttpStatus.NOT_FOUND;

        String rem = cardRepo.findById((long)id).parentCardList.parentBoard.key;

        cardRepo.delete(cardRepo.findById((long)id));
        boards.forceRefresh(rem);

        return HttpStatus.OK;
    }

    private void forceRefresh(Card card) {
        boards.forceRefresh(card.parentCardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
