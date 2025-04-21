package org.example.pageModel;
import java.util.Date;
import lombok.Data;

@Data
public class userdetailDto {
    Integer id;
    String username;
    String password;
    String email;
    Date createdAt;
}
