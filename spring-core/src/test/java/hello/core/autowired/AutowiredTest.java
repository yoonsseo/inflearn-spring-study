package hello.core.autowired;

import hello.core.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.Nullable;

import java.util.Optional;

public class AutowiredTest {
    @Test
    void AutowiredOption() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestBean.class);
        //테스트빈을 넣어주면 스프링빈으로 등록이 된다

    }

    //임의의 테스트 클래스
    static class TestBean {
        @Autowired(required = false)
        public void setNoBean1(Member noBean1) {
            //멤버는 스프링 빈 아님
            System.out.println("noBean1 = " + noBean1);
        }

        @Autowired
        public void setNoBean2(@Nullable Member noBean2) {
            //멤버는 스프링 빈 아님
            System.out.println("noBean2 = " + noBean2);
        }

        @Autowired
        public void setNoBean3(Optional<Member> noBean3) {
            //멤버는 스프링 빈 아님
            System.out.println("noBean3 = " + noBean3);
        }


    }

}
