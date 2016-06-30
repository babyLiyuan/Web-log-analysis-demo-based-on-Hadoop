package cleanLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Logline {
	private String c_ip;
	private String method;	
	private String uri_stem;
	private String uri_query;
	private String status_code;
	private String sc_bytes;
	private String referer;
	private String agent;
	private String port = "0";
	private String cs_bytes = "0";
	private String time_taken = "0";
	private String time;
	private String date;
	private boolean valid = true;
	
	public Logline(){};
	public Logline(String line){
		String[] arr = line.split(" ");
		if (arr.length > 12) {
			this.c_ip = arr[0];
			this.method = arr[1];
			this.uri_stem = arr[2];
			this.uri_query = arr[3];
			this.status_code = arr[4];
			this.sc_bytes = arr[5];
			this.referer = arr[6];
			this.agent = arr[7];
			this.port = arr[8];
			this.cs_bytes = arr[9];
			this.time_taken = arr[10];
			this.time = arr[11];
			this.date = arr[12];
			this.valid = true;
		}else{
			this.valid = false;
		}
	}
	public static Logline parser(String line) {
		Logline log = new Logline();
		String[] arr = line.split(" ");
		String date_time = new String();
		
		if (arr.length > 11) {
			log.setC_ip(arr[0]);
			date_time = arr[3].substring(1);
			log.setMethod(arr[5].substring(1));
			if(arr[6].contains("?")){
				String[] arr1 = arr[6].split("\\?");
				if(arr1.length==2){
				log.setUri_stem(arr1[0]);
				log.setUri_query(arr1[1]);
				}else{
					log.setUri_stem(arr[6]);
					log.setUri_query(null);
				}
			}else{
				log.setUri_stem(arr[6]);
				log.setUri_query(null);
			}
			log.setStatus_code(arr[8]);
			log.setSc_bytes(arr[9]);
			log.setReferer(arr[10]);
			log.setAgent(line.substring(line.indexOf("\" \"")+2));
			
			log.setValid(true);
			//清理规则一
			if(log.getUri_stem().endsWith(".jpg") || log.getUri_stem().endsWith(".gif") ||
			   log.getUri_stem().endsWith(".png") || log.getUri_stem().endsWith(".css") || 
			   log.getUri_stem().endsWith(".pdf") || log.getUri_stem().endsWith(".txt") || 
			   log.getUri_stem().endsWith(".doc")
			   ){
				log.setValid(false);
				if(log.getMethod()=="POST" || Integer.parseInt(log.getStatus_code()) >= 400){
					log.setValid(true);
				}
			}
			//清理规则二
			if (log.getAgent().contains("spider")) {
				log.setValid(false);
			}
			//清理规则三
			if(log.getMethod()=="HEAD" && log.getAgent().contains("Monitor")){
				log.setValid(false);
			}
			//清理规则四
			if(log.getMethod()=="POST" && log.getUri_stem().endsWith(".svc")){
				log.setValid(false);
			}
			try {
	            log.setDate(formatDate(parseDate(date_time)));
	        } catch (ParseException e) {
	            //e.printStackTrace();
	            log.setValid(false);
	            log.setDate("wrong");
	        }
			try {
	            log.setTime(formatTime(parseDate(date_time)));
	        } catch (ParseException e) {
	            //e.printStackTrace();
	            log.setValid(false);
	            log.setDate("wrong");   
	        }
			} else {
			log.setValid(false);
		}
		return log;
	}
	/*public static Logline filterIP(String line) {
		
	
		KPI kpi = parser(line);
		Set<String> pages = new HashSet<String>();
		pages.add("/about");
		pages.add("/black-ip-list/");
		pages.add("/cassandra-clustor/");
		pages.add("/finance-rhive-repurchase/");
		pages.add("/hadoop-family-roadmap/");
		pages.add("/hadoop-hive-intro/");
		pages.add("/hadoop-zookeeper-intro/");
		pages.add("/hadoop-mahout-roadmap/");

		if (!pages.contains(kpi.getRequest())) {
			kpi.setValid(false);
		}

		return Logline;
	}*/

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.c_ip);
	    sb.append(" " + this.method);
		sb.append(" " + this.uri_stem);
		sb.append(" " + this.uri_query);
		sb.append(" " + this.status_code);
		sb.append(" " + this.sc_bytes);
		sb.append(" " + this.referer+" ");
		String[] arr = this.agent.split(" ");
		for(int i=0;i<arr.length;i++){
			sb.append(arr[i]);
		}
		sb.append(" " + this.port);
		sb.append(" " + this.cs_bytes);
		sb.append(" " + this.time_taken);
		sb.append(" " + this.time);
		sb.append(" " + this.date);
		return sb.toString();
	}
	
	
	public static Date parseDate(String date_time) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.US);
        return df.parse(date_time);
    }
	
	public static String formatDate(Date d) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(d);
	}
	
	public static String formatTime(Date d) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		return df.format(d);
	}
	
	/*public String getReferer_domain() {
		if (referer.length() < 8) {
			return referer;
		}

		String str = this.referer.replace("\"", "").replace("http://", "")
				.replace("https://", "");
		return str.indexOf("/") > 0 ? str.substring(0, str.indexOf("/")) : str;
	}*/

	
	
	

	public static void main(String args[]) {
		String line = "202.102.85.24 - - [11/Jul/2014:14:05:15 +0800] \"GET /static/image/common/px_e.png HTTP/1.0\" 200 435 \"http://www.aboutyun.com/data/cache/style_1_common.css?hX6\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:30.0) Gecko/20100101 Firefox/30.0\"";
		System.out.println(line);
		Logline newLog = parser(line);
		
		System.out.println(newLog);
	}

	public String getC_ip() {
		return c_ip;
	}

	public void setC_ip(String c_ip) {
		this.c_ip = c_ip;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getUri_stem() {
		return uri_stem;
	}

	public void setUri_stem(String uri_stem) {
		this.uri_stem = uri_stem;
	}
	
	public String getUri_query() {
		return uri_query;
	}

	public void setUri_query(String uri_query) {
		this.uri_query = uri_query;
	}

	public String getStatus_code() {
		return status_code;
	}

	public void setStatus_code(String status_code) {
		this.status_code = status_code;
	}

	public String getSc_bytes() {
		return sc_bytes;
	}

	public void setSc_bytes(String sc_bytes) {
		this.sc_bytes = sc_bytes;
	}
	
	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}
	
	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getCs_bytes() {
		return cs_bytes;
	}

	public void setCs_bytes(String cs_bytes) {
		this.cs_bytes = cs_bytes;
	}

	public String getTime_taken() {
		return time_taken;
	}

	public void setTime_taken(String time_taken) {
		this.time_taken = time_taken;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}