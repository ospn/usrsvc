package im.crossim.push.service;

import java.util.List;

public interface VendorPushService {

    int getVendor();

    boolean pushMessage(String title, String body, List<String> deviceIds);

}
