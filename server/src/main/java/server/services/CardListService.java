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

    public boolean update(CardList cardList) {
        if(cardList == null) {
            return false;
        }

//        if(!pwd.hasEditAccess(password, edit.parentBoard.key))
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        try {
//            edit.getClass().getField(component).set(edit, newValue);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }

        cardListRepo.saveAndFlush(cardList);
        forceRefresh(cardList);

        return true;
    }

    public boolean delete(long id) {
        CardList c = cardListRepo.findById(id);
        if (id < 0 || c == null) {
            return false;
        }
        cardListRepo.deleteById(id);
        forceRefresh(c);
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
