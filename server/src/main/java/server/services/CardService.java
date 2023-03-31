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

import javax.swing.text.html.Option;

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

        Card sourceCard = optionalCard.get();

        if(newValue == null || isNullOrEmpty(newValue.toString())) {
            return HttpStatus.BAD_REQUEST;
        }

        HttpStatus res = updateSwitch(component, newValue, sourceCard);
        if (res.equals(HttpStatus.BAD_REQUEST)) return res;

        cardRepo.saveAndFlush(sourceCard);

        forceRefresh(sourceCard);

        return HttpStatus.OK;
    }

    @Transactional
    public HttpStatus updateSwitch(String component, Object newValue, Card sourceCard) {
        switch (component) {
            case "title":
                String newTitle = String.valueOf(newValue);
                if(isNullOrEmpty(newTitle)) {
                    return HttpStatus.BAD_REQUEST;
                }
                sourceCard.title = newTitle;
                return HttpStatus.OK;

            case "parentCardList":
                long parentCardListId = Long.parseLong(String.valueOf(newValue));
                Optional<CardList> parentCardList = cardListRepo.findById(parentCardListId);
                if (parentCardList.isEmpty()) {
                    return HttpStatus.BAD_REQUEST;
                }
                sourceCard.parentCardList = parentCardList.get();
                return HttpStatus.OK;

            case "index":
                int newIndex = Integer.parseInt(String.valueOf(newValue));
                if (newIndex < 0) {
                    return HttpStatus.BAD_REQUEST;
                }
                sourceCard.index = newIndex;
                return HttpStatus.OK;

            case "dragAndDrop":
                long targetId = Long.parseLong(String.valueOf(newValue));
                Optional<Card> optionalTargetCard = cardRepo.findById(targetId);

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

                cardRepo.shiftCardsUp(sourceCard.index, sourceCard.parentCardList.id);
                cardRepo.shiftCardsDown(targetCard.index, targetCard.parentCardList.id);

                sourceCard.parentCardList = targetCardList;
                sourceCard.index = targetCard.index;

                return HttpStatus.OK;

            case "listDragAndDrop":
                long targetListId = Long.parseLong(String.valueOf(newValue));
                Optional<CardList> optionalTargetList = cardListRepo.findById(targetListId);

                if (optionalTargetList.isEmpty()) {
                    return HttpStatus.BAD_REQUEST;
                }

                CardList targetList = optionalTargetList.get();

                cardRepo.shiftCardsUp(sourceCard.index, sourceCard.parentCardList.id);

                if(Objects.equals(targetList, sourceCard.parentCardList)) {
                    sourceCard.index = targetList.cards.size() - 1;
                }
                else {
                    sourceCard.parentCardList = targetList;
                    sourceCard.index = targetList.cards.size();
                }

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
