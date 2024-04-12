package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.order.Order;
import hello.core.order.OrderService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OrderApp {
    public static void main(String[] args) {
//      첫 번째
//      MemberService memberService = new MemberServiceImpl(null);
//      OrderService orderService = new OrderServiceImpl(null, null);

//      두 번째
//        AppConfig appConfig = new AppConfig();
//        MemberService memberService = appConfig.memberService();
//        OrderService orderService = appConfig.orderService();

//      세 번째
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
        OrderService orderService = applicationContext.getBean("orderService", OrderService.class);

        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);
        //일단 메모리에 넣어야 주문에서 찾아 쓸 수 있다

        Order order = orderService.createOrder(memberId, "itemA", 20000);

        System.out.println("order = " + order);
        //order의 toString으로 출력
        System.out.println("order.calculatePrice = " + order.calculatePrice());
    }
}
