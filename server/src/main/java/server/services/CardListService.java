package server.services;

import commons.CardList;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import server.database.CardListRepository;

@Service
public class CardListService {
    private final CardListRepository cardListRepo;
    private final BoardsService boards;
    private final SocketRefreshService sockets;
    private final RepositoryBasedAuthService pwd;

    public CardListService(CardListRepository cardListRepo, BoardsService boards,
                           SocketRefreshService sockets, RepositoryBasedAuthService pwd) {
        this.cardListRepo = cardListRepo;
        this.boards = boards;
        this.sockets = sockets;
        this.pwd = pwd;
    }

    public HttpStatus add(CardList cardList, String username, String password) {
        if (cardList == null || cardList.parentBoard == null) {
            return HttpStatus.BAD_REQUEST;
        }

        if (isNullOrEmpty(cardList.title)) {
            return HttpStatus.BAD_REQUEST;
        }

        if(!pwd.hasEditAccess(username, password, cardList.parentBoard.key)) {
            return HttpStatus.FORBIDDEN;
        }

        cardListRepo.save(cardList);

        forceRefresh(cardList);

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

        var optionalCardList = cardListRepo.findById(id);

        if(optionalCardList.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        if(!pwd.hasEditAccess(username, password, optionalCardList.get().parentBoard.key)) {
            return HttpStatus.FORBIDDEN;
        }

        return HttpStatus.OK;
    }

    public HttpStatus flush(CardList cardList) {
        cardListRepo.saveAndFlush(cardList);
        forceRefresh(cardList);

        return HttpStatus.OK;
    }


    public void forceRefresh(CardList cardList) {
        boards.forceRefresh(cardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
