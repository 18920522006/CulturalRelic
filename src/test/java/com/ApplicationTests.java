package com;

import com.beanfactory.MyBeanPostProcessor;
import com.beanfactory.MyInstantiationAwareBeanPostProcessor;
import com.config.Beans;
import com.config.DaoConfig;
import com.config.ServiceConfig;
import com.entity.LoginService;
import com.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

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
	@Test
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
		ClassPathResource resource = new ClassPathResource("beans.xml");
		BeanFactory factory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader =
				new XmlBeanDefinitionReader((DefaultListableBeanFactory) factory);
		reader.loadBeanDefinitions(resource);
		((ConfigurableBeanFactory)factory).addBeanPostProcessor(
				new MyBeanPostProcessor());
		((ConfigurableBeanFactory)factory).addBeanPostProcessor(
				new MyInstantiationAwareBeanPostProcessor());
		User user = factory.getBean("user", User.class);
		System.out.println(user.getUsername());
	}

}
