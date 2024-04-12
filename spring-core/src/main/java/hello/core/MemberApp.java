package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemberApp {
    public static void main(String[] args) {
//        첫 번째
//        MemberService memberService = new MemberServiceImpl();

//        두 번째
//        AppConfig appConfig = new AppConfig();
//        MemberService memberService = appConfig.memberService();
        //AppConfig에서 memberService를 달라고 하면 memberService 인터페이스를 준다
        //memberService 안에는 memberServiceImpl이 들어있고
        //memberServiceImpl 객체를 생성하면서 MemoryMemberRepository 사용할 거라고 주입

//      세 번째
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
//      스프링은 모두 ApplicationContext로 시작한다
//      스프링 컨테이너라고 생각하면 됨
//      ApplicationContext가 모두 관리 객체들 @Bean
//      AppConfig에 있는 환경 설정 정보를 가지고 관리
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
//      Config에서 memberService 꺼낼 건데
//      기본적으로 이름은 메소드 이름으로 등록
//      두번째 인자로 반환 타입

        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);
        System.out.println("new member = " + member.getName());
        System.out.println("find Member = " + findMember.getName());
    }
}
