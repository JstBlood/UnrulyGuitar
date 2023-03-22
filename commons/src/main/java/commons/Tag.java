package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity(name = "Tag")
@Table(name = "tag", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    @Column(nullable = false)
    public String name;
    public Color color;

    @ManyToMany(mappedBy = "tags")
    public Set<Board> boards = new HashSet<>();

    @SuppressWarnings("unused")
    protected Tag() {}

    public Tag(String name, Color color) {
        this.color = color;
        this.name = name;
    }

    public void addBoard(Board newBoard) {
        boards.add(newBoard);
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
