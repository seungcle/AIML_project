package com.group.receiptapp.web.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data

public class LoginForm {

    @NotEmpty(message = "이메일을 입력해 주세요.")
    @Email(message = "유효한 이메일 주소를 입력해 주세요.")
    private String email;

    @NotEmpty(message = "비밀번호를 입력해 주세요.")
    private String password;
}
