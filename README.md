**注：进行简单的模拟而非详尽的实现**

## Spring MVC

### 什么是MVC

>model-view-controller，是一种软件设计规范，本质上是一种解耦，其中：
>
>1. Model是应用程序中用于处理应用程序数据逻辑的部分
>2. View是应用程序中处理数据显示的部分，一般需要依据模型数据创建
>3. Controller是应用程序中处理用户交互的部分，通常控制器负责从视图读取数据，控制用户输入，并向模型发送数据

### 什么是Spring MVC

>是遵从MVC规范开发出的Web框架

### Spring MVC请求流程

1. 发起Http请求：客户端直接向DispatcherServlet发起请求
2. 寻找处理器：DispatcherServlet调用HandlerMapping，解析请求，找到对应的Handler
3. 适配处理器：解析到Handler后，由HandlerAdapter进行处理
4. 调用处理器：HandlerAdapter将调用对应处理器处理请求，以及相应业务逻辑
5. 返回ModelAndView：返回model数据对象和逻辑view
6. 寻找实际view：通过ViewResolver寻找逻辑view对应的实际view（是不是有点像虚拟dom和实际dom蛤
7. 根据Model渲染视图：把model数据传给view，进行视图渲染
8. 进行http响应：将view直接返回给客户端（发起请求者



**个人主要实现工作**

实现MyDispathcerServlet：

1. 扫描标注MyController注解的类或MyRequestMapping注解的方法，将标注MyController注解的类以及类中标注MyRequestMapping注解的方法与url进行绑定，注入到ioc容器中（通过map和自定义类ObjectRelation实现
2. 加载视图解析器，通过解析xml文件，获取视图解析器的prefix属性和suffix属性，创建解析器对象
3. 处理服务器请求并进行响应，获取当前请求uri，并生成url。通过url找到对应类，调用对应方法（controller方法，返回值为要去的页面名称）获取页面名称。通过视图解析器对页面名称进行补全后，使用请求转发，页面跳转

XmlTest是学习解析xml文件的类，与主要流程关系不是很密切
