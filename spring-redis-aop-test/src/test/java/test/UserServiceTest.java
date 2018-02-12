package test;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.lowen.entity.UserInfo;
import com.lowen.service.UserService;

public class UserServiceTest extends BaseTest {
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
	
	@Autowired
	private UserService userService;
	
	@Test
	public void insertTest() {
		UserInfo userInfo = new UserInfo();
		userInfo.setId("1002");
		userInfo.setName("lowen03");
		userInfo.setAge("18");
		userService.insert(userInfo);
	}
	
	@Test
	public void queryByIdTest() {
		UserInfo userInfo = userService.queryById("1001");
		logger.info("===>{}",JSON.toJSON(userInfo));
	}
	
	@Test
	public void queryByNameTest() {
		List<UserInfo> list = userService.queryByName("lowen");
		logger.info("===>{}",JSON.toJSON(list));
	}
	
	@Test
	public void updateByIdTest() {
		UserInfo userInfo = new UserInfo();
		userInfo.setId("1001");
		userInfo.setName("lowen02");
		userInfo.setAge("21");
		userService.updateById(userInfo);
	}
}
