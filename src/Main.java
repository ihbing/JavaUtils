import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.*;

public class Main
{
	public static void main(String[] args)
	{
		
		new Thread(){

			public void run(){
				try
				{
					System.out.println("start time:"+System.currentTimeMillis());

					System.out.println("length:"+ getFileSizeWithSocket("http://cdn.70mao.com/cs1/?id=OGE5N3c0ay9sbWp3Z04xd1lDOW9mV3BWb1hibmRpdndJdGkzcHh3NjV0WkJjZWJMZ2pLUG8renhIMWRmNXVhY0VkbDVhdw=="));

					System.out.println("end time:"+System.currentTimeMillis());
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.out.println(e.toString());
				}
			}
		}.start();
		}
		
	/*获取文件大小通过socket&HTTP/1.0
	 仅适用于http链接
	 */
	public static Long getFileSizeWithSocket(String url) throws MalformedURLException, IOException, NoSuchAlgorithmException, KeyManagementException{
		System.out.println("start time:"+System.currentTimeMillis());
		
		long contentLength=0;
		
		if(url.startsWith("https")){url.replaceFirst("https","http");}
		
		URL LocalUrl=new URL(url);
		String host=LocalUrl.getHost();
		int port=LocalUrl.getDefaultPort();
		String path= LocalUrl.getPath();
		String query=LocalUrl.getQuery();
		SocketAddress dest=new InetSocketAddress(host,port);
		Socket socket;
//		SSLContext ctx = SSLContext.getInstance("SSL");  
//		//Implementation of a trust manager for X509 certificates    
//		X509TrustManager tm = new X509TrustManager() {  
//
//			public void checkClientTrusted(X509Certificate[] xcs,  
//										   String string) throws CertificateException {  
//
//			}  
//
//			public void checkServerTrusted(X509Certificate[] xcs,  
//										   String string) throws CertificateException {  
//			}  
//
//			public X509Certificate[] getAcceptedIssuers() {  
//				return null;  
//			}  
//		};  
//		ctx.init(null, new TrustManager[] { tm }, null);  
//		
		//if(url.startsWith("https")){
		//socket=(SSLSocket)((SSLSocketFactory)SSLSocketFactory.getDefault()).createSocket(host,443);
		//}else{
		socket=new Socket();
		//}
		
		//连接
		if(socket.isConnected()){
		socket.close();
		}
		socket.connect(dest);
		
		//设置HTTP请求体
		OutputStreamWriter streamWriter=new OutputStreamWriter(socket.getOutputStream());
		BufferedWriter bufferWriter=new BufferedWriter(streamWriter);
		bufferWriter.write("GET "+path+"?"+query+" HTTP/1.0\r\n");
		bufferWriter.write("Host: "+host+"\r\n");
		bufferWriter.write(
			"Range: bytes=0-255\r\n"+
			"Pragma: no-cache\r\n"+
			"Cache-Control: no-cache\r\n"+
			"User-Agent: Mozilla/5.0 (Linux; Android 5.0.1; HTC M8d Build/LRX22C; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.121 Mobile Safari/537.36\r\n"+
			"Accept: */*\r\n");
		bufferWriter.write("\r\n");
		bufferWriter.flush();

		//获取请求响应
		BufferedInputStream streamReader=new BufferedInputStream(socket.getInputStream());
		BufferedReader bufferReader=new BufferedReader(new InputStreamReader(streamReader,"utf-8"));
		String resultData="";
		String line="";
		while((line=bufferReader.readLine())!=null){
			resultData+=line+"\r\n";
		}
		System.out.println(resultData);
		bufferReader.close();
		bufferWriter.close();
		socket.close();

		//解析文件长度
		if(isEmpty(resultData))return 0l;

		Matcher matcherRange=Pattern.compile("Content-Range: bytes 0-255/(.*)\r\n").matcher(resultData);
		if(matcherRange.find()){
			String range=matcherRange.group(1);
			contentLength=Long.parseLong(range);
			System.out.println("end time:"+System.currentTimeMillis());
			return contentLength;
		}else{
			Matcher matcherLength=Pattern.compile("Content-Length: (.*)\r\n").matcher(resultData);
			if(matcherLength.find()){
				String length=matcherLength.group(1);
				contentLength=Long.parseLong(length);
				System.out.println("end time:"+System.currentTimeMillis());
				return contentLength;
			}
		}

		return 0L;
	}

	private static boolean isEmpty(String resultData)
	{
		return "".equals(resultData)|null==resultData;
	}
}
