package com.group12.springboot.hoversprite.dataTransferObject.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectRequest {
    @NonNull
    private String token;
}
