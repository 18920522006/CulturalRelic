package com;

import com.advice.Waiter;
import com.advisor.Seller;
import com.advisor.WaiterDelegate;
import com.beanfactory.MyBeanFactoryPostProcessor;
import com.beanfactory.MyBeanPostProcessor;
import com.beanfactory.MyInstantiationAwareBeanPostProcessor;
import com.config.Beans;
import com.config.DaoConfig;
import com.config.ServiceConfig;
import com.entity.LoginService;
import com.entity.User;
import com.event.MailSendMulticaster;
import com.introduce.Monitorable;
import com.introduce.Say;
import com.proxy.service.ForumService;
import com.proxy.service.ForumServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	/**
	 * java config 方式加载Bean
	 */
	public void javaConfigAnnotation() {
		ApplicationContext applicationContext =
				new AnnotationConfigApplicationContext(DaoConfig.class,ServiceConfig.class);
		LoginService loginService = applicationContext.getBean("LoginService1",LoginService.class);
		System.out.println(loginService.getLoginDao());
	}

	/**
	 * 使用 FactoryBean 接口自定义实例化Bean
	 * 当 getBean(& + name)，带 & 时返回工厂本身(含User对象)
	 */
	public void factoruBeanforUser() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		User user1 = applicationContext.getBean("user1",User.class);
		System.out.println(user1.getUsername());
	}

	/**
	 * 使用注解方式实例Bean
	 */
	public void applicationContextForBean() {
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Beans.class);
		User user = applicationContext.getBean("user",User.class);
		System.out.println(user.getUsername());
		User user2 = applicationContext.getBean("user2",User.class);
		System.out.println(user2.getUsername());
		User user3 = applicationContext.getBean("user3",User.class);
		System.out.println(user3.getUsername());
	}

	/**
	 * 使用xml方式实例Bean
	 */
	public void beanFactoryForBean() {
		/** ① ResourceLoader 装载配置文件 */
		Resource resource = new ClassPathResource("beans.xml");

		/**  BeanDefinitionRegistry 存储 BeanDefinition */
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

		/**  BeanDefinitionReader 解析 <bean> 变成 BeanDefinition 对象 */
		BeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);

		/** ② 装载 */
		reader.loadBeanDefinitions(resource);

		/** ③ BeanPostProcessor 对Bean进行加工 */
		factory.addBeanPostProcessor(new MyBeanPostProcessor());
		factory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcessor());

		User user = factory.getBean("user", User.class);
		System.out.println(user.getUsername());
	}

	/**
	 * 自定义属性编辑器
	 */
	public void customEditor() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		User user = applicationContext.getBean("user4", User.class);
		String color = user.getCar().getColor();
		System.out.println(color);
	}

	/**
	 * 容器事件
	 */
	public  void sendMail() {
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Beans.class);
		MailSendMulticaster mailSender = applicationContext.getBean("mailSender", MailSendMulticaster.class);
		mailSender.sendMail("aaa@bbb.com");
	}

	/**
	 * 容器取代理类
	 */
	public void getProxy() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		Waiter waiter = applicationContext.getBean("waiter", Waiter.class);
		waiter.greetTo("zhangsan");
	}

	/**
	 * 引介增加增强,添加setMonitorActive方法
	 */
	public void introduce() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		ForumService service = applicationContext.getBean("forumServiceProxy", ForumService.class);
		service.removeForum(10);
		service.removeTopic(1012);

		Monitorable monitorable = (Monitorable) service;
		monitorable.setMonitorActive(true);

		Say say = (Say) service;
		say.say();

		service.removeForum(10);
		service.removeTopic(1012);
	}

	/**
	 *  静态普通方法匹配切面
	 */
	public void staticMethodMatcherPointcutAdvisor() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		com.advisor.Waiter advisor = applicationContext.getBean("waiterAdvisor", com.advisor.Waiter.class);
		Seller seller = applicationContext.getBean("sellerAdvisor", Seller.class);
		advisor.greetTo("张三");
		seller.greetTo("李四");
		System.out.println();
	}

	/**
	 *  静态正则表达式方法匹配切面
	 */
	@Test
	public void staticRegexpMethodMatcherPointcutAdvisor() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		com.advisor.Waiter advisor = applicationContext.getBean("waiterRegex", com.advisor.Waiter.class);
		advisor.greetTo("王五");
	}

	/**
	 *  动态切面
	 */
	public void dynamicAdvisor() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		com.advisor.Waiter advisor = applicationContext.getBean("waiterDynamic", com.advisor.Waiter.class);
		advisor.greetTo("张三");
		advisor.greetTo("王五");
	}

	/**
	 *  流程切面
	 */
	public void controlFlowAdvisor() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		com.advisor.Waiter waiter = applicationContext.getBean("waiterControlFlow", com.advisor.Waiter.class);
		WaiterDelegate waiterDelegate = new WaiterDelegate();
		waiterDelegate.setWaiter(waiter);
		waiter.serveTo("张三");
		waiter.greetTo("张三");
		waiterDelegate.service("张三");
	}

	/**
	 *  复合切面
	 *  	- 流程切面
	 *   	- 静态名称切面
	 */
	@Test
	public void composableAdvisor() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		com.advisor.Waiter waiter = applicationContext.getBean("waiterComposable", com.advisor.Waiter.class);
		WaiterDelegate waiterDelegate = new WaiterDelegate();
		waiterDelegate.setWaiter(waiter);
		waiterDelegate.service("张三");
	}
}
