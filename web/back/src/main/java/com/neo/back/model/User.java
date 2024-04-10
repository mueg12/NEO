package neo.back.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class User {
    @Id
    private String id;
    private String password;

    private String name;
    private String email;
    private Boolean isCertified;

    private String point;

}