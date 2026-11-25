package com.jordanpaille.housing.coop.services.memberlist;

import com.jordanpaille.housing.coop.entity.MemberDoc;
import com.jordanpaille.housing.coop.entity.MemberDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberDtoTransformer {
    List<MemberDoc> transformMemberDtoList(List<MemberDto> memberDocList) {
        return new ArrayList<>();
    }
}
