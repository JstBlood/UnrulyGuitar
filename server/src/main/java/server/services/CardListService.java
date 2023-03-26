package server.services;

import commons.CardList;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import server.api.BoardsController;
import server.database.CardListRepository;

@Service
public class CardListService {
    private final CardListRepository cardListRepo;
    private final BoardsController boardsController;

    public CardListService(CardListRepository cardListRepo, BoardsController boardsController) {
        this.cardListRepo = cardListRepo;
        this.boardsController = boardsController;
    }

    public HttpStatus add(CardList cardList) {

        //TODO: decide if we want to keep these checks, because they are basically useless

        if (cardList == null || cardList.parentBoard == null) {
            return HttpStatus.NOT_FOUND;
        }
        if(isNullOrEmpty(cardList.title)) {
            return HttpStatus.BAD_REQUEST;
        }

        //TODO: cardList may have null values at the moment of saving in the repo (Ex: parent list)
        // and that gives an internal server error

        cardListRepo.save(cardList);
        forceRefresh(cardList);

        return HttpStatus.OK;
    }

    public HttpStatus update(long id, String component, String newValue) {
        CardList cardList = cardListRepo.findById(id);

        if(cardList == null) {
            return HttpStatus.NOT_FOUND;
        }

        if(isNullOrEmpty(newValue)) {
            return HttpStatus.BAD_REQUEST;
        }

        try {
            cardList.getClass().getField(component).set(cardList, newValue);
        } catch  (NoSuchFieldException | IllegalAccessException e) {
            return HttpStatus.BAD_REQUEST;
        }

        cardListRepo.saveAndFlush(cardList);
        forceRefresh(cardList);

        return HttpStatus.OK;
    }

    public HttpStatus delete(long id) {
        CardList cardList = cardListRepo.findById(id);

        if(cardList == null) {
            return HttpStatus.NOT_FOUND;
        }

        //TODO: always use deleteById, since the object may have been modified on the meantime,
        // but the id remains the same

        cardListRepo.deleteById(id);
        forceRefresh(cardList);

        return HttpStatus.OK;
    }


    public void forceRefresh(CardList cardList) {
        //TODO: add functionality for only refreshing a certain cardList

        boardsController.forceRefresh(cardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
