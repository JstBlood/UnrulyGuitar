package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name = "Board")
@Table(name = "board", uniqueConstraints = @UniqueConstraint(columnNames = {"key"}))
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public String key;
    public String passwordHash;
    public boolean isPasswordProtected;
    public String title;
    public String description;
    public int backgroundColorR = 0;
    public int backgroundColorG = 0;
    public int backgroundColorB = 0;

    @OneToMany(mappedBy = "parentBoard",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    public List<Card> cards = new ArrayList<>();

    @ManyToMany(mappedBy = "boards", fetch = FetchType.EAGER)
    public Set<User> users = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
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
     * @param description The board's description
     * @param backgroundColor The board's background color.
     */
    public Board(String key, String title, String description, Color backgroundColor) {
        this.key = key;
        this.title = title;
        this.description = description;
        if(backgroundColor != null) {
            this.backgroundColorR = backgroundColor.getRed();
            this.backgroundColorG = backgroundColor.getGreen();
            this.backgroundColorB = backgroundColor.getBlue();
        }
    }

    public void addCard(Card newCard) {
        cards.add(newCard);
    }

    public void addUser(User newUser) {
        users.add(newUser);
    }

    public void addTag(Tag newTag) {
        tags.add(newTag);
    }

    /**
     * Protect the board using a password.
     * @param password The board's password.
     */
    public void setPassword(String password) {
        passwordHash = password;
        isPasswordProtected = true;
    }

    /**
     * Make the board password-free by removing the password.
     */
    public void deletePassword() {
        passwordHash = null;
        isPasswordProtected = false;
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
