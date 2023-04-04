package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name = "Card")
@Table(name = "card")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "@card_id")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    @Column
    public int index;
    @Column(nullable = false)
    public String title;
    public String description;

    @ManyToOne
    @JoinColumn(name = "cardList_id",
            nullable = false)
    public CardList parentCardList;

    @OneToMany(mappedBy = "parentCard",
            cascade = CascadeType.ALL)
    public List<Task> tasks = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "card_tag",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    public Set<Tag> tags = ConcurrentHashMap.newKeySet();

    @OneToOne
    public ColorPreset colors;

    /**
     * @param title The entry's text.
     * @param description The entry's description
     * @param parentCardList The entry's parentCard.
     */
    public Card(String title, String description, CardList parentCardList) {
        this.title = title;
        this.description = description;
        this.parentCardList = parentCardList;
        if(parentCardList != null) index = parentCardList.cards.size();
    }

    @SuppressWarnings("unused")
    protected Card() {}

    public void addTask(Task newTask) {
        tasks.add(newTask);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void removeTag(Tag tag) {
        Iterator<Tag> it = tags.iterator();
        while(it.hasNext()) {
            Tag curr = it.next();
            if(curr.id == tag.id) {
                it.remove();
                break;
            }
        }
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
