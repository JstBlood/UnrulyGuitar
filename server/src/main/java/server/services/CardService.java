package server.services;

import commons.Card;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import server.api.BoardsController;
import server.database.CardListRepository;
import server.database.CardRepository;

@Service
public class CardService implements StandardEntityService<Card> {
    private final CardRepository cardRepo;
    private final CardListRepository cardListRepo;
    private final BoardsController boardsController;

    public CardService(CardRepository cardRepo, BoardsController boardsController, CardListRepository cardListRepo) {
        this.cardRepo = cardRepo;
        this.boardsController = boardsController;
        this.cardListRepo = cardListRepo;
    }

    public HttpStatus add(Card card) {
        if (card == null || isNullOrEmpty(card.title) || card.parentCardList == null) {
            return HttpStatus.BAD_REQUEST;
        }

        cardRepo.saveAndFlush(card);
        forceRefresh(card);

        return HttpStatus.OK;
    }

    public HttpStatus update(long id, String component, Object newValue) {
        if(cardRepo.findById(id) == null)
            return HttpStatus.NOT_FOUND;

        Card edit = cardRepo.findById(id);

        try {
            edit.getClass().getField(component).set(edit, newValue);
        } catch (Exception e) {
            return HttpStatus.BAD_REQUEST;
        }

        cardRepo.saveAndFlush(edit);

        forceRefresh(edit);

        return HttpStatus.OK;
    }

    public HttpStatus updateIndex(long id, int newValue, boolean silent) {
        if(cardRepo.findById(id) == null)
            return HttpStatus.NOT_FOUND;

        Card edit = cardRepo.findById(id);

        edit.index = newValue;

        cardRepo.saveAndFlush(edit);

        if(!silent)
            forceRefresh(edit);

        return HttpStatus.OK;
    }

    public HttpStatus updateParent(long id, long newParent, boolean silent){
        if(cardListRepo.findById(newParent) == null)
            return HttpStatus.NOT_FOUND;

        Card edit = cardRepo.findById(id);


        var oldParent = edit.parentCardList.id;

        var cEdit = cardListRepo.findById(oldParent);
        cEdit.cards.remove(edit);

        cardListRepo.saveAndFlush(cEdit);



        edit.parentCardList = cardListRepo.findById(newParent);

        cardRepo.saveAndFlush(edit);


        cEdit = cardListRepo.findById(newParent);
        cEdit.cards.add(edit);

        cardListRepo.saveAndFlush(cEdit);

        if(!silent)
            forceRefresh(edit);

        return HttpStatus.OK;
    }

    public HttpStatus delete(long id) {
        if(cardRepo.findById(id) == null)
            return HttpStatus.NOT_FOUND;

        String rem = cardRepo.findById(id).parentCardList.parentBoard.key;

        cardRepo.delete(cardRepo.findById(id));
        boardsController.forceRefresh(rem);

        return HttpStatus.OK;
    }

    //TODO: Move DRAG AND DROP handlers to here

    public void forceRefresh(Card card) {
        boardsController.forceRefresh(card.parentCardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
