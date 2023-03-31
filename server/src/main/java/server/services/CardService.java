package server.services;

import java.util.Objects;
import java.util.Optional;

import commons.Card;
import commons.CardList;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.database.CardListRepository;
import server.database.CardRepository;

@Service
public class CardService implements StandardEntityService<Card, Long> {
    private final CardRepository cardRepo;
    private final CardListRepository cardListRepo;
    private final BoardsService boards;

    public CardService(CardRepository cardRepo, BoardsService boards, CardListRepository cardListRepo) {
        this.cardRepo = cardRepo;
        this.boards = boards;
        this.cardListRepo = cardListRepo;
    }

    public HttpStatus add(Card card, String username, String password) {
        if (card == null || card.parentCardList == null || isNullOrEmpty(card.title)) {
            return HttpStatus.BAD_REQUEST;
        }

        cardRepo.save(card);
        //forceRefresh(card);

        return HttpStatus.CREATED;
    }

    @Transactional
    public HttpStatus delete(Long id, String username, String password) {
        Optional<Card> optionalCard = cardRepo.findById(id);

        if(optionalCard.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        Card card = optionalCard.get();

        cardRepo.deleteById(id);

        cardRepo.shiftCardsUp(card.index, card.parentCardList.id);

        forceRefresh(card);

        return HttpStatus.OK;
    }

    public HttpStatus update(Long id, String component, Object newValue, String username, String password) {
        Optional<Card> optionalCard = cardRepo.findById(id);

        if(optionalCard.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        Card card = optionalCard.get();

        String newValueString = String.valueOf(newValue);

        HttpStatus res = null;

        switch (component) {
            case "title":
                res = updateTitle(newValueString, card);
                break;
            case "parentCardList":
                res = updateParentCardList(Long.parseLong(newValueString), card);
                break;
            case "index":
                res = updateIndex(Integer.parseInt(newValueString), card);
                break;

            case "dragAndDrop":
                res = dragAndDrop(Long.parseLong(newValueString), card);
                break;

            case "listDragAndDrop":
                res = listDragAndDrop(Long.parseLong(newValueString), card);
                break;
            default:
                res = HttpStatus.BAD_REQUEST;
                break;
        }

        if(!res.equals(HttpStatus.OK)) {
            return res;
        }

        cardRepo.saveAndFlush(card);

        forceRefresh(card);

        return HttpStatus.OK;
    }

    public HttpStatus updateTitle(String newValue, Card card) {
        if(isNullOrEmpty(newValue)) {
            return HttpStatus.BAD_REQUEST;
        }
        card.title = newValue;
        return HttpStatus.OK;
    }

    public HttpStatus updateParentCardList(long newValue, Card card) {
        Optional<CardList> parentCardList = cardListRepo.findById(newValue);
        if (parentCardList.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }
        card.parentCardList = parentCardList.get();
        return HttpStatus.OK;
    }

    public HttpStatus updateIndex(int newValue, Card card) {
        if (newValue < 0) {
            return HttpStatus.BAD_REQUEST;
        }
        card.index = newValue;
        return HttpStatus.OK;
    }

    @Transactional
    public HttpStatus dragAndDrop(long newValue, Card card) {
        Optional<Card> optionalTargetCard = cardRepo.findById(newValue);

        if (optionalTargetCard.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        Card targetCard = optionalTargetCard.get();
        long targetCardListId = targetCard.parentCardList.id;

        Optional<CardList> optionalTargetCardList = cardListRepo.findById(targetCardListId);
        if (optionalTargetCardList.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        CardList targetCardList = optionalTargetCardList.get();

        cardRepo.shiftCardsUp(card.index, card.parentCardList.id);
        cardRepo.shiftCardsDown(targetCard.index, targetCard.parentCardList.id);

        card.parentCardList = targetCardList;
        card.index = targetCard.index;

        return HttpStatus.OK;
    }

    @Transactional
    public HttpStatus listDragAndDrop(long newValue, Card card) {
        Optional<CardList> optionalTargetList = cardListRepo.findById(newValue);

        if (optionalTargetList.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        CardList targetList = optionalTargetList.get();

        cardRepo.shiftCardsUp(card.index, card.parentCardList.id);

        if(Objects.equals(targetList, card.parentCardList)) {
            card.index = targetList.cards.size() - 1;
        }
        else {
            card.parentCardList = targetList;
            card.index = targetList.cards.size();
        }

        return HttpStatus.OK;
    }

    private void forceRefresh(Card card) {
        boards.forceRefresh(card.parentCardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
