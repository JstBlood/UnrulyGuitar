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
    private final SocketRefreshService sockets;


    public CardService(CardRepository cardRepo, BoardsService boards, CardListRepository cardListRepo,
                       SocketRefreshService sockets) {
        this.cardRepo = cardRepo;
        this.boards = boards;
        this.cardListRepo = cardListRepo;
        this.sockets = sockets;
    }

    public HttpStatus add(Card card, String username, String password) {
        if (card == null || card.parentCardList == null || isNullOrEmpty(card.title)) {
            return HttpStatus.BAD_REQUEST;
        }

        cardRepo.save(card);
        sockets.broadcastRemoval(card);
        forceRefresh(card);

        return HttpStatus.CREATED;
    }

    @Transactional
    public HttpStatus delete(Long id, String username, String password) {
        HttpStatus res = prepare(id, username, password);

        if (!res.equals(HttpStatus.OK))
            return res;

        Card card = cardRepo.findById(id).get();

        cardRepo.deleteById(id);

        cardRepo.shiftCardsUp(card.index, card.parentCardList.id);

        forceRefresh(card);

        return HttpStatus.OK;
    }

    public HttpStatus update(Long id, String component, Object newValue, String username, String password) {
//        if (!prepare(id, username, password).equals(HttpStatus.OK))
//            return prepare(id, username, password);
//
//        Card card = cardRepo.findById(id).get();
//        String newValueString = String.valueOf(newValue);
//
//        if(newValueString.isEmpty()) {
//            return HttpStatus.BAD_REQUEST;
//        }
//
//        return flush(card);

        //No use for this method, should I delete?
        return HttpStatus.BAD_REQUEST;
    }

    public HttpStatus updateTitle(Long id, Object newValue, String username, String password) {
        if (!prepare(id, username, password).equals(HttpStatus.OK))
            return prepare(id, username, password);

        Card card = cardRepo.findById(id).get();
        String newValueString = String.valueOf(newValue).trim();

        if(newValueString.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        card.title = newValueString;

        return flush(card);
    }

    public HttpStatus updateDescription(Long id, Object newValue, String username, String password) {
        if (!prepare(id, username, password).equals(HttpStatus.OK))
            return prepare(id, username, password);

        Card card = cardRepo.findById(id).get();
        String newValueString = String.valueOf(newValue).trim();

        if(newValueString.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        card.description = newValueString;

        return flush(card);
    }

    public HttpStatus updateIndex(Long id, Object newValue, String username, String password) {
        if (!prepare(id, username, password).equals(HttpStatus.OK))
            return prepare(id, username, password);

        Card card = cardRepo.findById(id).get();
        int newValueInt = Integer.parseInt(String.valueOf(newValue).trim());

        if(newValueInt < 0) {
            return HttpStatus.BAD_REQUEST;
        }

        card.index = newValueInt;

        return flush(card);
    }

    public HttpStatus updateParent(Long id, Object newValue, String username, String password) {
        if (!prepare(id, username, password).equals(HttpStatus.OK))
            return prepare(id, username, password);

        Card card = cardRepo.findById(id).get();
        long newValueLong = Long.parseLong(String.valueOf(newValue).trim());

        if(newValueLong < 0) {
            return HttpStatus.BAD_REQUEST;
        }

        Optional<CardList> parentCardList = cardListRepo.findById(newValueLong);

        if (parentCardList.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        card.parentCardList = parentCardList.get();

        return flush(card);
    }

    @Transactional
    public HttpStatus updateDragAndDrop(Long id, Object newValue, String username, String password) {
        if (!prepare(id, username, password).equals(HttpStatus.OK))
            return prepare(id, username, password);

        Card card = cardRepo.findById(id).get();
        long newValueLong = Long.parseLong(String.valueOf(newValue).trim());

        if(newValueLong < 0) {
            return HttpStatus.BAD_REQUEST;
        }

        Optional<Card> optionalTargetCard = cardRepo.findById(newValueLong);

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

        return flush(card);
    }

    @Transactional
    public HttpStatus updateListDragAndDrop(Long id, Object newValue, String username, String password) {
        if (!prepare(id, username, password).equals(HttpStatus.OK))
            return prepare(id, username, password);

        Card card = cardRepo.findById(id).get();
        long newValueLong = Long.parseLong(String.valueOf(newValue).trim());

        if(newValueLong < 0) {
            return HttpStatus.BAD_REQUEST;
        }

        Optional<CardList> optionalTargetList = cardListRepo.findById(newValueLong);

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

        return flush(card);
    }

    public HttpStatus prepare(Long id, String username, String password) {
        if (id < 0) {
            return HttpStatus.BAD_REQUEST;
        }

        Optional<Card> optionalCard = cardRepo.findById(id);

        if(optionalCard.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        return HttpStatus.OK;
    }

    public HttpStatus flush(Card card) {
        cardRepo.saveAndFlush(card);
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
