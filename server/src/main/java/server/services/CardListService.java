package server.services;

import java.util.Optional;

import commons.CardList;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import server.database.CardListRepository;

@Service
public class CardListService {
    private final CardListRepository cardListRepo;
    private final BoardsService boards;
    private final SocketRefreshService sockets;

    public CardListService(CardListRepository cardListRepo, BoardsService boards, SocketRefreshService sockets) {
        this.cardListRepo = cardListRepo;
        this.boards = boards;
        this.sockets = sockets;
    }

    public HttpStatus add(CardList cardList, String username, String password) {
        if (cardList == null || cardList.parentBoard == null) {
            return HttpStatus.BAD_REQUEST;
        }

        if (isNullOrEmpty(cardList.title)) {
            return HttpStatus.BAD_REQUEST;
        }

        cardListRepo.save(cardList);

        return HttpStatus.CREATED;
    }

    public HttpStatus delete(long id, String username, String password) {
        if (!prepare(id, username, password).equals(HttpStatus.OK)) {
            return prepare(id, username, password);
        }

        CardList cardList = cardListRepo.findById(id).get();

        cardListRepo.deleteById(id);
        sockets.broadcastRemoval(cardList);
        forceRefresh(cardList);

        return HttpStatus.OK;
    }

    public HttpStatus update(long id, String component, Object newValue, String username, String password) {
//        if (!prepare(id, username, password).equals(HttpStatus.OK))
//            return prepare(id, username, password);
//
//        CardList cardList = cardListRepo.findById(id).get();
//
//        return flush(cardList)

        //Unused, delete?
        return HttpStatus.BAD_REQUEST;
    }

    public HttpStatus updateTitle(long id, Object newValue, String username, String password) {
        if (!prepare(id, username, password).equals(HttpStatus.OK))
            return prepare(id, username, password);

        CardList cardList = cardListRepo.findById(id).get();
        String newValueString = newValue.toString().trim();

        if (isNullOrEmpty(newValueString)) {
            return HttpStatus.BAD_REQUEST;
        }

        cardList.title = newValueString;

        return flush(cardList);
    }

    public HttpStatus prepare(long id, String username, String password) {
        if (id < 0) {
            return HttpStatus.BAD_REQUEST;
        }

        return HttpStatus.OK;
    }

    public HttpStatus flush(CardList cardList) {
        cardListRepo.saveAndFlush(cardList);
        forceRefresh(cardList);

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
