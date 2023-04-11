package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
    public Set<User> users = ConcurrentHashMap.newKeySet();

    @OneToMany(mappedBy = "parentBoard",
            cascade = CascadeType.ALL)
    public List<Tag> tags = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    public ColorPreset colors;

    @OneToOne(cascade = CascadeType.ALL)
    public ColorPreset cardListColors;

    @OneToMany(cascade = CascadeType.ALL)
    public List<ColorPreset> cardPresets = new ArrayList<>();

    @OneToOne
    public ColorPreset defaultPreset;

    public boolean isPasswordProtected = false;

    @JsonIgnore
    public String password = null;

    @SuppressWarnings("unused")
    public Board() {
    }

    /**
     * @param key The key set when creating the board, used to join the board. (is UNIQUE)
     * @param title The board's title.
     */
    public Board(String key, String title) {
        this.key = key;
        this.title = title;
    }

    /**
     * method for adding a new list to the board
     * @param newCardList
     */
    public void addCard(CardList newCardList) {
        cardLists.add(newCardList);
    }

    /**
     * method for adding a new User to a board
     * @param newUser
     */
    public void addUser(User newUser) {
        users.add(newUser);
    }

    /**
     * method for adding a tag to a board
     * @param newTag
     */
    public void addTag(Tag newTag) {
        tags.add(newTag);
    }

    /**
     * returns the default colour of the foreground
     * @return string hexadecimal value of default colour
     */
    public static String getDefaultForeground() { return "#1a4d1a"; }
    /**
     * returns the default colour of the background
     * @return string hexadecimal value of default colour
     */
    public static String getDefaultBackground() { return "#adaaaa"; }

    /**
     * equals method for boards
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
     * to string method for boards
     * @return string representation of the board
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
