package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name = "Entry")
@Table(name = "entry")
public class Entry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public int index;
    @Column(nullable = false)
    public String text;
    public Color textColor;
    public int fontSize;
    public String fontDecoration;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "card_id",
            nullable = false)
    public Card parentCard;

    @OneToMany(mappedBy = "parentEntry",
            cascade = CascadeType.ALL)
    public List<Subentry> subEntries = new ArrayList<>();


    @SuppressWarnings("unused")
    private Entry() {}

    /**
     * @param text The entry's text.
     * @param textColor The entry's textColor.
     * @param fontSize The entry's fontSize.
     * @param fontDecoration The entry's fontDecoration.
     * @param parentCard The entry's parentCard.
     */
    public Entry(String text, Color textColor, int fontSize, String fontDecoration, Card parentCard) {
        this.text = text;
        this.textColor = textColor;
        this.fontSize = fontSize;
        this.fontDecoration = fontDecoration;
        this.parentCard = parentCard;
        index = parentCard.entries.size();
    }

    public void addSubentry(Subentry newSubentry) {
        subEntries.add(newSubentry);
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
