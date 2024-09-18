
package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.validator.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateRequest {
    @NameConstraint
    private String fullName;
    @PhoneConstraint
    private String phoneNumber;
    @AddressConstraint
    private String address;
    @Email
    @EmailConstraint
    private String email;
}
