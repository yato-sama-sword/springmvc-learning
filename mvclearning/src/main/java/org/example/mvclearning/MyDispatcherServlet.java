package org.example.mvclearning;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求分发，springmvc核心类
 * 1.扫描controller、requestMapping注解，将controller交给ioc容器管理，将requestMapping放到映射的hashmap中
 * 2.加载视图解析器
 * 3.处理浏览器发过来的请求
 * @author yato
 */
@Slf4j
public class MyDispatcherServlet extends HttpServlet {

    /**
     * 创建一个ioc容器
     * 键为url，值为xml文件中的name-value组合
     */
    private Map<String, ObjectRelation> iocContainer = new HashMap<>();
    private MyViewResolver myViewResolver;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1. 该servlet会接受服务器的所有请求
        //2.根据请求的url不同，决定具体调用controller的哪个方法

        // 获取当前请求的地址
        String uri = req.getRequestURI();
        String[] split = uri.split("/");

        if(split.length < 2){
            return;
        }

        // 获取当前要访问的url
        String url = split[2];
        // 通过url，找到ioc容器中对应的类对象和方法对象
        ObjectRelation relation = iocContainer.get(url);
        try {
            // 调用对应方法，返回页面名称
            String viewName = (String) relation.getMethod().invoke(relation.getObj(), req);
            // 使用视图解析器，对页面名进行补全
            String page = myViewResolver.jspMapping(viewName);
            // 使用请求转发，进行页面跳转
            req.getRequestDispatcher(page).forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    /**
     * 在servlet初始化的时候会执行一次init方法，通常用来做一些初始化的工作
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        // 扫描标注controller注解的类，给类创建对象并存入ioc容器
        // 扫描标注了RequestMapping的方法，创建url与方法的映射关系，存入映射容器中
        scanController(config);
        // 加载视图解析器
        loadViewResolver(config);
    }

    /**
     * 扫描标注controller注解的类，找到标注RequestMapping注解的方法，
     * 创建url与类-方法的映射关系，存入ioc容器中
     * @param config config
     */
    private void scanController(ServletConfig config){
        try {
            // 试图获取springmvc配置文件的路径
            StringBuilder path = new StringBuilder();
            path.append(config.getServletContext().getRealPath("")).append("\\WEB-INF\\classes\\")
                    .append(config.getInitParameter("contextConfigLocation"));
            // 使用dom4j读取xml文件
            SAXReader reader = new SAXReader();
            Document doc = reader.read(path.toString());
            Element root = doc.getRootElement();
            // 获取扫包路径对应的节点对象
            Element element = root.element("component-scan");
            // 获取base-package的属性值
            String packageName = element.attributeValue("base-package");
            // 根据包名找到包下所有类的名字
            List<String> classNames = getClassNames(packageName);
            // 根据类名，通过类的反射机制，给它创建对象，注入ioc容器
            for (String str: classNames) {
                // 通过类名获取Class对象
                Class clazz = Class.forName(str);
                // 判断该类是否标注controller注解
                if (clazz.isAnnotationPresent(MyController.class)) {
                    // 方便获取注解内value的值
                    MyController annotation = (MyController) clazz.getAnnotation(MyController.class);
                    // 给类创建对象
                    Object obj = clazz.newInstance();
                    // 找到类中所有requestMapping注解标注的方法，生产方法对象
                    initMapping(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取包下所有类的名字
     * @param packageName 包名
     * @return list
     */
    private List<String> getClassNames(String packageName) {
        List<String> classNames = new ArrayList<String>();
        // 将包名中的.替换成/，这样才是在文件夹中显示的路径
        String packagePath = packageName.replace(".", "/");
        // 通过当前线程获取类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // 通过类加载器找到文件夹的真实路径
        URL url = classLoader.getResource(packagePath);
        // 如果路径不存在则抛出异常
        if (url == null) {
            throw new RuntimeException("path not exists: " + packageName );
        } else {
            // 该file对象是包对应的文件夹的对象
            File file = new File(url.getPath());
            // 获取文件夹中所有文件（获取包里所有的类
            File[] files = file.listFiles();
            assert files != null;
            // 拼接处完整的包名+类型的格式
            for (File f : files) {
                String className = packageName + "." + f.getName().replace(".class", "");
                classNames.add(className);
            }
        }
        return classNames;
    }

    /**
     * 用来生成requestMapping注解标注的方法的对象
     * @param obj 当前对象
     */
    private void initMapping(Object obj) {
        Class<?> clazz = obj.getClass();
        // 获取类中的所有方法
        Method[] methods = clazz.getMethods();
        // 判断方法是否有RequestMapping注解标注
        for (Method method : methods) {
            if (method.isAnnotationPresent(MyRequestMapping.class)) {
                MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                String value = annotation.value();
                // 把类对象和方法对象组合在一起
                ObjectRelation relation = new ObjectRelation();
                relation.setObj(obj);
                relation.setMethod(method);
                // 存入ioc容器
                iocContainer.put(value, relation);
            }
        }
    }

    /**
     * 加载视图解析器，创建解析器对象
     * @param config
     */
    private void loadViewResolver(ServletConfig config){
        try {
            StringBuilder path = new StringBuilder();
            path.append(config.getServletContext().getRealPath("")).append("\\WEB-INF\\classes\\")
                    .append(config.getInitParameter("contextConfigLocation"));
            // 解析xml文件
            SAXReader reader = new SAXReader();
            Document doc =  reader.read(path.toString());
            Element root = doc.getRootElement();
            // 读取bean标签，转换成节点对象
            Element bean = root.element("bean");
            // 读取bean标签里class属性的值
            String className = bean.attributeValue("class");
            Class clazz = Class.forName(className);
            // 为当前类创建一个对象
            Object obj = clazz.newInstance();
            // 获取setPrefix和setSuffix方法
            Method setPrefix = clazz.getMethod("setPrefix", String.class);
            Method setSuffix = clazz.getMethod("setSuffix", String.class);
            // 获取bean标签内的所有子标签
            List<Element> elementList = bean.elements();
            for(Element property : elementList){
                String name = property.attributeValue("name");
                String value = property.attributeValue("value");
                if ("prefix".equals(name)) {
                    // 反射调用setPrefix方法
                    setPrefix.invoke(obj, value);
                } else if ("suffix".equals(name)) {
                    setSuffix.invoke(obj, value);
                }
            }
            // 初始化视图解析器
            myViewResolver = (MyViewResolver) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
