package hello.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan( //자동으로 스프링 빈을 끌어와야
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
        //스프링 빈으로 등록하지 않을 것 지정
        //@Configuration에 @Component 내장
)
public class AutoAppConfig {

}
