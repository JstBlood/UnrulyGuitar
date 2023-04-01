package server.services;

import java.util.Objects;
import java.util.Optional;

import commons.CardList;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import server.database.CardListRepository;

@Service
public class CardListService {
    private final CardListRepository cardListRepo;
    private final BoardsService boards;

    public CardListService(CardListRepository cardListRepo, BoardsService boards) {
        this.cardListRepo = cardListRepo;
        this.boards = boards;
    }

    public HttpStatus add(CardList cardList, String username, String password) {
        if (cardList == null || cardList.parentBoard == null) {
            return HttpStatus.BAD_REQUEST;
        }

        if (isNullOrEmpty(cardList.title)) {
            return HttpStatus.BAD_REQUEST;
        }

        cardListRepo.save(cardList);
        forceRefresh(cardList);

        return HttpStatus.CREATED;
    }

    public HttpStatus delete(long id, String username, String password) {
        Optional<CardList> optionalCardList = cardListRepo.findById(id);

        if (id < 0) {
            return HttpStatus.BAD_REQUEST;
        }

        if (optionalCardList.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        CardList cardList = optionalCardList.get();

        cardListRepo.deleteById(id);
        forceRefresh(cardList);

        return HttpStatus.OK;
    }

    public HttpStatus update(long id, String component, Object newValue, String username, String password) {
        Optional<CardList> optionalCardList = cardListRepo.findById(id);

        if (id < 0) {
            return HttpStatus.BAD_REQUEST;
        }

        if (optionalCardList.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        CardList cardList = optionalCardList.get();

        if (Objects.isNull(newValue)) {
            return HttpStatus.BAD_REQUEST;
        }


        HttpStatus res = handleSwitch(component, newValue, cardList);

        if (res.equals(HttpStatus.BAD_REQUEST))
            return res;

        cardListRepo.saveAndFlush(cardList);
        forceRefresh(cardList);

        return res;
    }

    private HttpStatus handleSwitch(String component, Object newValue, CardList cardList) {
        HttpStatus res = null;

        switch (component) {
            //Only use of updateCardList
            case "title":
                res = updateTitle(cardList, newValue);
                break;

            //If we ever want to change List Indexes
            case "index":
                res = updateIndex(cardList, newValue);
                break;

            default:
                res = HttpStatus.BAD_REQUEST;
                break;
        }

        return res;
    }

    private HttpStatus updateTitle(CardList cardList, Object newValue) {
        var newValueString = newValue.toString().trim();

        if (isNullOrEmpty(newValueString)) {
            return HttpStatus.BAD_REQUEST;
        }

        cardList.title = newValueString;

        return HttpStatus.OK;
    }

    private HttpStatus updateIndex(CardList cardList, Object newValue) {
        int newValueInt = Integer.parseInt(newValue.toString());

        if (newValueInt < 0) {
            return HttpStatus.BAD_REQUEST;
        }

        cardList.index = newValueInt;

        return HttpStatus.OK;
    }

    //TODO: Move DRAG AND DROP handlers to here
    public void forceRefresh(CardList cardList) {
        //TODO: add functionality for only refreshing a certain cardList

        boards.forceRefresh(cardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
