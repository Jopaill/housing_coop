package com.jordanpaille.housing.coop.entity;

import lombok.Builder;

@Builder
public record MemberDto(
        String civicNumber,
        String firstName,
        String lastName,
        String phone,
        String email,
        Boolean isMember,
        String adminName,
        String committee,
        String responsibility,
        String responsibilityCommittee
) {
}
