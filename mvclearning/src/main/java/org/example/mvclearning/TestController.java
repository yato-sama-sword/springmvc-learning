package org.example.mvclearning;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yato
 */
@MyController
public class TestController {

    @MyRequestMapping("test")
    public String test(HttpServletRequest request){
        request.setAttribute("msg", "Test");
        return "test";
    }

    @MyRequestMapping("HelloWorld")
    public String helloWorld(HttpServletRequest request){
        request.setAttribute("data", "HelloWorld");
        return "HelloWorld";
    }
}
