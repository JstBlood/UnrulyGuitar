package server.services;

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

        if(newValue == null || isNullOrEmpty(newValue.toString())) {
            return HttpStatus.BAD_REQUEST;
        }

        HttpStatus res = updateSwitch(component, newValue, card);
        if (res.equals(HttpStatus.BAD_REQUEST)) return res;

        cardRepo.saveAndFlush(card);

        forceRefresh(card);

        return HttpStatus.OK;
    }

    private HttpStatus updateSwitch(String component, Object newValue, Card card) {
        switch (component) {
            case "title":
                String newTitle = String.valueOf(newValue);
                if(isNullOrEmpty(newTitle)) {
                    return HttpStatus.BAD_REQUEST;
                }
                card.title = newTitle;
                return HttpStatus.OK;

            case "parentCardList":
                long parentCardListId = Long.parseLong(String.valueOf(newValue));
                Optional<CardList> parentCardList = cardListRepo.findById(parentCardListId);
                if (parentCardList.isEmpty()) {
                    return HttpStatus.BAD_REQUEST;
                }
                card.parentCardList = parentCardList.get();
                return HttpStatus.OK;

            case "index":
                int newIndex = Integer.parseInt(String.valueOf(newValue));
                if (newIndex < 0) {
                    return HttpStatus.BAD_REQUEST;
                }
                card.index = newIndex;
                return HttpStatus.OK;

            case "dragAndDrop":
                long targetId = Long.parseLong(String.valueOf(newValue));
                Optional<Card> optionalTargetCard = cardRepo.findById(targetId);

                if (optionalTargetCard.isEmpty()) {
                    return HttpStatus.BAD_REQUEST;
                }

                Card targetCard = optionalTargetCard.get();
                long targetCardListId = targetCard.parentCardList.id;

                Optional<CardList> targetCardList = cardListRepo.findById(targetCardListId);
                if (targetCardList.isEmpty()) {
                    return HttpStatus.BAD_REQUEST;
                }

                cardRepo.shiftCardsUp(card.index, card.parentCardList.id);

                card.parentCardList = targetCardList.get();

                cardRepo.shiftCardsDown(targetCard.index, targetCard.parentCardList.id);
                card.index = targetCard.index;

                return HttpStatus.OK;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private void forceRefresh(Card card) {
        boards.forceRefresh(card.parentCardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
