/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionsystem.interceptor;

import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.Timeout;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.InvocationContext;

/**
 *
 * @author OCE
 */
public class AuctionInterceptor {
    
    @AroundInvoke
    public Object interceptBusinessMethod(InvocationContext iContext) throws Exception {
        Date current = new Date();
        Calendar calendar = Calendar.getInstance();
        int day;
        
        calendar.setTime(current);
        day = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println("AuctionInterceptor.interceptBusinessMethod() Method: " + iContext.getMethod().getName());
        if (Calendar.SUNDAY == day) {
            throw new SecurityException("Do not dirturb");
        }        
        return iContext.proceed();
    }
    
    @PostConstruct
    public void interceptorInit(InvocationContext iContext) {
        System.out.println("AuctionInterceptor.interceptorInit()");
    }
    
    @AroundTimeout
    public Object initTimeOut(InvocationContext iContext) throws Exception {
        System.out.println("AuctionInterceptor.interceptorInit()");
        
        return iContext.proceed();
    }
}
