package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity(name = "Card")
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    /**
     * Here index is not unique per board, but we can create an embeddedId / IdClass to do that
     */
    public int index;
    @Column(nullable = false)
    public String title;
    public String description;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "board_id",
            nullable = false)
    public Board parentBoard;

    @OneToMany(mappedBy = "parentCard",
            cascade = CascadeType.ALL)
    public List<Entry> entries;

    @SuppressWarnings("unused")
    private Card() {}

    /**
     * @param title The card's title.
     * @param description The card's description
     * @param parentBoard The card's parent board.
     * The index field is initialized with the number of cards already on the board,
     * since this card is added at the end of the card list
     */
    public Card(String title, String description, Board parentBoard) {
        this.title = title;
        this.description = description;
        this.parentBoard = parentBoard;
        index = parentBoard.cards.size();
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
