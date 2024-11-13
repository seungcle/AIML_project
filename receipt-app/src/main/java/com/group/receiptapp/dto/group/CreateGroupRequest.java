package com.group.receiptapp.dto.group;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateGroupRequest {

    @NotEmpty(message = "Company name cannot be empty")
    private String name;

}