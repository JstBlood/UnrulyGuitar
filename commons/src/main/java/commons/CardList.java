package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name = "CardList")
@Table(name = "cardlist")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "@list_id")
public class CardList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Column(nullable = false)
    public String title;

    @ManyToOne
    @JoinColumn(name = "board_id",
            nullable = false)
    public Board parentBoard;

    @OneToMany(mappedBy = "parentCardList",
            cascade = CascadeType.ALL)
    public List<Card> cards = new ArrayList<>();

    @SuppressWarnings("unused")
    protected CardList() {}

    /**
     * @param title The card's title.
     * @param parentBoard The card's parent board.
     * The index field is initialized with the number of cards already on the board,
     * since this card is added at the end of the card list
     */
    public CardList(String title, Board parentBoard) {
        this.title = title;
        this.parentBoard = parentBoard;
    }

    /**
     * method for adding cards to a list
     * @param newCard card to add
     */
    public void addCard(Card newCard) {
        cards.add(newCard);
    }
    /**
     * method for removing cards from a list
     * @param cardToRemove card to remove
     */
    public void removeCard(Card cardToRemove) {
        cards.remove(cardToRemove);
    }
    /**
     * equals method for cardLists
     * @param obj object to compare
     * @return boolean true/false
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
    /**
     * to string method for cardLists
     * @return string representation of the list
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
