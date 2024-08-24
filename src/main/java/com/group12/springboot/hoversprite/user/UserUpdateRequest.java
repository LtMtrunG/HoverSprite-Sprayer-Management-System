
package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.validator.EmailConstraint;
import com.group12.springboot.hoversprite.validator.NameConstraint;
import com.group12.springboot.hoversprite.validator.PasswordConstraint;
import com.group12.springboot.hoversprite.validator.PhoneConstraint;
import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateRequest {
    @PasswordConstraint
    private String password;
    @NameConstraint
    private String fullName;
    @PhoneConstraint
    private String phoneNumber;
    @NonNull
    private String address;
    @Email
    @EmailConstraint
    private String email;
}
