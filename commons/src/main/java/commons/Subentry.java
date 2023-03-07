package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name = "Subentry")
@Table(name = "subentry")
public class Subentry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public int index;

    @Column(nullable = false)
    public String text;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "card_id",
            nullable = false)
    public Entry parentEntry;
    @SuppressWarnings("unused")
    private Subentry() {}

    /**
     * @param text The subentry's text.
     * @param parentEntry The subentry's parent entry.
     */
    public Subentry(String text, Entry parentEntry) {
        this.text = text;
        this.parentEntry = parentEntry;
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
