package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name = "Task")
@Table(name = "task")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "@task_id")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public int index;

    @Column(nullable = false)
    public String title;

    public boolean isDone;

    @ManyToOne
    @JoinColumn(name = "card_id",
            nullable = false)
    public Card parentCard;
    @SuppressWarnings("unused")
    protected Task() {}

    /**
     * @param title The subentry's text.
     * @param parentCard The subentry's parent entry.
     */
    public Task(String title, Card parentCard) {
        this.title = title;
        this.parentCard = parentCard;
        this.isDone = false;
        if(parentCard != null) index = parentCard.tasks.size();
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
