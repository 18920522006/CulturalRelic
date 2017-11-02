package com;

import com.beanfactory.MyBeanPostProcessor;
import com.beanfactory.MyInstantiationAwareBeanPostProcessor;
import com.entity.User;
import com.login.model.TSystemUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test
	public void contextLoads() {

		ClassPathResource resource = new ClassPathResource("beans.xml");

		BeanFactory factory = new DefaultListableBeanFactory();

		XmlBeanDefinitionReader reader =
				new XmlBeanDefinitionReader((DefaultListableBeanFactory) factory);

		reader.loadBeanDefinitions(resource);

		/**
		 * xml load bean
		 */
		//factory = new ClassPathXmlApplicationContext("beans.xml");

		/**
		 * java config load bean
		 */
		//factory = new AnnotationConfigApplicationContext(Beans.class);

		((ConfigurableBeanFactory)factory).addBeanPostProcessor(
				new MyBeanPostProcessor());

		((ConfigurableBeanFactory)factory).addBeanPostProcessor(
				new MyInstantiationAwareBeanPostProcessor());

		System.out.println("factory.getBean");
		User user = factory.getBean("user", User.class);

		System.out.println(user.getUsername());
	}

}
