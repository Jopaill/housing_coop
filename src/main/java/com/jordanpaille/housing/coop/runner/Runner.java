package com.jordanpaille.housing.coop.runner;

import com.jordanpaille.housing.coop.dao.memberlist.MemberListDao;
import com.jordanpaille.housing.coop.entity.Member;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Component
@Profile("dev") // Keep this Runner out of production
public class Runner implements CommandLineRunner {

    private static final String del = "*************************************************************************";
    private final MemberListDao memberListDao;

    @Override
    public void run(String... args) {
        log.info(del);
        log.info(del);
        log.info(del);
        log.info(del);
        log.info(del);
        List<Member> list = memberListDao.getMemberList();
        log.info("The size of list is: {}", list.size());

        for (Member row : list) {
            log.info(row.toString());
            log.info(del);
        }
    }
}
