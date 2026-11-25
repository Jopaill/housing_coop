package com.jordanpaille.housing.coop.dao.memberlist;

import com.jordanpaille.housing.coop.entity.MemberDto;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@AllArgsConstructor
@Repository
public class MemberListDaoImpl implements MemberListDao {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<MemberDto> getMemberList() {
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
                    c2.committee_name AS "RESPONSIBILITY_COMMITTEE"
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

        return jdbc.query(sql, rs -> {
            List<MemberDto> memberListToReturn = new ArrayList<>();

            while (rs.next()) {
                MemberDto memberDto = MemberDto.builder()
                        .civicNumber(rs.getString("civic_number"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .phone(rs.getString("phone"))
                        .email(rs.getString("EMAIL"))
                        .isMember(rs.getBoolean("is_member")) // Note: getBoolean results to 'false' when value is null (see getBoolean() docs)
                        .adminName(rs.getString("admin_name"))
                        .committee(rs.getString("committee_name"))
                        .responsibility(rs.getString("responsibility_name"))
                        .responsibilityCommittee(rs.getString("RESPONSIBILITY_COMMITTEE"))
                        .build();
                memberListToReturn.add(memberDto);
            }
            return memberListToReturn;
        });
    }
}



















