package hello.core.discount;

import hello.core.member.Member;

public interface DiscountPolicy {
     /**
     * @return 할인 대상 금액
     */
     // 호출 결과로 얼마가 할인되었는지 반환
    int discount(Member member, int price);
}
