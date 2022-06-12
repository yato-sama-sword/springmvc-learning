package org.example.mvclearning;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.List;

/**
 * 学习xml文件解析
 * @author yato
 */
@Slf4j
public class XmlTest {
    public static void main(String[] args) throws Exception {
        String path = "src/main/resources/springmvc.xml";
        // 创建读取xml文件对象
        SAXReader reader = new SAXReader();
        // 读取文件，然后把文件转换成document对象
        Document doc = reader.read(path);
        // 非不得已不要使用System.out.println，方法实现使用synchronized锁
        String enc = doc.getXMLEncoding();
        log.info(enc);
        // 获取xml文件根节点(就是标签啦，还可以获取属性值
        Element root = doc.getRootElement();
        // 获取根节点下所有一级子节点
        List<Element> elements = root.elements();
        for (Element element : elements) {
            log.info(element.getName());
            Attribute attribute = element.attribute(0);
            log.info(attribute.getName());
            log.info(attribute.getText());
            // bean标签内还有property标签
            if ("bean".equals(element.getName())) {
                List<Element> proList = element.elements();
                for (Element proItem : proList) {
                    log.info(proItem.attribute("name").getText() + "\t" + proItem.attribute("value").getText());
                }
            }
        }
    }
}
