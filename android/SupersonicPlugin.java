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

import com.supersonic.adapters.supersonicads.SupersonicConfig;
import com.supersonic.mediationsdk.logger.SupersonicError;
import com.supersonic.mediationsdk.model.Placement;
import com.supersonic.mediationsdk.sdk.InterstitialListener;
import com.supersonic.mediationsdk.sdk.OfferwallListener;
import com.supersonic.mediationsdk.sdk.RewardedVideoListener;
import com.supersonic.mediationsdk.sdk.Supersonic;
import com.supersonic.mediationsdk.sdk.SupersonicFactory;
import com.supersonic.mediationsdk.utils.SupersonicUtils;
import com.supersonicads.sdk.agent.SupersonicAdsAdvertiserAgent;

public class SupersonicPlugin implements IPlugin {

  Context _ctx = null;
  Activity _activity = null;
  String appKey = "";
  private Supersonic mSupersonicInstance;
  private SupersonicListener listener = null;

  private Placement mPlacement;
  private Integer rewardedCount = 0;

  private class SupersonicListener implements InterstitialListener, RewardedVideoListener, OfferwallListener {
    /************************************************************
     *         Supersonic Interstitial Listeners
     ************************************************************
     */

    /**
     * Invoked when Interstitial initialization process completes successfully.
     */
    public void onInterstitialInitSuccess() {
      logger.log("{supersonic} onInterstitialInitSuccess");
    }

    /**
     * Invoked when Interstitial initialization process is failed.
     * @param supersonicError - An Object which represents the reason of initialization failure.
     */
    public void onInterstitialInitFailed(SupersonicError supersonicError) {
      logger.log("{supersonic} onInterstitialInitFail", supersonicError);
    }

    /**
      Invoked when Interstitial Ad is ready to be shown after load function was called.
     */
    public void onInterstitialReady() {
      logger.log("{supersonic} onInterstitialReady");
      EventQueue.pushEvent(new SupersonicAdAvailable());
    }

    /**
      invoked when there is no Interstitial Ad available after calling load function.
     */
    public void onInterstitialLoadFailed(SupersonicError supersonicError) {
      logger.log("{supersonic} onInterstitialLoadFailed", supersonicError);
      EventQueue.pushEvent(new SupersonicAdNotAvailable());
    }

    /*
     * Invoked when the ad was opened and shown successfully.
     */
    public void onInterstitialShowSuccess() {
      logger.log("{supersonic} onInterstitialShowSuccess");
    }

    /**
     * Invoked when Interstitial ad failed to show.
     * @param supersonicError - An object which represents the reason of showInterstitial failure.
     */
    public void onInterstitialShowFailed(SupersonicError supersonicError) {
      logger.log("{supersonic} onInterstitialShowFailed", supersonicError);
    }

    /*
     * Invoked when the end user clicked on the interstitial ad.
     */
    public void onInterstitialClick() {
    }

    /*
     * Invoked when the ad is closed and the user is about to return to the application.
     */
    public void onInterstitialClose() {
      logger.log("{supersonic} onInterstitialClose");
      EventQueue.pushEvent(new SupersonicAdDismissed());
    }

    /**
      Invoked when the Interstitial Ad Unit is opened
     */
    public void onInterstitialOpen() {
    }

    /************************************************************
     *         Supersonic Offerwall Listeners
     ************************************************************
     */
    @Override
    public void onOfferwallInitSuccess() {
      logger.log("{supersonic} onOfferwallInitSuccess");
    }

    @Override
    public void onOfferwallInitFail(SupersonicError supersonicError) {
      logger.log("{supersonic} onOfferwallInitFail");
    }

    @Override
    public void onOfferwallOpened() {
      logger.log("{supersonic} onOfferwallOpened");
    }

    /**
      * Invoked when the method 'showOfferWall' is called and the OfferWall fails to load.
      * Handle initialization error here.
      * @param description - A String which represents the reason of 'showOfferWall' failure.
      */
    @Override
    public void onOfferwallShowFail(SupersonicError supersonicError) {
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
    @Override
    public boolean onOfferwallAdCredited(int credits, int totalCredits, boolean totalCreditsFlag) {
      logger.log("{supersonic} onOWAdCredited");
      EventQueue.pushEvent(new onOWAdCredited(credits));
      return true;
    }

    /**
      * Invoked when the method 'getOfferWallCredits' fails to retrieve
      * the user's credit balance info.
      * @param description - A String which represents the reason of 'getOfferWallCredits'
      * failure.
      */
    @Override
    public void onGetOfferwallCreditsFail(SupersonicError supersonicError) {
      logger.log("{supersonic} onGetOWCreditsFailed", supersonicError);
    }

    /**
      * Invoked when the user is about to return to the application after closing
      * the Offerwall.
      */
    @Override
    public void onOfferwallClosed() {
      logger.log("{supersonic} onOWAdClosed");
    }

    /************************************************************
     *         Supersonic RewardedVideo Listeners
     ************************************************************
     */
    @Override
    public void onRewardedVideoInitSuccess() {
      logger.log("{supersonic} onRewardedVideoInitSuccess");
    }

    @Override
    public void onRewardedVideoInitFail(SupersonicError supersonicError) {
      logger.log("{supersonic} onRewardedVideoInitFail", supersonicError);
      mPlacement = null;
    }

    @Override
    public void onRewardedVideoAdOpened() {
      logger.log("{supersonic} onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoAdClosed() {
      logger.log("{supersonic} onRewardedVideoAdClosed");
      EventQueue.pushEvent(new onRVAdClosed(mPlacement, rewardedCount));
      mPlacement = null;
      rewardedCount = 0;
    }

    @Override
    public void onVideoAvailabilityChanged(final boolean available) {
      logger.log("{supersonic} onVideoAvailabilityChanged");
      EventQueue.pushEvent(new onRVAvailabilityChange(available));
    }

    @Override
    public void onVideoStart() {
      logger.log("{supersonic} onVideoStart");
    }

    @Override
    public void onVideoEnd() {
      logger.log("{supersonic} onVideoEnd");
    }

    @Override
    public void onRewardedVideoShowFail(SupersonicError supersonicError) {
      logger.log("{supersonic} onRewardedVideoShowFail", supersonicError);
    }

    @Override
    public void onRewardedVideoAdRewarded(Placement placement) {
      logger.log("{supersonic} onRewardedVideoAdRewarded");
      mPlacement = placement;
      rewardedCount++;
    }
  }

  public class SupersonicAdNotAvailable extends Event {

    public SupersonicAdNotAvailable() {
      super("SupersonicAdNotAvailable");
    }
  }

  public class SupersonicAdAvailable extends Event {

    public SupersonicAdAvailable() {
      super("SupersonicAdAvailable");
    }
  }

  public class SupersonicAdDismissed extends Event {

    public SupersonicAdDismissed() {
      super("SupersonicAdDismissed");
    }
  }

  public class onOWAdCredited extends Event {
    int credits;

    public onOWAdCredited(int credits) {
      super("supersonicOWCredited");
      logger.log("{supersonic} Credits received:", credits);
      this.credits = credits;
    }
  }

  public class onRVAdClosed extends Event {
    String placement = null;
    Integer rewardedCount = 0;

    public onRVAdClosed(Placement placement, Integer rewardedCount) {
      super("supersonicRVAdClosed");
      if(placement != null) {
        this.placement = placement.getRewardName();
        this.rewardedCount = rewardedCount;
      }
      logger.log("{supersonic} RVAd rewarded received:", this.placement, this.rewardedCount);
    }
  }

  public class onRVAvailabilityChange extends Event {
    boolean available;

    public onRVAvailabilityChange(boolean available) {
      super("supersonicOnRVAvailabilityChange");
      logger.log("{supersonic} supersonicOnRVAvailabilityChange:", available);
      this.available = available;
    }
  }

  private String getUserId(String json) {
    String userId = "";

    try {
      JSONObject jsonObject = new JSONObject(json);
      userId = jsonObject.getString("user_id");
    } catch (Exception e) {
      logger.log("{supersonic} exception", e);
    }
    return userId;
  }

  public SupersonicPlugin() {
  }

  public void onCreateApplication(Context applicationContext) {
    _ctx = applicationContext;
  }

  public void onCreate(Activity activity, Bundle savedInstanceState) {
    PackageManager manager = activity.getBaseContext().getPackageManager();
    _activity = activity;

    try {
      Bundle meta = manager.getApplicationInfo(activity.getApplicationContext().getPackageName(),
          PackageManager.GET_META_DATA).metaData;
      appKey = meta.getString("supersonicAppKey");
    } catch (Exception e) {
      logger.log("{supersonic} Exception while loading manifest keys:", e);
    }

    logger.log("{supersonic} Installing for appKey:", appKey);

    if(listener == null) {
      listener = new SupersonicListener();
    }
    //Initialize the SDK, passing the current context to the method
    mSupersonicInstance = SupersonicFactory.getInstance();
  }

  public void initInterstitial(String jsonData) {
    logger.log("{supersonic} Init Interstitial");

    if(mSupersonicInstance != null) {
      mSupersonicInstance.setInterstitialListener(listener);
      mSupersonicInstance.initInterstitial(_activity, appKey, getUserId(jsonData));
    }
  }

  public void initVideoAd(String jsonData) {
    logger.log("{supersonic} Init video Ad");

    if(mSupersonicInstance != null) {
      mSupersonicInstance.setRewardedVideoListener(listener);
      mSupersonicInstance.initRewardedVideo(_activity, appKey, getUserId(jsonData));
    }
  }

  public void initOfferWallAd(String jsonData) {
    logger.log("{supersonic} Init offerwall Ad");

    if(mSupersonicInstance != null) {
      mSupersonicInstance.setOfferwallListener(listener);
      mSupersonicInstance.initOfferwall(_activity, appKey, getUserId(jsonData));
    }
  }

  public void cacheInterstitial(String jsonData) {
    logger.log("{supersonic} loadInterstitial");
    mSupersonicInstance.loadInterstitial();
  }

  public void showInterstitial(String jsonData) {
    logger.log("{supersonic} showInterstitial called");
    mSupersonicInstance.showInterstitial();
  }

  public void showOffersForUserID(String jsonData) {
    logger.log("{supersonic} showOffers called");

    if(mSupersonicInstance.isOfferwallAvailable()) {
      mSupersonicInstance.showOfferwall();
    }
  }

  public void showRVAd(String jsonData) {
    logger.log("{supersonic} showRewardedVideo called");

    if(mSupersonicInstance.isRewardedVideoAvailable()) {
      mSupersonicInstance.showRewardedVideo();
    }
  }

  public void onResume() {
    if (mSupersonicInstance != null) {
      mSupersonicInstance.onResume(_activity);
    }
  }

  public void onRenderResume() {
  }

  public void onStart() {
  }

  public void onFirstRun() {
  }

  public void onPause() {
    if (mSupersonicInstance != null) {
      mSupersonicInstance.onPause(_activity);
    }
  }

  public void onRenderPause() {
  }

  public void onStop() {
  }

  public void onDestroy() {
    if (mSupersonicInstance != null) {
      // Release the SDK resources
      mSupersonicInstance.release(_activity);
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
