package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity(name = "Board")
@Table(name = "board", uniqueConstraints = @UniqueConstraint(columnNames = {"key"}))
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
                property = "@board_id")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    @Column(nullable = false)
    public String key;
    public String title;

    @OneToMany(mappedBy = "parentBoard",
            cascade = CascadeType.ALL)
    public List<CardList> cardLists = new ArrayList<>();

    @ManyToMany(mappedBy = "boards")
    public Set<User> users = new HashSet<>();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "board_tag",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    public Set<Tag> tags = new HashSet<>();

    @SuppressWarnings("unused")
    protected Board() {
    }

    /**
     * @param key The key set when creating the board, used to join the board. (is UNIQUE)
     * @param title The board's title.
     */
    public Board(String key, String title) {
        this.key = key;
        this.title = title;
    }

    public void addCard(CardList newCardList) {
        cardLists.add(newCardList);
    }

    public void addUser(User newUser) {
        users.add(newUser);
    }

    public void addTag(Tag newTag) {
        tags.add(newTag);
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
