package com.jordanpaille.housing.coop.dao.memberlist;

import com.jordanpaille.housing.coop.entity.MemberDoc;

import java.util.List;

public interface MemberListDao {

    List<MemberDoc> getMemberList();
}
