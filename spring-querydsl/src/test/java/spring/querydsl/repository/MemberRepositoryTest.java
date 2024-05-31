package spring.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import spring.querydsl.dto.MemberSearchCondition;
import spring.querydsl.dto.MemberTeamDto;
import spring.querydsl.entity.Member;
import spring.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

//        List<Member> result1 = memberJpaRepository.findAll();
        List<Member> result1 = memberRepository.findAll();
        assertThat(result1).containsExactly(member);


//        List<Member> result2 = memberJpaRepository.findByUsername("member1");
        List<Member> result2 = memberRepository.findByUsername("member1");
        assertThat(result2).containsExactly(member);
    }

    @Test
    public void searchTest() {
        Team tA = new Team("tA");
        em.persist(tA);
        Team tB = new Team("tB");
        em.persist(tB);

        Member m1 = new Member("m1", 10, tA);
        em.persist(m1);
        Member m2 = new Member("m2", 20, tA);
        em.persist(m2);
        Member m3 = new Member("m3", 30, tB);
        em.persist(m3);
        Member m4 = new Member("m4", 40, tB);
        em.persist(m4);

        MemberSearchCondition condition = new MemberSearchCondition();
//        condition.setAgeGoe(30);
        condition.setAgeLoe(40);
        condition.setTeamName("tB");

//        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(condition);
        List<MemberTeamDto> result = memberRepository.search(condition);

        assertThat(result).extracting("username").containsExactly("m3", "m4");
    }

    @Test
    public void searchPageImpl() {
        Team tA = new Team("tA");
        em.persist(tA);
        Team tB = new Team("tB");
        em.persist(tB);

        Member m1 = new Member("m1", 10, tA);
        em.persist(m1);
        Member m2 = new Member("m2", 20, tA);
        em.persist(m2);
        Member m3 = new Member("m3", 30, tB);
        em.persist(m3);
        Member m4 = new Member("m4", 40, tB);
        em.persist(m4);

        MemberSearchCondition condition = new MemberSearchCondition();
//        condition.setAgeGoe(30);
//        condition.setAgeLoe(40);
//        condition.setTeamName("tB");
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition, pageRequest);

        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getContent()).extracting("username").containsExactly("m1", "m2", "m3");

        for (MemberTeamDto memberTeamDto : result) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }
}