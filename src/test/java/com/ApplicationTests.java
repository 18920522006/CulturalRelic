package com;

import com.beanfactory.MyBeanPostProcessor;
import com.beanfactory.MyInstantiationAwareBeanPostProcessor;
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
	 * 使用 FactoryBean 接口自定义实例化Bean
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
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(User.class);
		User user = applicationContext.getBean("user",User.class);
		System.out.println(user.getUsername());
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
