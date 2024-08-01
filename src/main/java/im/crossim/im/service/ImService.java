package im.crossim.im.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ospn.common.ECUtils;
import im.crossim.common.exception.SystemException;
import im.crossim.config.web.UserServiceConfig;
import im.crossim.user.service.LTPServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class ImService {

    @Autowired
    private LTPServer ltpServer;

    @Autowired
    private UserServiceConfig userServiceConfig;

    public byte[] sha256(byte[] data){
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data);
            data = messageDigest.digest();
        } catch (Exception e){
            log.error("[sha256]", e);
        }
        return data;
    }

    public String aesEncrypt(String data, String key){
        try {
            byte[] iv = new byte[16];
            Arrays.fill(iv,(byte)0);
            byte[] pwdHash = sha256(key.getBytes());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(pwdHash, "AES"), new IvParameterSpec(iv));
            byte[] encData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encData);
        }
        catch (Exception e){
            log.error("[aesEncrypt]", e);
        }
        return null;
    }

    public String aesDecrypt(String data, String key){
        try {
            byte[] iv = new byte[16];
            Arrays.fill(iv,(byte)0);
            byte[] rawData = Base64.getDecoder().decode(data);
            byte[] pwdHash = sha256(key.getBytes());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(pwdHash, "AES"), new IvParameterSpec(iv));
            rawData = cipher.doFinal(rawData);
            return new String(rawData);
        }
        catch (Exception e){
            log.error("[aesDecrypt]", e);
        }
        return null;
    }

    private String register2im(
            String imNodeIp, int imNodePort, String imNodePassword,
            String osnId, String owner, String name
    ) {
        String osnID = null;
        try {
            String imUrl = String.format(
                    "http://%s:%s/admin",
                    imNodeIp,
                    imNodePort
            );
            log.info("[registerOsn] imUrl:{}", imUrl);
            String imPwd = imNodePassword;
            log.info("[registerOsn] imPwd:{}", imPwd);
            URL url = new URL(imUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(20 * 1000);
            httpURLConnection.setReadTimeout(20 * 1000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            JSONObject json = new JSONObject();
            json.put("osnID", osnId);
            json.put("name", name);
            json.put("owner2", owner);
            String body = aesEncrypt(json.toString(),imPwd);
            json.clear();
            json.put("command", "register2");
            json.put("data", body);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(json.toString().getBytes());
            outputStream.flush();
            outputStream.close();

            if(httpURLConnection.getResponseCode() == 200){
                InputStream responseStream = httpURLConnection.getInputStream();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead = 0;
                while ((bytesRead = responseStream.read(buffer)) > 0) {
                    byteStream.write(buffer, 0, bytesRead);
                }
                String responseBody = byteStream.toString("utf-8");
                log.info("[registerOsn] register result:{}", responseBody);
                JSONObject jsonObject = JSONObject.parseObject(responseBody);
                if(!("0:success".equals(jsonObject.getString("errCode")))){
                    return "";
                }
                json = JSON.parseObject(responseBody);
                body = json.getString("data");
                if(body != null){

                    body = aesDecrypt(body,imPwd);
                    log.info("[registerOsn] register result data:{}", body);

                    jsonObject = JSONObject.parseObject(body);
                    osnID = jsonObject.getString("osnID");
                    log.info("[registerOsn] register osn id:{}", osnID);
                }
            } else {
                log.error("[registerOsn] http error:{}", httpURLConnection.getResponseCode());
            }
        }
        catch (Exception e){
            log.error("[registerOsn] exception", e);
        }
        return osnID;
    }

    public String[] createOsnID(String type, String aesKey) {
        return ECUtils.createOsnID2("user", "");
    }

    public String register2im(
            String imNodeIp, int imNodePort, String imNodePassword,
            String osnId
    ) {
        return register2im(
                imNodeIp, imNodePort, imNodePassword,
                osnId, LTPServer.getLtpData().apps.osnID, null
        );
    }

    public JSONObject setGroupMax(String groupId, int max) {
        try {
            String imUrl = userServiceConfig.getImConfig().getGroupServerUrl();
            log.info("[setGroupMax] imUrl:{}", imUrl);
            String imPwd = userServiceConfig.getImConfig().getGroupServerPassword();
            log.info("[setGroupMax] imPwd:{}", imPwd);
            URL url = new URL(imUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(20 * 1000);
            httpURLConnection.setReadTimeout(20 * 1000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            JSONObject json = new JSONObject();
            json.put("group", groupId);
            json.put("max", max);
            String body = aesEncrypt(json.toString(),imPwd);
            json.clear();
            json.put("command", "setGroupMax");
            json.put("data", body);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(json.toString().getBytes());
            outputStream.flush();
            outputStream.close();

            if(httpURLConnection.getResponseCode() == 200){
                InputStream responseStream = httpURLConnection.getInputStream();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead = 0;
                while ((bytesRead = responseStream.read(buffer)) > 0) {
                    byteStream.write(buffer, 0, bytesRead);
                }
                String responseBody = byteStream.toString("utf-8");
                log.info("[setGroupMax] response:{}", responseBody);
                JSONObject jsonObject = JSONObject.parseObject(responseBody);
                if(!("0:success".equals(jsonObject.getString("errCode")))){
                    return null;
                }
                json = JSON.parseObject(responseBody);
                body = json.getString("data");
                if(body != null){
                    // body是群信息
                    body = aesDecrypt(body,imPwd);

                    log.info("[setGroupMax] response data:{}", body);

                    jsonObject = JSONObject.parseObject(body);

                    return jsonObject;
                }
            } else {
                log.error("[setGroupMax] http error:{}", httpURLConnection.getResponseCode());
            }
        }
        catch (Exception e){
            log.error("[setGroupMax] exception", e);
        }
        return null;
    }

    public JSONObject createGroup(String groupName, String owner, String portrait, List<String> userList, int max) {
        try {
            String imUrl = userServiceConfig.getImConfig().getGroupServerUrl();
            log.info("[createGroup] imUrl:{}", imUrl);
            String imPwd = userServiceConfig.getImConfig().getGroupServerPassword();
            log.info("[createGroup] imPwd:{}", imPwd);
            URL url = new URL(imUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(20 * 1000);
            httpURLConnection.setReadTimeout(20 * 1000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            JSONObject json = new JSONObject();
            json.put("name", groupName);
            json.put("owner", owner);
            json.put("portrait", portrait);
            json.put("max", max);
            json.put("owner2", LTPServer.getLtpData().dappInfo.osnID);
            json.put("userList", userList);
            String body = aesEncrypt(json.toString(),imPwd);
            json.clear();
            json.put("command", "createGroup");
            json.put("data", body);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(json.toString().getBytes());
            outputStream.flush();
            outputStream.close();

            if(httpURLConnection.getResponseCode() == 200){
                InputStream responseStream = httpURLConnection.getInputStream();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead = 0;
                while ((bytesRead = responseStream.read(buffer)) > 0) {
                    byteStream.write(buffer, 0, bytesRead);
                }
                String responseBody = byteStream.toString("utf-8");
                log.info("[createGroup] response:{}", responseBody);
                JSONObject jsonObject = JSONObject.parseObject(responseBody);
                if(!("0:success".equals(jsonObject.getString("errCode")))){
                    return null;
                }
                json = JSON.parseObject(responseBody);
                body = json.getString("data");
                if(body != null){
                    // body是群信息
                    body = aesDecrypt(body,imPwd);

                    log.info("[createGroup] response data:{}", body);

                    jsonObject = JSONObject.parseObject(body);

                    return jsonObject;
                }
            } else {
                log.error("[createGroup] http error:{}", httpURLConnection.getResponseCode());
            }
        }
        catch (Exception e){
            log.error("[createGroup] exception", e);
        }
        return null;
    }

}
