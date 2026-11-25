package com.jordanpaille.housing.coop.services.memberlist;

import com.jordanpaille.housing.coop.entity.MemberDoc;
import com.jordanpaille.housing.coop.entity.MemberDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MemberDtoTransformer {
    List<MemberDoc> transformMemberDtoList(List<MemberDto> memberDocList) {
        Map<String, MemberDoc> civicNumberToEntityMap = new LinkedHashMap<>();

        for (MemberDto memberDto : memberDocList) {

            MemberDoc currentMemberDoc = civicNumberToEntityMap.computeIfAbsent(
                    memberDto.civicNumber(),
                    civicNumber -> MemberDoc.builder()
                            .civicNumber(civicNumber)
                            .firstName(memberDto.firstName())
                            .lastName(memberDto.lastName())
                            .phone(memberDto.phone())
                            .email(memberDto.email())
                            .isMember(memberDto.isMember())
                            .admin(memberDto.adminName())
                            .committeeResponsibilityMap(new HashMap<>())
                            .build()
            );

            currentMemberDoc.addCommitteeIfNotNullAndNotAlreadyPresent(memberDto.committee());
            currentMemberDoc.addResponsibilityIfNotNullAndNotAlreadyPresent(memberDto.responsibility(), memberDto.responsibilityCommittee());
        }
        List<MemberDoc> memberList = new ArrayList<>(civicNumberToEntityMap.values());

        return memberList;
    }
}

//                            .builder()
//                            .civicNumber(civicNumber)
//                            .firstName(memberDto.firstName())
//                            .lastName(memberDto.lastName())
//                            .phone(memberDto.phone())
//                            .email(memberDto.email())
//                            .isMember(memberDto.isMember())
//                            .admin(memberDto.adminName())
//                            .build());
