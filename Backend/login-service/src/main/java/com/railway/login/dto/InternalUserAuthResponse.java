
package com.railway.login.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InternalUserAuthResponse {

    private Long userId;

    private String email;

    private String passwordHash;

    private String role;
}
