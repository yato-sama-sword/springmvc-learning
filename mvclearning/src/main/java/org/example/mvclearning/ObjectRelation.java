package org.example.mvclearning;


import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author yato
 */
@Data
public class ObjectRelation {
    /**
     * 类对象
      */
    private Object obj;
    /**
     * 方法对象
      */
    private Method method;

}
