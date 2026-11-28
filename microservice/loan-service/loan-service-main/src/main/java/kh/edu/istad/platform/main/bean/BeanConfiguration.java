package kh.edu.istad.platform.main.bean;

import kh.edu.istad.platform.loan.domain.domain.LoanDomainService;
import kh.edu.istad.platform.loan.domain.domain.LoanDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public LoanDomainService loanDomainService(){
        return new LoanDomainServiceImpl();
    }
}
