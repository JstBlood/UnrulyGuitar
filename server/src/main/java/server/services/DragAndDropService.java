package server.services;

import org.springframework.stereotype.Service;
import server.api.BoardsController;
import server.database.CardRepository;

@Service
public class DragAndDropService {
    private final CardRepository cardRepo;
    private final BoardsController boardsController;

    public DragAndDropService(CardRepository cardRepo, BoardsController boardsController) {
        this.cardRepo = cardRepo;
        this.boardsController = boardsController;
    }

    //TODO: Move DRAG AND DROP handlers to here

}
