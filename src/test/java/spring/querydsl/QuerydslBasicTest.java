package spring.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spring.querydsl.entity.Member;
import spring.querydsl.entity.QMember;
import spring.querydsl.entity.QTeam;
import spring.querydsl.entity.Team;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static spring.querydsl.entity.QMember.*;
import static spring.querydsl.entity.QTeam.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

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

//        em.flush(); // 영속성 컨텍스트 -> 쿼리
//        em.close(); // 영속성 컨텍스트 초기화
    }

    @Test
    public void startJPQL() {
        //m1찾기
        Member findByJPQL = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "m1")
                .getSingleResult();

        assertThat(findByJPQL.getUsername()).isEqualTo("m1");
    }

    @Test
    public void startQuerydsl() {
//        JPAQueryFactory queryFactory = new JPAQueryFactory(em); // 위로 빼주었음
//        QMember m = new QMember("m"); // Q 타입 활용 방법1
//        QMember m = QMember.member; // Q 타입 활용 방법 2

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("m1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("m1");

    }

    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("m1")
                        .and(member.age.eq(10)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("m1");
    }

    @Test
    public void resultFetch() {
//        List<Member> fetch = queryFactory
//                .selectFrom(member)
//                .fetch();
//
//        Member fetchOne = queryFactory
//                .selectFrom(member)
//                .fetchOne();
//
//        Member fetchFirst = queryFactory
//                .selectFrom(member)
//                .fetchFirst(); //limit(1).fetchOne()과 동일

//        QueryResults<Member> fetchResults = queryFactory
//                .selectFrom(member)
//                .fetchResults();

//        long total = fetchResults.getTotal();
//        List<Member> content = fetchResults.getResults();

        long fetchCount = queryFactory
                .selectFrom(member)
                .fetchCount();
    }

    /**
     * 회원 정렬 순서
     * 1. 나이 내림차순 (desc)
     * 2. 회원 이름 올림차순 (asc)
     * 단, 2에서 회원 이름이 없으면 마지막에 출력 (nulls last)
     */
    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("m5", 200));
        em.persist(new Member("m6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.between(100, 200))
                .orderBy(
                        member.age.desc(),
                        member.username.asc().nullsLast()
                ).fetch();

        Member m5 = result.get(0);
        Member m6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(m5.getUsername()).isEqualTo("m5");
        assertThat(m6.getUsername()).isEqualTo("m6");
        assertThat(memberNull.getUsername()).isEqualTo(null);
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    public void paging1() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void paging2() {
        QueryResults<Member> fetchResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        assertThat(fetchResults.getTotal()).isEqualTo(4);
        assertThat(fetchResults.getLimit()).isEqualTo(2);
        assertThat(fetchResults.getOffset()).isEqualTo(1);
        assertThat(fetchResults.getResults().size()).isEqualTo(2);
    }

    @Test
    public void aggregation() {
        List<Tuple> fetch = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        Tuple tuple = fetch.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령 구하기
     */
    @Test
    public void group() {
        List<Tuple> result = queryFactory
                .select(
                        team.name,
                        member.age.avg()
                )
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple tA = result.get(0);
        Tuple tB = result.get(1);

        assertThat(tA.get(team.name)).isEqualTo("tA");
        assertThat(tA.get(member.age.avg())).isEqualTo(15);
        assertThat(tB.get(team.name)).isEqualTo("tB");
        assertThat(tB.get(member.age.avg())).isEqualTo(35);
    }

    /**
     * 팀A에 소속된 모든 회원 찾기
     */
    @Test
    public void join() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team) //두 번째 파라미터 team은 QTeam을 뜻한다
                .where(team.name.eq("tA"))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("m1", "m2");
    }

    /**
     * 세타 조인
     * 회원의 이름이 팀 이름과 같은 회원 조인
     */
    @Test
    public void thetaJoin() { // 연관관계 없이 하는 조인
        em.persist(new Member("tA"));
        em.persist(new Member("tB"));
        // 멤버인데 이름이 팀A, B인

        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();
        //DB가 최적화를 하겠지만 모든 회원과 모든 팀을 가져와서 조인하고 where 절에서 필터링

        assertThat(result)
                .extracting("username")
                .containsExactly("tA", "tB");
    }

    /**
     * 회원과 팀을 조인하면서 , 팀 이름이 tA인 팀만 조인 , 회원은 모두 조회
     * JPQL : SELECT m. t FROM Member m
     * LEFT JOIN m.team t ON t.name = "tA";
     */
    @Test
    public void joinOnFiltering() {
        List<Tuple> result = queryFactory
                .select(member, team) // select로 여러가지 타입을 뽑아서 tuple
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("tA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * 연관 관계가 없는 엔티티 외부 조인하는 경우
     * 회원 이름이 팀 이름과 같은 대상 외부 조인
     */
    @Test
    public void joinOnNoRelation() { // 연관관계 없이 하는 조인
        em.persist(new Member("tA"));
        em.persist(new Member("tB"));
        em.persist(new Member("tC"));
        // 멤버인데 이름이 팀A, B, C인

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team) // member.team이 아니라 그냥 team을 넣어서 막 조인
                .on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void noFetchJoin() {
        em.flush();
        em.clear();
        //영속성 컨텍스트 비우고 시작

        Member findMember = queryFactory.selectFrom(member)
                .where(member.username.eq("m1"))
                .fetchOne();
        // Member에서 Team이 LAZY로 되어있기 때문에
        // DB에서 조회할 때 Member만 조회되고 Team은 조회가 안 된다

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        //Member에서 Team이 이미 로딩되었는지 아닌지 확인
        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    @Test
    public void fetchJoin() {
        em.flush();
        em.clear();
        //영속성 컨텍스트 비우고 시작

        Member findMember = queryFactory.selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("m1"))
                .fetchOne();
        // Member에서 Team이 LAZY로 되어있기 때문에
        // DB에서 조회할 때 Member만 조회되고 Team은 조회가 안 된다

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        //Member에서 Team이 이미 로딩되었는지 아닌지 확인
        assertThat(loaded).as("페치 조인 적용").isTrue();
    }

    /**
     * 나이가 가장 많은 회원 조회
     */
    @Test
    public void subQuery() {
        QMember mSub = new QMember("mSub");

        List<Member> result = queryFactory.selectFrom(member)
                .where(member.age.eq(
                        select(mSub.age.max())
                                .from(mSub)
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(40);
    }

     /**
     * 나이가 평균 이상인 회원 조회
     */
    @Test
    public void subQueryGoe() {
        QMember mSub = new QMember("mSub");

        List<Member> result = queryFactory.selectFrom(member)
                .where(member.age.goe(
                        select(mSub.age.avg())
                                .from(mSub)
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(30, 40);
    }

    @Test
    public void subQueryIn() {
        QMember mSub = new QMember("mSub");

        List<Member> result = queryFactory.selectFrom(member)
                .where(member.age.in(
                        select(mSub.age)
                                .from(mSub)
                                .where(mSub.age.gt(10))
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(20, 30, 40);
    }

    @Test
    public void selectSubQuery() {
        QMember mSub = new QMember("mSub");

        List<Tuple> result = queryFactory
                .select(member.username,
                        select(mSub.age.avg())
                                .from(mSub))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void basicCase() {
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타")
                )
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void complexCase() {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20")
                        .when(member.age.between(21, 30)).then("21~30")
                        .otherwise("기타")
                )
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * 임의 순서대로 회원 출력하기
     */
    @Test
    public void rankPath() {
        NumberExpression<Integer> rankPath = new CaseBuilder()
                .when(member.age.between(0, 20)).then(2)
                .when(member.age.between(21, 30)).then(1)
                .otherwise(3);

        List<Tuple> result = queryFactory
                .select(member.username, member.age, rankPath)
                .from(member)
                .orderBy(rankPath.desc())
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            Integer rank = tuple.get(rankPath);
            System.out.println("username = " + username + " age = " + age + " rank = " +
                    rank);
        }
    }

    @Test
    public void constant() {
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * {username}_{age}
     */
    @Test
    public void concat() {
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

}









