package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity(name = "CardList")
@Table(name = "cardlist")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "@list_id")
public class CardList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public int index;

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
        if(parentBoard != null) index = parentBoard.cardLists.size();
    }

    public void addCard(Card newCard) {
        cards.add(newCard);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
