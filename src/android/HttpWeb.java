package gl.plugins;

import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Set;
import java.util.Map;

import android.graphics.Bitmap;  
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpWeb extends CordovaPlugin {
    private JSONObject jsonObject;
	private JSONObject jsonParams;
	private String url; 
	private String params; 
	private String showCookie;
	private String setCookie;
	private String setReferer;
	private String charset;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
		url = args.getString(0); 
		params = args.getString(1); 
		showCookie = null;
		setCookie = null;
		setReferer = null;
		charset = "utf-8";
			
		jsonParams = args.getJSONObject(2);
		if(jsonParams.has("setCookie")){			
			setCookie = jsonParams.getString("setCookie"); 
		}
		if(jsonParams.has("setReferer")){			
			setReferer = jsonParams.getString("setReferer"); 
		}
		if(jsonParams.has("showCookie")){			
			showCookie = jsonParams.getString("showCookie"); 
		}
		if(jsonParams.has("charset")){			
			charset = jsonParams.getString("charset"); 
		}

		if("get".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {  
				public void run() {  
					String res = sendGet(url, params, setCookie, showCookie, charset, setReferer);
					callbackContext.success(res);  
				}  
			});  
			return true;
        }else if("post".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {  
				public void run() {  
					String res = sendPost(url, params, setCookie, charset, setReferer);
					callbackContext.success(res);  
				}  
            });  
            return true;
        }else if("getImage".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {  
				public void run() {  
					String res = sendGetImage(url, params, setCookie, showCookie, setReferer);
					callbackContext.success(res);  
				}  
            });  
            return true;
        }else if("getDate".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {  
				public void run() {  
					String res = getNetworkTime();
					callbackContext.success(res);  
				}  
            });  
            return true;
        }    

        callbackContext.error("error");  
        return false;  
    }

	//public String sendGet(String url, String param, String setCookie, String showCookie) {
	//	String setReferer = null;
	//	return sendGet(url, param, setCookie, showCookie, "utf-8", setReferer);
	//}

    public String sendGet(String url, String param, String setCookie, String showCookie, String charset, String setReferer) {
        jsonObject = new JSONObject();
        
        String cookie = "";
        String data = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
           
            URLConnection connection = realUrl.openConnection();
            connection.setConnectTimeout(3000);
			connection.setReadTimeout(30000);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
           
		    if (setCookie != null) {  
				connection.setRequestProperty("Cookie", setCookie);  
			}  
			if (setReferer != null) {  
				connection.setRequestProperty("Referer", setReferer);  
			}  
            connection.connect();
          
            if("1".equals(showCookie)){
                //Map headers = connection.getHeaderFields();
                //Set<String> keys = headers.keySet();
                //for (String key : keys) {
                //	//System.out.println(key + "--->" + map.get(key));
                //	String val = connection.getHeaderField(key);
                //	result += key + "=" + val+",";
                //}

				
                String key = null;
                for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++ ) {
					String value = connection.getHeaderField(i);
					if (key.equalsIgnoreCase("Set-Cookie")) {
						if (value == null)
							continue;
						cookie +=  value;
					}
				}

				jsonObject.put("cookie", cookie);
			}
         
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(),charset));
			String line;
			while ((line = in.readLine()) != null) {
				data += line;
			}

			jsonObject.put("ret", "0");
			jsonObject.put("data", data);
				
			return jsonObject.toString();  
		} catch (Exception e) {
			String errMsg = e.toString().replace("\"","'");
			return "{\"ret\":\"-1\",\"data\":\""+ errMsg +"\"}";  
		}
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				//e2.printStackTrace();
			}
		}   
    }

	public String sendGetImage(String url, String param, String setCookie, String showCookie, String setReferer) {
        jsonObject = new JSONObject();
        
        String cookie = "";
        String data = "";
        InputStream in = null;
		Bitmap map = null;
		ByteArrayOutputStream baos = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
           
            URLConnection connection = realUrl.openConnection();
            connection.setConnectTimeout(3000);
			connection.setReadTimeout(30000);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
           
		    if (setCookie != null) {  
				connection.setRequestProperty("Cookie", setCookie);  
			}  
			if (setReferer != null) {  
				connection.setRequestProperty("Referer", setReferer);  
			}  
            connection.connect();
          
            if("1".equals(showCookie)){				
                String key = null;
                for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++ ) {
					String value = connection.getHeaderField(i);
					if (key.equalsIgnoreCase("Set-Cookie")) {
						if (value == null)
							continue;
						cookie +=  value;
					}
				}

				jsonObject.put("cookie", cookie);
			}
         
			in = connection.getInputStream();
			map = BitmapFactory.decodeStream(in);
			baos = new ByteArrayOutputStream();
			map.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] bitmapBytes = baos.toByteArray();
			data = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			jsonObject.put("ret", "0");
			jsonObject.put("data", data);
				
			return jsonObject.toString();  
		} catch (Exception e) {
			String errMsg = e.toString().replace("\"","'");
			return "{\"ret\":\"-1\",\"data\":\""+ errMsg +"\"}";  
		}
		finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (Exception e2) {
				//e2.printStackTrace();
			}
		}   
    }

	//public String sendPost(String url, String param, String setCookie) {
	//	String setReferer = null;
	//	return sendGet(url, param, setCookie, "utf-8", setReferer);
	//}

	public String sendPost(String url, String param, String setCookie, String charset, String setReferer) {  
        PrintWriter out = null;  
        BufferedReader in = null;  
        jsonObject = new JSONObject();
        String data = "";
        try {  
            URL realUrl = new URL(url);  
            URLConnection connection = realUrl.openConnection();  
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(30000);
            connection.setRequestProperty("accept", "*/*");  
            connection.setRequestProperty("connection", "Keep-Alive");  
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
           
		    if (setCookie != null) {  
				connection.setRequestProperty("Cookie", setCookie);  
			}  
			if (setReferer != null) {  
				connection.setRequestProperty("Referer", setReferer);  
			}  
            connection.setDoOutput(true);  
            connection.setDoInput(true);  
            out = new PrintWriter(connection.getOutputStream());  
            out.print(param);  
            out.flush();  

            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),charset));  
            String line;  
            while ((line = in.readLine()) != null) {  
               data += line;
            }  

			jsonObject.put("ret", "0");
			jsonObject.put("data", data);
				
			return jsonObject.toString();  
        } catch (Exception e) {  
			String errMsg = e.toString().replace("\"","'");
			return "{\"ret\":\"-1\",\"data\":\""+ errMsg +"\"}";  
        }  
        finally {  
            try {  
                if (out != null) {  
                    out.close();  
                }  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                //ex.printStackTrace();  
            }  
        }  
    }  

	public String getNetworkTime() {
		jsonObject = new JSONObject();
        try {
            URL url = new URL("http://www.baidu.com");
            URLConnection conn = url.openConnection();
            conn.connect();
            long dateL = conn.getDate();
            Date date = new Date(dateL);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
			jsonObject.put("ret", "0");
			jsonObject.put("data", dateFormat.format(date));
				
			return jsonObject.toString();  
        } catch (Exception e) {  
			String errMsg = e.toString().replace("\"","'");
			return "{\"ret\":\"-1\",\"data\":\""+ errMsg +"\"}";  
        }  
    }

}