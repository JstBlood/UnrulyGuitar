package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

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
    @Column(nullable = false)
    public String key;
    public String passwordHash;
    public boolean isPasswordProtected;
    public String title;
    public String description;
    public String backgroundColor;

    @OneToMany(mappedBy = "parentBoard",
            cascade = CascadeType.ALL)
    public List<Card> cards = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "board_tag",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    public Set<Tag> tags = new HashSet<>();

    @SuppressWarnings("unused")
    private Board() {
    }

    /**
     * @param key The key set when creating the board, used to join the board. (is UNIQUE)
     * @param title The board's title.
     * @param description The board's description
     * @param backgroundColor The board's background color.
     */
    public Board(String key, String title, String description, String backgroundColor) {
        this.key = key;
        this.title = title;
        this.description = description;
        this.backgroundColor = backgroundColor;
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
        passwordHash = "";
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
