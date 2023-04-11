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
    public Long id;
    @Column
    public int index;
    @Column(nullable = false)
    public String title;
    public String description;
    @Column
    public String file;

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

    /**
     * method for adding a task to a card
     * @param newTask
     */
    public void addTask(Task newTask) {
        tasks.add(newTask);
    }

    /**
     * method for setting a description of a card
     * @param description new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * method for setting the title of a card
     * @param title new title
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * method for setting the attachment of a card
     * @param file the card's attachment
     */
    public void setFile(String file){this.file=file;}

    /**
     * method for removing a tag on a card
     * @param tag
     */
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
    /**
     * equals method for cards
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
     * to string method for cards
     * @return string representation of the card
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
