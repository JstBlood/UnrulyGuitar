package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.awt.*;
import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name = "Tag")
@Table(name = "tag", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    @Column(nullable = false)
    public String name;

    public Color color;

    @ManyToOne
    @JoinColumn(name = "board_id",
            nullable = false)
    public Board parentBoard;

    @ManyToOne
    @JoinColumn(name = "card_id")
    public Card parentCard;

    @SuppressWarnings("unused")
    protected Tag() {}

    public Tag(String name, Color color) {
        this.color = color;
        this.name = name;
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
