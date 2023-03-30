package server.services;

import java.util.Optional;

import commons.Card;
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
        forceRefresh(card);

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

        try {
            card.getClass().getField(component).set(card, newValue);
        } catch (Exception e) {
            return HttpStatus.BAD_REQUEST;
        }

        cardRepo.saveAndFlush(card);

        forceRefresh(card);

        return HttpStatus.OK;
    }

    public HttpStatus update(long id, String component, Object newValue) {
        if(cardRepo.findById(id).isEmpty())
            return HttpStatus.NOT_FOUND;

        Card edit = cardRepo.findById(id).get();

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
        if(cardRepo.findById(id).isEmpty())
            return HttpStatus.NOT_FOUND;

        Card edit = cardRepo.findById(id).get();

        edit.index = newValue;

        cardRepo.saveAndFlush(edit);

        if(!silent)
            forceRefresh(edit);

        return HttpStatus.OK;
    }

    public HttpStatus updateParent(long id, long newParent, boolean silent){
        if(cardListRepo.findById(newParent).isEmpty())
            return HttpStatus.NOT_FOUND;
        if(cardRepo.findById(id).isEmpty())
            return HttpStatus.NOT_FOUND;

        Card edit = cardRepo.findById(id).get();


        var oldParent = edit.parentCardList.id;

        var cEdit = cardListRepo.findById(oldParent).get();
        cEdit.cards.remove(edit);

        cardListRepo.saveAndFlush(cEdit);



        edit.parentCardList = cardListRepo.findById(newParent).get();

        cardRepo.saveAndFlush(edit);


        cEdit = cardListRepo.findById(newParent).get();
        cEdit.cards.add(edit);

        cardListRepo.saveAndFlush(cEdit);

        if(!silent)
            forceRefresh(edit);

        return HttpStatus.OK;
    }

    //TODO: Move DRAG AND DROP handlers to here


    private void forceRefresh(Card card) {
        boards.forceRefresh(card.parentCardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
