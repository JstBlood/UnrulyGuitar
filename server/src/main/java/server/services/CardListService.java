package server.services;

import java.util.Optional;

import commons.CardList;
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

    public void add(CardList cardList) throws RuntimeException{
        if (cardList == null || cardList.parentBoard == null) {
            throw new RuntimeException("Invalid card list");
        }
        if (isNullOrEmpty(cardList.title)) {
            throw new RuntimeException("Card list title cannot be empty");
        }

        cardListRepo.save(cardList);
        forceRefresh(cardList);
    }

    public CardList get(long id) throws RuntimeException{
        Optional<CardList> optionalCardList = cardListRepo.findById(id);

        if (optionalCardList.isEmpty()) {
            throw new RuntimeException("CardList not found");
        }

        return optionalCardList.get();
    }

    public void delete(long id) throws RuntimeException {
        Optional<CardList> cardList = cardListRepo.findById(id);

        if (cardList.isEmpty()) {
            throw new RuntimeException("CardList not found with id: " + id);
        }

        cardListRepo.deleteById(id);
        forceRefresh(cardList.get());
    }

    public CardList update(long id, String component, String newValue) throws RuntimeException {
        Optional<CardList> optionalCardList = cardListRepo.findById(id);

        if (optionalCardList.isEmpty()) {
            throw new RuntimeException("CardList not found");
        }

        CardList cardList = optionalCardList.get();

        if (isNullOrEmpty(newValue)) {
            throw new RuntimeException("New value cannot be null or empty");
        }

        try {
            cardList.getClass().getField(component).set(cardList, newValue);
        } catch  (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Invalid field name: " + component);
        }

        cardListRepo.saveAndFlush(cardList);
        forceRefresh(cardList);
        return cardList;
    }

    public void forceRefresh(CardList cardList) {
        //TODO: add functionality for only refreshing a certain cardList

        boards.forceRefresh(cardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
