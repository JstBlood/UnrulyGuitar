package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name = "Entry")
@Table(name = "entry")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public int index;
    @Column(nullable = false)
    public String title;
    public String description;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "cardList_id",
            nullable = false)
    public CardList parentCardList;

    @OneToMany(mappedBy = "parentCard",
            cascade = CascadeType.ALL)
    public List<Task> tasks = new ArrayList<>();


    @SuppressWarnings("unused")
    private Card() {}

    /**
     * @param title The entry's text.
     * @param description The entry's description
     * @param parentCardList The entry's parentCard.
     */
    public Card(String title, String description, CardList parentCardList) {
        this.title = title;
        this.description = description;
        this.parentCardList = parentCardList;
        index = parentCardList.cards.size();
    }

    public void addTask(Task newTask) {
        tasks.add(newTask);
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
