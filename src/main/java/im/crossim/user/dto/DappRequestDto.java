package im.crossim.user.dto;

import com.ospn.command.CmdDappLogin;
import com.ospn.command.CmdGetServerInfo;
import lombok.Data;

@Data
public class DappRequestDto {

    /*

    req.put("command", "Login");
        req.put("username", username);
        req.put("user", userOsnId);
        req.put("hash", calcHash);
        req.put("sign", calcSign);
        req.put("session", session);

     */


    public String username;     // Login



    public String project;

    public String command;
    public String user;         // GetLoginInfo   Login
    public String sign;         // Login
    public String hash;
    public String data;
    public long random;         // GetLoginInfo

    public CmdDappLogin getCmdDappLogin(){
        CmdDappLogin cmd = new CmdDappLogin();
        cmd.command = command;
        cmd.user = user;
        cmd.sign = sign;
        cmd.hash = hash;
        return cmd;
    }

    public CmdGetServerInfo getCmdGetServerInfo(){
        CmdGetServerInfo cmd = new CmdGetServerInfo();
        cmd.command = command;
        cmd.user = user;
        cmd.random = random;
        return cmd;
    }

}
