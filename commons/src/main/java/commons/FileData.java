package commons;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="FileData")
@Data
public class FileData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public String name;
    public String type;
    @Lob
    @Column(name="filedata")
    private byte[] fileData;
    public FileData() {}
    public FileData(String name, String type, byte[] fileData) {
        this.name = name;
        this.type = type;
        this.fileData = fileData;
    }
    public byte[] getFileData() {
        return fileData;
    }

    public String getType() {
        return type;
    }
    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }
}
