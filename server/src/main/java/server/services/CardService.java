package server.services;

import commons.Card;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.api.BoardsController;
import server.database.CardRepository;

@Service
public class CardService implements StandardEntityService<Card> {
    private final CardRepository cardRepo;
    private final BoardsController boardsController;

    public CardService(CardRepository cardRepo, BoardsController boardsController) {
        this.cardRepo = cardRepo;
        this.boardsController = boardsController;
    }

    public HttpStatus add(Card card) {
        if (card == null || isNullOrEmpty(card.title) || card.parentCardList == null) {
            return HttpStatus.BAD_REQUEST;
        }

        cardRepo.saveAndFlush(card);
        forceRefresh(card);

        return HttpStatus.OK;
    }

    public ResponseEntity<Card> get(long id) {
        Card card = cardRepo.findById(id);

        if (card == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(card);
    }

    public HttpStatus update(long id, String component, String newValue) {
        if(cardRepo.findById(id) == null)
            return HttpStatus.NOT_FOUND;

        Card edit = cardRepo.findById(id);

        try {
            edit.getClass().getField(component).set(edit, newValue);
        } catch (Exception e) {
            return HttpStatus.BAD_REQUEST;
        }

        cardRepo.saveAndFlush(edit);

        forceRefresh(edit);

        return HttpStatus.OK;
    }

    public HttpStatus delete(long id) {
        if(cardRepo.findById(id) == null)
            return HttpStatus.NOT_FOUND;

        String rem = cardRepo.findById(id).parentCardList.parentBoard.key;

        cardRepo.delete(cardRepo.findById(id));
        boardsController.forceRefresh(rem);

        return HttpStatus.OK;
    }

    public void forceRefresh(Card card) {
        boardsController.forceRefresh(card.parentCardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
