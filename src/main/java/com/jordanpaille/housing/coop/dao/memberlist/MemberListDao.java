package com.jordanpaille.housing.coop.dao.memberlist;

import com.jordanpaille.housing.coop.entity.MemberDto;

import java.util.List;

public interface MemberListDao {

    List<MemberDto> getMemberList();
}
