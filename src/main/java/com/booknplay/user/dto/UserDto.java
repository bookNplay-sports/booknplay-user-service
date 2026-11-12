package com.booknplay.user.dto;

import com.booknplay.user.utility.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    Long id;
    String name;
    String email;
    String phone;
    Role role;
}
