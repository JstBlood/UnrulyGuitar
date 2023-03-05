package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Tag {
    @Id
    public String name;
    public String colour;

    @ManyToOne(cascade = CascadeType.PERSIST)
    public Board parent;

    @ManyToMany(cascade = CascadeType.PERSIST)
    public List<Entry> entries;

    @SuppressWarnings("unused")
    protected Tag() {

    }

    public Tag(String name, String colour, Board parent) {
        this.colour = colour;
        this.name = name;
        this.parent = parent;
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
