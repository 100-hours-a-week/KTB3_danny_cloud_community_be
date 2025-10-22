package com.ktb.community.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReIssueRefreshTokenDto {
    @JsonProperty("refresh_token")
    public String refreshToken;
}
