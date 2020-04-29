package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.util.Map;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws InterruptedException {
		// Thread.sleep(2 * 1000);

		connectTo(getCapiHostname());
		connectTo("example.com");

		SpringApplication.run(DemoApplication.class, args);
	}

	private static String getCapiHostname() {
		JsonParser springParser = JsonParserFactory.getJsonParser();
		Map<String, Object> vcapApplication = springParser.parseMap(System.getenv("VCAP_APPLICATION"));
		return ((String) vcapApplication.get("cf_api")).replace("https://", "");
	}

	private static void connectTo(String host) {
		try {
			SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(host, 443);
			// socket.startHandshake();
			socket.close();
			System.out.println("Connection succeeded to " + host + ":" + 443);
		} catch (Exception e) {
			System.out.println("ERROR connecting to " + host + ":" + 443);
			e.printStackTrace();
		}
	}

}
