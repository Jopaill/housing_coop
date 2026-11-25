package com.jordanpaille.housing.coop.entity;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public record MemberDoc(
        String civicNumber,
        String firstName,
        String lastName,
        String phone,
        String email,
        Boolean isMember,
        String adminName,
        Map<String, List<String>> committeeResponsibilityMap
) {
    public void addCommitteeIfNotNullAndNotAlreadyPresent(String committeeToAdd) {
        if (null != committeeToAdd) {
            committeeResponsibilityMap.computeIfAbsent(committeeToAdd, _ -> new ArrayList<>());
        }
    }

    public void addResponsibilityIfNotNullAndNotAlreadyPresent(String responsibility, String committee) {
        if (null == responsibility) {
            return;
        }
        if (null == committee) {
            log.error("Every responsibility should be attached to a committee. This should not occur. 'responsibility' = {}", responsibility);
            return;
        }
        List<String> responsibilities = committeeResponsibilityMap.computeIfAbsent(committee, _ -> new ArrayList<>());
        if (!responsibilities.contains(responsibility)) {
            responsibilities.add(responsibility);
        }
    }


    public static final class Builder {
        private String builderCivicNumber;
        private String builderFirstName;
        private String builderLastName;
        private String builderPhone;
        private String builderEmail;
        private Boolean builderIsMember;
        private String builderAdminName;
        private final Map<String, List<String>> builderCommitteeResponsibilityMap = new HashMap<>();

        public Builder() {
        }

        public Builder civicNumber(String civicNumber) {
            this.builderCivicNumber = civicNumber;
            return this;
        }

        public Builder firstName(String firstName) {
            this.builderFirstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.builderLastName = lastName;
            return this;
        }

        public Builder phone(String phone) {
            this.builderPhone = phone;
            return this;
        }

        public Builder email(String email) {
            this.builderEmail = email;
            return this;
        }

        public Builder isMember(Boolean isMember) {
            this.builderIsMember = isMember;
            return this;
        }

        public Builder admin(String adminName) {
            this.builderAdminName = adminName;
            return this;
        }

        public MemberDoc build() {
            return new MemberDoc(
                    builderCivicNumber,
                    builderFirstName,
                    builderLastName,
                    builderPhone,
                    builderEmail,
                    builderIsMember,
                    builderAdminName,
                    builderCommitteeResponsibilityMap
            );
        }
    }
}
