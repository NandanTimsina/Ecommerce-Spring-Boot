package com.Project.Ecommerce.security.response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class UserInfoResponse {
    private Long id;
    private String jwtToken;

    private String userName;
    private List<String> roles;

    public UserInfoResponse(Long id, String userName, List<String> roles,String jwtToken) {
        this.id=id;
        this.jwtToken = jwtToken;
        this.userName = userName;
        this.roles = roles;
    }

    public UserInfoResponse(Long id, String userName, List<String> roles) {
        this.id=id;
        this.userName = userName;
        this.roles = roles;
    }
}
