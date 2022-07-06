**注：进行简单的模拟而非详尽的实现**

## Spring MVC

### 什么是MVC

>model-view-controller，是一种软件设计规范，本质上是一种解耦，其中：
>
>1. Model是应用程序中用于处理应用程序数据逻辑的部分（JavaBean对象
>2. View是应用程序中处理数据显示的部分，一般需要依据模型数据创建（Html页面
>3. Controller是应用程序中处理用户交互的部分，通常控制器负责从视图读取数据，控制用户输入，并向模型发送数据（emmm，就是>Controller或者说Servlet


### 什么是Spring MVC

>是遵从MVC规范开发出的Web框架

### Spring MVC请求流程

>**注！！！后文引用kaitao-springMVC一书中的知识**
>
>个人看来，DispatcherServlket相当于前端控制器，Handler相当于页面控制器；前者负责发送具体请求给页面控制器，后者则负责处理对应的业>务逻辑，实现功能，完成功能需要将ModelAndView还回去。最后进行视图的解析，前端控制器会对用户做出响应。这里有必要提一提


1. **首先用户发送请求——>DispatcherServlet**，前端控制器收到请求后自己不进行处理，而是委托给其他的解析器进行处理，作为统一访问点，进行全局的流程控制；
2. **DispatcherServlet——>HandlerMapping**， HandlerMapping 将会把请求映射为 HandlerExecutionChain对象（包含一个 Handler 处理器（页面控制器）对象、多个HandlerInterceptor 拦截器）对象，通过这种策略模式，很容易添加新的映射策略；
3. **DispatcherServlet——>HandlerAdapter**，HandlerAdapter 将会把处理器包装为适配器，从而支持多种类型的处理器， 即适配器设计模式的应用，从而很容易支持很多类型的处理器；
4. **HandlerAdapter——>处理器功能处理方法的调用**，HandlerAdapter 将会根据适配的结果调用真正的处理器的功能处理方法，完成功能处理；并返回一个ModelAndView 对象（包含模型数据、逻辑视图名）；
5. **ModelAndView 的逻辑视图名——> ViewResolver**，ViewResolver 将把逻辑视图名解析为具体的View，通过这种策略模式，很容易更换其他视图技术；
6. **View——>渲染**，View 会根据传进来的Model 模型数据进行渲染，此处的Model 实际是一个Map 数据结构，因此很容易支持其他视图技术；
7. **返回控制权给DispatcherServlet**，由DispatcherServlet 返回响应给用户，到此一个流程结束。



**个人主要实现工作**

实现MyDispathcerServlet：

1. 扫描标注MyController注解的类或MyRequestMapping注解的方法，将标注MyController注解的类以及类中标注MyRequestMapping注解的方法与url进行绑定，注入到ioc容器中（通过map和自定义类ObjectRelation实现，这一过程相当于创建HandlerMapping，到时候找对应的Handler就方便啦）
2. 加载视图解析器，通过解析xml文件，获取视图解析器的prefix属性和suffix属性，创建解析器对象，实现将逻辑view转换为真实view（最近学了点vue，感觉有虚拟dom和真实dom的味道）
3. 处理服务器请求并进行响应，获取当前请求uri，并生成url。通过url找到对应类，调用对应方法（controller（实质上也是一种类handler）的方法，返回值为要去的页面名称）获取页面名称。通过视图解析器对页面名称进行补全后，使用请求转发，页面跳转。（这里是因为使用页面跳转效果比较明显啦）

XmlTest是学习解析xml文件的类，与主要流程关系不是很密切
