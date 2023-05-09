package hello.core.web;

import hello.core.common.MyLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class LogDemoController {
    // @RequiredArgsConstructor로 생성자랑 빈 자동으로 설정
    private final LogDemoService logDemoService;

    //Provider 이용 해결 시
    //private final ObjectProvider<MyLogger> myLoggerProvider;
    //MyLogger가 아니라 MyLogger를 찾을 수 있는 게 주입

    //기본, 프록시 이용 해결 시
    private final MyLogger myLogger;

    @RequestMapping("log-demo")
    @ResponseBody // 뷰 화면 없이 글자 그대로 반환
    public String logDemo(HttpServletRequest request) throws InterruptedException {
        //자바에서 제공하는 표준 서블렛 규약에 의한 고객 요청 정보 받을 수 있다
//        MyLogger myLogger = myLoggerProvider.getObject();
        String requestURL = request.getRequestURL().toString();
        //getRequestURL() : 고객이 어떤 URL로 요청했는지 알 수 있다
        System.out.println("myLogger = " + myLogger.getClass());
        myLogger.setRequestURL(requestURL);

        myLogger.log("controller test");
        Thread.sleep(1000);
        logDemoService.logic("testId");
        return "OK";
    }
}
