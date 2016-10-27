package nr.localmovies.movieinfoapi;

import javax.persistence.*;

@Entity
@Table(name = "Movies")
public class MovieInfoEntity {

    public MovieInfoEntity(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public MovieInfoEntity() {
    }

    @Id
    private String id;

    @Lob
    @Basic
    @Column(name = "data")
    private String data;

    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }
}
