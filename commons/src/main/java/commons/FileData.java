package commons;

import javax.persistence.*;

import lombok.Data;

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

    @SuppressWarnings("unused")
    protected FileData() {}

    public FileData(String name, String type, byte[] fileData) {
        this.name = name;
        this.type = type;
        this.fileData = fileData;
    }
    public byte[] getFileData() {
        return fileData;
    }
}
