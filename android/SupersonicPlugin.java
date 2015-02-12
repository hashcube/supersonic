package com.tealeaf.plugin.plugins;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import com.tealeaf.EventQueue;
import com.tealeaf.TeaLeaf;
import com.tealeaf.logger;
import com.tealeaf.event.*;
import com.tealeaf.plugin.IPlugin;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import com.supersonicads.sdk.*;
import com.supersonicads.sdk.listeners.OnOfferWallListener;
import com.supersonicads.sdk.SSAFactory;
import com.supersonicads.sdk.SSAPublisher;
import com.supersonicads.sdk.data.AdUnitsReady;
import com.supersonicads.sdk.listeners.OnOfferWallListener;
import com.supersonicads.sdk.utils.SDKUtils;


public class SupersonicPlugin implements IPlugin {

  HashMap<String, String> manifestKeyMap = new HashMap<String,String>();
  Map<String, String> params = new HashMap<String, String>();
  SSAPublisher ssaPub;
  String supersonicAppKey;
  String userID;
  Activity myActivity;

  private class SupersonicListener implements OnOfferWallListener{

    public void onOWShowSuccess(){
      logger.log("{supersonic} onOWShowSuccess");
    }

    public void onOWGeneric(String arg0, String arg1) {
    }


    public void onRVGeneric(String arg0, String arg1) {
    }
    /**
      * Invoked when the method 'showOfferWall' is called and the OfferWall fails to load.
      * Handle initialization error here.
      * @param description - A String which represents the reason of 'showOfferWall' failure.
      */
    public void onOWShowFail(String description){
      logger.log("{supersonic} onOWShowFail");
    }

    /**
      * Invoked each time the user completes an Offer.
      * Award the user with the credit amount corresponding to the value of the ‘credits’
      * parameter.
      * @param credits - The number of credits the user has earned.
      * @param totalCredits - The total number of credits ever earned by the user.
      * @param totalCreditsFlag - In some cases, we won’t be able to provide the exact
      * amount of credits since the last event (specifically if the user clears
      * the app’s data). In this case the ‘credits’ will be equal to the ‘totalCredits’,
      * and this flag will be ‘true’.
      * @return boolean - true if you received the callback and rewarded the user,
      * otherwise false.
      */
    public boolean onOWAdCredited(int credits, int totalCredits, boolean totalCreditsFlag){
      logger.log("{supersonic} onOWAdCredited");
      EventQueue.pushEvent(new onCreditReceived(credits));
      return true;
    }

    /**
      * Invoked when the method 'getOfferWallCredits' fails to retrieve
      * the user's credit balance info.
      * @param description - A String which represents the reason of 'getOfferWallCredits'
      * failure.
      */
    public void onGetOWCreditsFailed(String description){
      logger.log("{supersonic} onGetOWCreditsFailed");
    }

    /**
      * Invoked when the user is about to return to the application after closing
      * the Offerwall.
      */
    public void onOWAdClosed(){
      logger.log("{supersonic} onOWAdClosed");
    }

 }

  public class onCreditReceived extends com.tealeaf.event.Event {

    public onCreditReceived(int credits) {
      int credits;

      super("onCreditReceived");
      this.credits = credits;
    }

  }

  public SupersonicPlugin() {
  }

  public void onCreateApplication(Context applicationContext) {
  }

  public void onCreate(Activity activity, Bundle savedInstanceState) {
    PackageManager manager = activity.getBaseContext().getPackageManager();
    String key = "supersonicAppKey";
    myActivity = activity;
    try {
      Bundle meta = manager.getApplicationInfo(activity.getApplicationContext().getPackageName(),
          PackageManager.GET_META_DATA).metaData;
      if (meta.containsKey(key)) {
        supersonicAppKey = meta.get(key).toString();
      }
    } catch (Exception e) {
      logger.log("{supersonic} Exception while loading manifest keys:", e);
    }

    logger.log("{supersonic} Installing for appKey:", supersonicAppKey);

    //Initialize the SDK, passing the current context to the method
    ssaPub = SSAFactory.getPublisherInstance(activity);
    params.put("useClientSideCallbacks", "true");
  }


  public void showOffersForUserID(String jsonData) {
    logger.log("{supersonic} showOffers called");
    try {
      JSONObject jsonObject = new JSONObject(jsonData);
      userID = jsonObject.getString("userID");
    } catch (Exception e) {
      logger.log("{supersonic} WARNING: Failure in getting userID:", e);
      e.printStackTrace();
    }

    ssaPub.showOfferWall(supersonicAppKey, userID, params, new SupersonicListener());
  }

  public void onResume() {
    if (ssaPub != null) {
      ssaPub.onResume(myActivity);
    }
  }

  public void onStart() {
  }

  public void onPause() {
    if (ssaPub != null) {
      ssaPub.onPause(myActivity);
    }
  }

  public void onStop() {
  }

  public void onDestroy() {
    if (ssaPub != null) {
      // Release the SDK resources
      ssaPub.release(myActivity);
    }
  }

  public void onNewIntent(Intent intent) {
  }

  public void setInstallReferrer(String referrer) {
  }

  public void onActivityResult(Integer request, Integer result, Intent data) {
  }

  public void logError(String error) {
  }

  public boolean consumeOnBackPressed() {
    return true;
  }

  public void onBackPressed() {
  }

}
