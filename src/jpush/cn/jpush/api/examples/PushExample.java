package cn.jpush.api.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.PushPayload.Builder;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

/**
 * 
 * 极光推送 友盟账户
 * 帐号： admin@skillopedia.cc       
 * 密码：Skillopedia123
 * @author Administrator
 *
 */
public class PushExample {
	protected static final Logger LOG = LoggerFactory.getLogger(PushExample.class);

	// demo App defined in resources/jpush-api.conf
	private static final String appKey = "f2597c0a4a57ec1af841b4e2";
	private static final String masterSecret = "146ac67a13bcdb17f7aa29af";

	public static final String MSG_CONTENT = "";
	public static final String REGISTRATION_ID = "";
	public static final String TAG = "siji";

	public static void main(String[] args) {
		pushToUser("2818921504@qq.com","test");
		System.out.println("done");
	}

	public static boolean pushToUser(String telephone, String title) {
		JPushClient jpushClient = new JPushClient(masterSecret, appKey, 3);
		PushPayload payload = buildPushObject_all_alias_alert(telephone,title);
		try {
			PushResult result = jpushClient.sendPush(payload);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static PushPayload buildPushObject_all_alias_alert(String telephone,String title) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.tag(telephone))
                .setNotification(Notification.alert(title))
                .build();
}
public static boolean pushToUser2(String telephone, String title,int message_id) {
	JPushClient jpushClient = new JPushClient(masterSecret, appKey, 3);
	PushPayload payload = buildPushObject_all_alias_alert2(telephone,title,message_id);
	try {
		PushResult result = jpushClient.sendPush(payload);
		return true;
	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}
}
public static PushPayload buildPushObject_all_alias_alert23(String telephone,String title,int message_id) {
	return PushPayload
			.newBuilder()
			.setPlatform(Platform.android_ios())
			.setAudience(Audience.tag(telephone))
			.setNotification(
					Notification
							.newBuilder()
							.setAlert(title)
							.addPlatformNotification(AndroidNotification.newBuilder().setTitle(title).addExtra("message_id", message_id).build())
							.addPlatformNotification(IosNotification.newBuilder().incrBadge(1).addExtra("message_id", message_id).build()).build())
			.setOptions(Options.newBuilder().setApnsProduction(true).build()).build();
}
public static PushPayload buildPushObject_all_alias_alert2(String telephone,String title,int message_id) {
    return PushPayload.newBuilder()
            .setPlatform(Platform.all())
            .setAudience(Audience.tag(telephone))
            .setNotification(Notification.alert(title))
            .build();
}


}
