package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public String passwordHash;
    public boolean isPasswordProtected;

    public String title;
    public String colour;

    @OneToMany
    public List<CardList> lists;

    @OneToMany
    public List<Tag> tags;

    @SuppressWarnings("unused")
    protected Board() {

    }

    public Board(String title, String colour) {
        this.colour = colour;
        this.title = title;
    }

    public void protect(String password) {
        passwordHash = password;
        isPasswordProtected = true;
    }

    public void unlock() {
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
