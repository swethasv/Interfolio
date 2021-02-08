package com.tf.intf.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class HMAC_Encryption {
	
	@Autowired
	private Environment environment;
	
	public String getTenantId() {
		return environment.getProperty("tenant_id");
	}
	
	public String getHost() {
		return environment.getProperty("host_details");
	}
	
	public String getPublicKey() {
		return environment.getProperty("public_key");
	}
	
	public String getPrivateKey() {
		return environment.getProperty("private_key");
	}
	// Value should be either 'faculty180', or 'byc' (if you are using FS or RPT)
	public static String product = "byc";
	
	public String timestampString() {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		String timestamp_string = dateFormatGmt.format(new Date());

		return timestamp_string;
	}

	public String gen_HMAC(String request_string, String query_string, String request_verb) {
		String HMAC_request_string = request_string;
		if (product == "byc") {
			HMAC_request_string = HMAC_request_string + query_string;
		}

		String verbReq = request_verb + "\n\n\n" + timestampString() + "\n" + HMAC_request_string;
		try {
			Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
			SecretKeySpec secret_key = new SecretKeySpec(getPrivateKey().getBytes(), "HmacSHA1");
			sha1_HMAC.init(secret_key);
			String output_hash = org.apache.commons.codec.binary.Base64
					.encodeBase64String(sha1_HMAC.doFinal(verbReq.getBytes()));

			String authorization_header = "INTF " + getPublicKey() + output_hash;

			return authorization_header;
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return null;
		}
	}

	public String gen_HMAC(String query_string, String request_verb) {
		String HMAC_request_string = "/byc-tenure/" + getTenantId() + "/packet_templates/";
		if (product == "byc") {
			HMAC_request_string = HMAC_request_string + query_string;
		}

		String verbReq = request_verb + "\n\n\n" + timestampString() + "\n" + HMAC_request_string;
		try {
			Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
			SecretKeySpec secret_key = new SecretKeySpec(getPrivateKey().getBytes(), "HmacSHA1");
			sha1_HMAC.init(secret_key);
			String output_hash = org.apache.commons.codec.binary.Base64
					.encodeBase64String(sha1_HMAC.doFinal(verbReq.getBytes()));

			String authorization_header = "INTF " + getPublicKey() + output_hash;

			return authorization_header;
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return null;
		}
	}

	public String getResults(String request_string, String query_string) throws IOException {
		String full_request = getHost() + request_string + query_string;

		URL url = new URL(full_request);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Timestamp", timestampString());
		conn.setRequestProperty("Authorization", gen_HMAC(request_string, query_string));
		conn.setRequestProperty("INTF-DatabaseID", getTenantId());

		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}

		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		conn.disconnect();
		return sb.toString();
	}

	public HttpURLConnection getConnection(String request_string, String query_string, String request_verb)
			throws IOException {
		String full_request = getHost() + request_string + query_string;
		System.out.println("Calling: " + full_request);
		URL url = new URL(full_request);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Timestamp", timestampString());
		conn.setRequestProperty("Authorization", gen_HMAC(request_string, query_string, request_verb));
		conn.setRequestProperty("INTF-DatabaseID", getTenantId());
		conn.setRequestMethod(request_verb);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("UserVO-Agent", "Java client");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoInput(true);
		conn.setDoOutput(true);

		return conn;
	}
}
