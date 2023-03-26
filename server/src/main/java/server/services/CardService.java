package server.services;

import commons.Card;
import org.springframework.http.HttpStatus;
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
        if (card == null || card.parentCardList == null) {
            return HttpStatus.NOT_FOUND;
        }
        if(isNullOrEmpty(card.title)) {
            return HttpStatus.BAD_REQUEST;
        }

        cardRepo.saveAndFlush(card);
        forceRefresh(card);

        return HttpStatus.OK;
    }

    public HttpStatus update(long id, String component, String newValue) {
        Card card = cardRepo.findById(id);

        if(card == null) {
            return HttpStatus.NOT_FOUND;
        }

        if(isNullOrEmpty(card.title)) {
            return HttpStatus.BAD_REQUEST;
        }

        try {
            card.getClass().getField(component).set(card, newValue);
        } catch (Exception e) {
            return HttpStatus.BAD_REQUEST;
        }

        cardRepo.saveAndFlush(card);
        forceRefresh(card);

        return HttpStatus.OK;
    }

    public HttpStatus delete(long id) {
        Card card = cardRepo.findById(id);

        if(card == null) {
            return HttpStatus.NOT_FOUND;
        }

        cardRepo.deleteById(id);
        forceRefresh(card);

        return HttpStatus.OK;
    }

    public void forceRefresh(Card card) {
        boardsController.forceRefresh(card.parentCardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
