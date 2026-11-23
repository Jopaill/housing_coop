package com.jordanpaille.housing.coop.dao.memberlist;

import com.jordanpaille.housing.coop.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@AllArgsConstructor
@Repository
public class MemberListDaoImpl implements MemberListDao {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Member> getMemberList() {
        String sql = """
                SELECT
                    a.civic_number,
                    p.first_name,
                    p.last_name,
                    p.phone,
                    p.username AS "EMAIL",
                    p.is_member,
                    admin.admin_name,
                    c.committee_name,
                    r.responsibility_name,
                    c2.committee_name AS "RESPONSIBILITY_COMMITTEE_NAME"
                FROM
                    apartments a
                    LEFT JOIN person_apartment pa ON a.apartment_id = pa.apartment_id
                    LEFT JOIN persons p ON pa.person_id = p.person_id
                    LEFT JOIN admin_person ap ON p.person_id = ap.person_id
                    LEFT JOIN admins admin ON ap.admin_id = admin.admin_id
                    LEFT JOIN person_committee pc ON p.person_id = pc.person_id
                    LEFT JOIN committees c ON pc.committee_id = c.committee_id
                    LEFT JOIN person_responsibility pr ON p.person_id = pr.person_id
                    LEFT JOIN responsibilities r ON pr.responsibility_id = r.responsibility_id
                    LEFT JOIN committees c2 ON r.committee_id = c2.committee_id
                ORDER BY a.civic_number
                """;
        Map<String, Member> civicNumberToMemberMap = jdbc.query(sql, rs -> {
            Map<String, Member> civicNumberToMemberMapConstruct = new HashMap<>();

            while (rs.next()) {
                String currentCivicNumber = rs.getString("CIVIC_NUMBER");

                Member member = civicNumberToMemberMapConstruct.get(currentCivicNumber);

                if (null == member) {
                    boolean isPresent = null != rs.getString("FIRST_NAME");
                    member = new Member.Builder()
                            .civicNumber(rs.getString("CIVIC_NUMBER"))
                            .firstName(rs.getString("FIRST_NAME"))
                            .lastName(isPresent ? rs.getString("LAST_NAME") : "Locataire Externe")
                            .phone(rs.getString("PHONE"))
                            .email(rs.getString("EMAIL"))
                            .isMember(rs.getBoolean("IS_MEMBER"))
                            .admin(rs.getString("ADMIN_NAME"))
                            .build();
                    civicNumberToMemberMapConstruct.put(currentCivicNumber, member);
                }
                member.addCommitteeIfNotNullAndNotAlreadyPresent(rs.getString("COMMITTEE_NAME"));
                member.addResponsibilityIfNotNullAndNotAlreadyPresent(rs.getString("RESPONSIBILITY_NAME"), rs.getString("RESPONSIBILITY_COMMITTEE_NAME"));
            }
            return civicNumberToMemberMapConstruct;
        });
        List<Member> orderedMemberList = new ArrayList<>(civicNumberToMemberMap.values());

        orderedMemberList.sort(Comparator.comparing(Member::civicNumber));

        return orderedMemberList;
    }
}



















