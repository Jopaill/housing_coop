package com.jordanpaille.housing.coop.dao.memberlist;

import com.jordanpaille.housing.coop.entity.Member;

import java.util.List;

public interface MemberListDao {

    List<Member> getMemberList();
}
