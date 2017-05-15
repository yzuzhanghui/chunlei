package com.wuxi.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.wuxi.bean.vo.User;

@Repository("userDao")
public class UserDao extends BaseDao{

	
//	public List<User> queryByName(String name){
//		List<User> userList = new ArrayList<>();
//		List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from user where name = ?",new Object[]{name});
//		Iterator<Map<String, Object>> it = list.iterator();  
//		while(it.hasNext()) {  
//		   Map userMap = (Map) it.next();  
//		   User user = new User();
//		   user.setName(userMap.get("name").toString());
//		   userList.add(user);
//		}  
//		return userList;
//	}
	
	public List<User> queryByName(String name){  
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("name", name);
		return this.sqlSession.selectList("user.getUser", params);
	}
	
	/**
	 * 批量操作
	 * https://note.youdao.com/web/#/file/8AB63A06A1B345A28B67F05A2E447D30/note/WEB35d7a2fd715a4a7dfe62d2bb00bff214/
	 * @param userList
	 */
	public void batchInsert(List<User> userList){
		SqlSession session = this.sqlSession.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
		try {
			for(User u : userList){
				session.insert("", u);
			}
			//数量太大可清除缓存 
			//session.clearCache();
			session.commit();
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
			session.close();
		}
		
	}
}
