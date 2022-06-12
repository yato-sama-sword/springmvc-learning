package org.example.mvclearning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 视图解析器model类
 * @author yato
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyViewResolver {

    private String prefix;
    private String suffix;

    /**
     * 给jsp文件名拼接前缀后缀
     * @param name name
     * @return 返回添加前后缀的jsp文件
     */
    public String jspMapping(String name){
        return this.prefix + name + this.suffix;
    }
}
