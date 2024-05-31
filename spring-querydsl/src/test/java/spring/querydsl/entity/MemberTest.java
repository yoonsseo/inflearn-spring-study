package spring.querydsl.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
//@Commit
class MemberTest {
    @Autowired
    EntityManager em;

    @Test
    public void testEntity() {
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

        em.flush(); // 영속성 컨텍스트 -> 쿼리
        em.close(); // 영속성 컨텍스트 초기화

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("team = " + member.getTeam());
            System.out.println("\n");
        }
    }
}