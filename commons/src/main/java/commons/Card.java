package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
    public List<Entry> entries = new ArrayList<>();

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

    public void addEntry(Entry newEntry) {
        entries.add(newEntry);
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
