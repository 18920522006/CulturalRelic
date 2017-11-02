package com;

import com.base.config.Beans;
import com.login.model.TSystemUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test
	public void contextLoads() {
		ApplicationContext factory = null;

		/**
		 * xml load bean
		 */
		factory = new ClassPathXmlApplicationContext("beans.xml");

		/**
		 * java config load bean
		 */
		factory = new AnnotationConfigApplicationContext(Beans.class);

		TSystemUser user = factory.getBean("user", TSystemUser.class);

		System.out.println(user.getUsername());
	}

}
