package client.scenes;

import javax.inject.Inject;

import client.utils.ServerUtils;
import commons.Card;

public class TagsPopupCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Card card;

    @Inject
    public TagsPopupCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void prepare(Card card) {
        this.card = card;

    }
}
