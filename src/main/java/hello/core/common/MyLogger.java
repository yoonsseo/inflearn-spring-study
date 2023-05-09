package hello.core.common;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MyLogger {
    private String uuid;
    private String requestURL;

    public void setRequestURL(String requestURL) { //중간에 들어오도록 세터 설정
        this.requestURL = requestURL;
    }

    public void log(String message) { //로그 남길 때
        System.out.println("[" + uuid + "]" + " [" + requestURL + "] " + message);
    }

    @PostConstruct //중요
    public void init() { // 고객 요청이 들어올 떄
        uuid = UUID.randomUUID().toString();
        System.out.println("[" + uuid + "] request scope bean create : " + this);
    }

    @PreDestroy // 종료 메서드 호출된다
    public void close() { // 고객 요청이 빠져나갈 때
        System.out.println("[" + uuid + "] request scope bean close : " + this);
    }
}
