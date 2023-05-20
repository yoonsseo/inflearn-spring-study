package jpabook.jpashop;

import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class itemUpdateTest {
    @Autowired EntityManager em;

    @Test
    public void updateTest() throws Exception {
        Book book = em.find(Book.class, 1L);

        //트랜잭션 안에서
        book.setName("이름바꾸기");

        //트랜잭션 커밋하면 JPA가 update 쿼리 자동으로 짜서 update -> 더티채킹(변경 감지)
    }
}
