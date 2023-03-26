package server.services;

import commons.CardList;
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

    public boolean add(CardList cardList) {
        if (cardList == null || isNullOrEmpty(cardList.title) || cardList.parentBoard == null) {
            return false;
        }
        cardListRepo.save(cardList);
        forceRefresh(cardList);
        return true;
    }

    public boolean update(long id, String component, String newValue) {

        CardList cardList = cardListRepo.findById(id);
        if(cardList == null || isNullOrEmpty(component) || isNullOrEmpty(newValue)) {
            return false;
        }

        switch (component) {
            case "title":
                if (isNullOrEmpty(newValue)) {
                    return false;
                }
                cardList.title = newValue;
                break;
            case "index":
                int newIndex = Integer.parseInt(newValue);
                if (newIndex < 0) {
                    return false;
                }
                cardList.index = newIndex;
                break;
            default:
                return false;
        }

        cardListRepo.saveAndFlush(cardList);
        forceRefresh(cardList);
        return true;
    }

    public boolean delete(long id) {
        CardList cardList = cardListRepo.findById(id);
        if (cardList == null) {
            return false;
        }
        cardListRepo.delete(cardList);
        forceRefresh(cardList);
        return true;
    }


    public void forceRefresh(CardList cardList) {
        //TODO: add functionality for only refreshing a certain cardList

        boardsController.forceRefresh(cardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
