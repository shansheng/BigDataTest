package com.xiandian.cloud.common.learning;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 计算学生知识，素质，技能三个能力值
 * 
 * @author guoyi
 *
 */
public class StudentData {

	

	public static void connect() {
		// 声明Connection对象
		Connection con = null;
		// 驱动程序名
		String driver = "com.mysql.jdbc.Driver"; // URL指向要访问的数据库名mydata
		String url = "jdbc:mysql://10.10.0.161:3306/douxue";
		// MySQL配置时的用户名
		String user = "root";
		// MySQL配置时的密码
		String password = "123456";
		// 遍历查询结果集
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			// 加载驱动程序
			Class.forName(driver);
			// 1.getConnection()方法，连接MySQL数据库！！
			con = DriverManager.getConnection(url, user, password);
			if (!con.isClosed())
				System.out.println("Succeeded connecting to the Database!");
			// 2.创建statement类对象，用来执行SQL语句！！
			Statement smt = con.createStatement();
			Statement smt1 = con.createStatement();
			// 要执行的SQL语句
			String sql = "select * from t_class_score where userid = 2039";
			String sql1 = "select distinct(coursename) from t_class_score";
			// 3.ResultSet类，用来存放获取的结果集！！
			rs = smt.executeQuery(sql);
			rs1 = smt1.executeQuery(sql1);

			while (rs1.next()) {
				if (classify(rs1.getString("coursename")).equals("技能"))
					System.out.println(rs1.getString("coursename"));
			}

			// 学生知识
			List<Double> knowledge = new ArrayList<>();
			// 学生素质
			List<Double> quality = new ArrayList<>();
			// 学生技能
			List<Double> ability = new ArrayList<>();
			while (rs.next()) {
				System.out.println(rs.getString("coursename") + " " + rs.getString("df"));
				System.out.println(classify(rs.getString("coursename")));
				if (classify(rs.getString("coursename")).equals("知识"))
					knowledge.add(scoreTransform(rs.getString("df")));
				if (classify(rs.getString("coursename")).equals("素养"))
					quality.add(scoreTransform(rs.getString("df")));
				if (classify(rs.getString("coursename")).equals("技能"))
					ability.add(scoreTransform(rs.getString("df")));
			}
			// 知识得分
			double point_knowledge = pointCalaulate(knowledge);
			// 素质得分
			double point_quality = pointCalaulate(quality);
			// 技能得分
			double point_ability = pointCalaulate(ability);
			System.out.println(point_knowledge);
			System.out.println(point_quality);
			System.out.println(point_ability);
			rs.close();
			rs1.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// 数据库驱动类异常处理
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
		} catch (SQLException e) {
			// 数据库连接失败异常处理
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				rs1.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		connect();
	}

	/**
	 * 将课程分至知识，素质，技能三类
	 * 
	 * @param coursename
	 * @return 类别
	 */
	public static String classify(String coursename) {
		if (coursename.contains("数学") || coursename.contains("英语") || coursename.contains("音乐")
				|| coursename.contains("体育") || coursename.contains("语") || coursename.contains("绘图")
				|| coursename.contains("球"))
			return "知识";
		else if (coursename.contains("职业") || coursename.contains("素养") || coursename.contains("礼仪")
				|| coursename.contains("文化") || coursename.contains("思想") || coursename.contains("素养")
				|| coursename.contains("创业") || coursename.contains("心理") || coursename.contains("军事")
				|| coursename.contains("艺术") || coursename.contains("赏") || coursename.contains("形象")
				|| coursename.contains("启示") || coursename.contains("形势") || coursename.contains("普通话"))
			return "素养";
		else
			return "技能";
	}

	/**
	 * 将String型得分转化为double型
	 * 
	 * @param coursename
	 * @return 得分
	 */
	public static double scoreTransform(String coursename) {
		if (coursename.contains("优秀"))
			return 90.0;
		else if (coursename.contains("良好"))
			return 80.0;
		else if (coursename.contains("一般"))
			return 70.0;
		else if (coursename.contains("及格"))
			return 60.0;
		else if (coursename.contains("不及格"))
			return 50.0;
		else
			return Double.parseDouble(coursename);
	}

	/**
	 * 计算学生每一类的得分
	 * 
	 * @param scores
	 * @return
	 */
	public static double pointCalaulate(List<Double> scores) {
		double sum = 0.0;
		for (double dou : scores)
			sum += dou;
		return sum / scores.size();
	}
}
