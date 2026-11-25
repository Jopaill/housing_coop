package com.jordanpaille.housing.coop.entity;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Builder
@Slf4j
public record MemberDoc(
        String civicNumber,
        String firstName,
        String lastName,
        String phone,
        String email,
        boolean isMember,
        boolean isEmpty,
        String admin,
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
}
