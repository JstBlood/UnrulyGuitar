package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Entry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public String title;
    public String colour;
    public String description;
    public int fontSize;
    public String fontDecoration;

    @ManyToOne(cascade = CascadeType.PERSIST)
    public CardList parent;

    @OneToMany(cascade = CascadeType.PERSIST)
    public List<Entry> subentry;

    @ManyToOne(cascade = CascadeType.PERSIST)
    public Entry subparent;

    @ManyToMany(cascade = CascadeType.PERSIST)
    public List<Tag> tags;

    @SuppressWarnings("unused")
    protected Entry() {

    }

    public Entry(String title, String colour, String description, int fontSize, String fontDecoration, CardList parent,
                 List<Tag> tags) {
        this.colour = colour;
        this.title = title;
        this.description = description;
        this.fontSize = fontSize;
        this.fontDecoration = fontDecoration;
        this.parent = parent;
        this.subentry = new ArrayList<>();
        this.tags = tags;
    }

    public void addSubentry(Entry e) {
        subentry.add(e);
        e.subparent = this;
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
