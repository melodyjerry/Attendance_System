import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hd.bean.Employee;

public class Test {
	public static void main(String[] args) {
//		Map<String,String> map = new HashMap<String,String>();
//		Employee employee = new Employee("ce003","С��","xiaohao",
//				"123","20000","2019-7-30","��ͨԱ��","���繤��ʦ");
//		String info = employee.toString();
//		String str = null;
//		System.out.println(info);
//		Pattern pattern = Pattern.compile("(\\[[^\\]]*\\])");
//		Matcher matcher = pattern.matcher(info);
//		while(matcher.find()) {
//			str = matcher.group().substring(1, matcher.group().length()-1);
//		}
//		System.out.println(str);
//		String[] str2 = str.split(",");
//		for (String string : str2) {
//			String[] str3 = string.split("=");
//			System.out.println(str3[0]);
//			System.out.println(str3[1]);
//			map.put(str3[0], str3[1]);
//		}
//		String name = map.get(" employeeAccount");
//		System.out.println(name);
//		System.out.println(map);
		
		
		Date date = new Date();
		// ���ڸ�ʽ
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		//��ȡ��ǰ����
		String dateNow = dateFormat.format(date);
		String _dateNow = dateNow.substring(0, 7);
		System.out.println(_dateNow);
	}
}
