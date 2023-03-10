package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name = "Subentry")
@Table(name = "subentry")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public int index;

    @Column(nullable = false)
    public String title;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "card_id",
            nullable = false)
    public Card parentCard;
    @SuppressWarnings("unused")
    private Task() {}

    /**
     * @param title The subentry's text.
     * @param parentCard The subentry's parent entry.
     */
    public Task(String title, Card parentCard) {
        this.title = title;
        this.parentCard = parentCard;
        index = parentCard.tasks.size();
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
