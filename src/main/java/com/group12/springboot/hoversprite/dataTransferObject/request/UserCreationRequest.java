package com.group12.springboot.hoversprite.dataTransferObject.request;

import com.group12.springboot.hoversprite.validator.PasswordConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationRequest {
    @Email
    @NotNull
    @Pattern(regexp = ".+(.com|.vn)$", message = "Email must end with .com or .vn")
    private String email;
    @Size(min = 2, message = "Password must have at least 2 characters.")
    @NotNull
    @PasswordConstraint
    private String password;
    @NotNull
    @Pattern(regexp = "^(?:[A-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểễếỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ][a-zàáâãèéêìíòóôõùúăđĩũơưạảấầẩẫậắằẳẵặẹẻẽềềểễếệỉịọỏốồổỗộớờởỡợụủứừửữựỳỵỷỹ]*\\s?)+$", message = "Each word should start with capital letters")
    private String fullName;
    @NotNull
    @Pattern(regexp = "^(0\\d{9}|\\+84\\d{9})$")
    private String phoneNumber;
    @NotNull
    private String address;
}
