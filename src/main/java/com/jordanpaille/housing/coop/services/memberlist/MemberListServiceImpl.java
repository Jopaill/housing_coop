package com.jordanpaille.housing.coop.services.memberlist;

import com.jordanpaille.housing.coop.dao.memberlist.MemberListDao;
import com.jordanpaille.housing.coop.entity.MemberDoc;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class MemberListServiceImpl implements MemberListService {

    private final MemberListDao memberListDao;

    @Override
    public List<MemberDoc> getMemberList() {
        return memberListDao.getMemberList();
    }
}
