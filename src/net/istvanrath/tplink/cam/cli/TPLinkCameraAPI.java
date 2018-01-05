package net.istvanrath.tplink.cam.cli;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TP-Link webcam API driver
 * @author istvanrath
 *
 */
public class TPLinkCameraAPI {

	private BasicCookieStore cookieStore;
	private CloseableHttpClient client;
	private String username;
	private String pass_b64;
	private String host;
	private String token;

	public TPLinkCameraAPI(boolean debug, String camhost, String user, String pass) {
		if (debug) {
			// enable detailed http logging
			System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
			System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
		}
		cookieStore = new BasicCookieStore();

		RequestConfig globalConfig = RequestConfig.custom()
				.setCookieSpec(CookieSpecs.DEFAULT)
				.build();

		RequestConfig localConfig = RequestConfig.copy(globalConfig)
				.setCookieSpec(CookieSpecs.STANDARD)
				.build();

		client = HttpClients.custom()
				.setDefaultRequestConfig(localConfig)
				.setDefaultCookieStore(cookieStore)
				.build();
		
		this.token = "";
		this.host = camhost;
		this.username = user;
		this.pass_b64 = Base64.getEncoder().encodeToString(pass.getBytes());
	}
	
	public void doLogin() throws ClientProtocolException, IOException {
		// perform login
		HttpUriRequest loginrequest = RequestBuilder.post("http://"+host+"/login.fcgi")
				.addParameter("Username", username)
				.addParameter("Password", pass_b64)
				.setCharset(StandardCharsets.UTF_8)
				.build();

		HttpResponse loginresponse = client.execute(loginrequest);
		HttpEntity loginentity = loginresponse.getEntity();
		if (loginentity != null) {
			String json = EntityUtils.toString(loginentity);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonobj = mapper.readTree(json);
			token = jsonobj.get("token").asText();
			EntityUtils.consume(loginentity);
		}
	}
	
	public void doLogout() throws ClientProtocolException, IOException {
		// perform logout
		HttpUriRequest logoutrequest = RequestBuilder.post("http://"+host+"/logout.fcgi")
				.addParameter("token", token)
				.setCharset(StandardCharsets.UTF_8)
				.build();
		HttpResponse logoutresponse = client.execute(logoutrequest);
		EntityUtils.consume(logoutresponse.getEntity());
		client.close();
	}
	
	public void setLed(boolean on) throws ClientProtocolException, IOException {
		// set led status
		HttpUriRequest ledrequest = RequestBuilder.post("http://"+host+"/ledsetting.fcgi")
				.addParameter("token", token)
				.addParameter("enable", on?"1":"0")
				.setCharset(StandardCharsets.UTF_8)
				.build();

		HttpResponse ledresponse = client.execute(ledrequest);
		HttpEntity ledentity = ledresponse.getEntity();
		EntityUtils.consume(ledentity);
	}
	
	public void setMotion(boolean on) throws ClientProtocolException, IOException {
		// set motion
		HttpUriRequest request = RequestBuilder.post("http://"+host+"/mdconf_set.fcgi")
				.addParameter("token", token)
				.addParameter("is_enable", on?"1":"0")
				.setCharset(StandardCharsets.UTF_8)
				.build();

		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		EntityUtils.consume(entity);
	}
	
	public void setSound(boolean on) throws ClientProtocolException, IOException {
		// set motion
		HttpUriRequest request = RequestBuilder.post("http://"+host+"/SetAudioDetection.fcgi")
				.addParameter("token", token)
				.addParameter("adStatus", on?"1":"0")
				.addParameter("adSensitivity", "2")
				.setCharset(StandardCharsets.UTF_8)
				.build();

		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		EntityUtils.consume(entity);
	}
/*	
	private void addMagicCookies() {
		cookieStore.addCookie(new BasicClientCookie("Token", token));
		cookieStore.addCookie(new BasicClientCookie("StreamAccount", username));
		cookieStore.addCookie(new BasicClientCookie("StreamPassword", pass_b64));
		cookieStore.addCookie(new BasicClientCookie("Account", username));
		cookieStore.addCookie(new BasicClientCookie("UserName", username));
		cookieStore.addCookie(new BasicClientCookie("RemAccount", "true"));
		cookieStore.addCookie(new BasicClientCookie("isAdmin", "true"));
		cookieStore.addCookie(new BasicClientCookie("tplanguage", "EN"));
		cookieStore.addCookie(new BasicClientCookie("CameraType", "NC250(UN)%201.0"));

		for (Cookie c : cookieStore.getCookies()) {
			if (c instanceof BasicClientCookie) {
				BasicClientCookie bc = (BasicClientCookie) c;
				if (bc.getDomain() == null) {
					bc.setDomain(this.host);
				}
				bc.setPath("/");
				bc.setVersion(1);
			}
		}
	}
*/
}
