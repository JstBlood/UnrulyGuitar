package server.services;

import java.util.Optional;

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
        if (card == null || card.parentCardList == null || isNullOrEmpty(card.title)) {
            return HttpStatus.BAD_REQUEST;
        }

        cardRepo.saveAndFlush(card);
        forceRefresh(card);

        return HttpStatus.OK;
    }

    public HttpStatus update(Long id, String component, Object newValue, String username, String password) {
        Optional<Card> optionalCard = cardRepo.findById(id);

        if(optionalCard.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        Card card = optionalCard.get();

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

    public HttpStatus delete(Long id, String username, String password) {
        Optional<Card> optionalCard = cardRepo.findById(id);

        if(optionalCard.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        Card card = optionalCard.get();

        cardRepo.deleteById(id);
        forceRefresh(card);

        return HttpStatus.OK;
    }

    private void forceRefresh(Card card) {
        boards.forceRefresh(card.parentCardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
