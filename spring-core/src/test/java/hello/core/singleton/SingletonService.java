package hello.core.singleton;

public class SingletonService {
    private static final SingletonService instance = new SingletonService();
    //자기 자신을 내부에 private으로 하나 가지고 있는데 static으로 가지고 있다
    //클래스 레벨에 가지고 있기 때문에 하나만 존재하게 된다

    //조회 시 사용
    public static SingletonService getInstance() {
        return instance;
    }

    private SingletonService() { }

    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");
    }
}
