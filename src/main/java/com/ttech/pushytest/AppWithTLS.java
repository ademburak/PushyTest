package com.ttech.pushytest;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLException;

import com.relayrides.pushy.apns.ApnsClient;
import com.relayrides.pushy.apns.ApnsClientBuilder;
import com.relayrides.pushy.apns.PushNotificationResponse;
import com.relayrides.pushy.apns.util.ApnsPayloadBuilder;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;
import com.relayrides.pushy.apns.util.TokenUtil;

import io.netty.util.concurrent.Future;

public class App 
{ 
    public static void main( String[] args ) throws InterruptedException, ExecutionException
    {
    	try {
			final ApnsClient apnsClient = new ApnsClientBuilder()
			        .setClientCredentials(new File("/Users/ttacakmak/Desktop/Certificates.p12"), "12345")
			        .build();
			
			final Future<Void> connectFuture = apnsClient.connect(ApnsClient.DEVELOPMENT_APNS_HOST);
			connectFuture.await();
			
			final SimpleApnsPushNotification pushNotification;

		    {
		        final ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
		        payloadBuilder.setAlertBody("Example!");

		        final String payload = payloadBuilder.buildWithDefaultMaximumLength();
		       // b150d157b7636fdc9073627d529b5763faed97ac7932f569d9370ff869868390
		       //voip a8a0ab743f133bfd04b4ccb4e15782bc63fbf0519fa353f755776c836dc63a8f
		        final String token = TokenUtil.sanitizeTokenString("a8a0ab743f133bfd04b4ccb4e15782bc63fbf0519fa353f755776c836dc63a8f");

		        pushNotification = new SimpleApnsPushNotification(token, "com.turkcell.bipdev.voip", payload);
		    }
			
			final Future<PushNotificationResponse<SimpleApnsPushNotification>> sendNotificationFuture =
			        apnsClient.sendNotification(pushNotification);
			
			
			 final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse =
			            sendNotificationFuture.get();

			    if (pushNotificationResponse.isAccepted()) {
			        System.out.println("Push notification accepted by APNs gateway.");
			    } else {
			        System.out.println("Notification rejected by the APNs gateway: " +
			                pushNotificationResponse.getRejectionReason());

			        if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
			            System.out.println("\t…and the token is invalid as of " +
			                pushNotificationResponse.getTokenInvalidationTimestamp());
			        }
			    }
			
		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    
    
}
