package nr.localmovies.movieinfoapi;

import javax.persistence.*;

@Entity
@Table(name = "Movies")
public class MovieInfoEntity {

    @Id
    private String id;

    @Column(name = "data")
    private String data;

    public MovieInfoEntity(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public MovieInfoEntity() {
        // Default constructor
    }

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
