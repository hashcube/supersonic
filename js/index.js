import device;
import event.Emitter as Emitter;
import util.setProperty as setProperty;

var is_online = navigator.onLine;

var is_rv_connected = false;

var rv_source = null;
/*
 * Flag for RewardVideo init status
 */
var is_rv_available = false;

/*
 * Flag for OfferWall init status
 */
var is_ow_available = false;

var Supersonic = Class(Emitter, function (supr) {
  this.init = function() {
    supr(this, 'init', arguments);

    setProperty(this, "onAdDismissed", {
      set: function(f) {
        if (typeof f === "function") {
          onAdDismissed = f;
        } else {
          onAdDismissed = null;
        }
      },
      get: function() {
        return onOfferClose;
      }
    });

    setProperty(this, "onAdAvailable", {
      set: function(f) {
        if (typeof f === "function") {
          onAdAvailable = f;
        } else {
          onAdAvailable = null;
        }
      },
      get: function() {
        return onAdAvailable;
      }
    });

    setProperty(this, "onAdNotAvailable", {
      set: function(f) {
        if (typeof f === "function") {
          onAdNotAvailable = f;
        } else {
          onAdNotAvailable = null;
        }
      },
      get: function() {
        return onAdNotAvailable;
      }
    });

    setProperty(this, "onVideoClosed", {
      set: function(f) {
        // If a callback is being set,
        if (typeof f === "function") {
          onVideoClosed = f;
        } else {
          onVideoClosed = null;
        }
      },
      get: function() {
        return onVideoClosed;
      }
    });

    setProperty(this, "onOfferwallCredited", {
      set: function(f) {
        // If a callback is being set,
        if (typeof f === "function") {
          onOfferwallCredited = f;
        } else {
          onOfferwallCredited = null;
        }
      },
      get: function() {
        return onOfferwallCredited;
      }
    });

    NATIVE.events.registerHandler("SupersonicAdDismissed", function() {
      logger.log("{supersonic} ad dismissed ");
      if (typeof onAdDismissed === "function") {
        onAdDismissed();
      }
    });

    NATIVE.events.registerHandler("SupersonicAdAvailable", function() {
      logger.log("{supersonic} ad available");
      if (typeof onAdAvailable === "function") {
        onAdAvailable("supersonic");
      }
    });

    NATIVE.events.registerHandler("SupersonicAdNotAvailable", function() {
      logger.log("{supersonic} ad not available");
      if (typeof onAdNotAvailable === "function") {
        onAdNotAvailable();
      }
    });

    function onRWAvailabilityChange(is_rv_connected) {
      var available = is_rv_connected && is_online;

      if (available != is_rv_available) {
        is_rv_available = available;

        if (is_rv_available) {
          logger.log("Rewarded Video is now available");
        } else {
          logger.log("Rewarded Video is now unavailable");
        }
      }
    };

    NATIVE.events.registerHandler('supersonicRVAdClosed', function(evt) {
      this.onVideoClosed(rv_source, evt.placement, evt.rewardedCount);
      rv_source = null;
    });

    NATIVE.events.registerHandler('supersonicOnRVAvailabilityChange', function(evt) {
      onRWAvailabilityChange(evt.available);
    });

    window.addEventListener("online", bind(this, function() {
      is_online = true;

      if(this.user_id) {
        this.initVideoAd(this.user_id);
      }
    }));

    window.addEventListener("offline", function() {
      is_online = false;

      onRWAvailabilityChange(is_rv_connected);
    });
  };

  this.showInterstitial = function() {
    NATIVE.plugins.sendEvent("SupersonicPlugin", "showInterstitial", JSON.stringify({}));
  };

  this.cacheInterstitial = function() {
    NATIVE.plugins.sendEvent("SupersonicPlugin", "cacheInterstitial", JSON.stringify({}));
  }

  this.isOWAdAvailable = function() {
    return is_ow_available === true;
  };

  this.isVideoAdAvailable = function() {
    return is_rv_available === true;
  };

  this.initInterstitial = function(user_id) {
    this.user_id = user_id;

    NATIVE.plugins.sendEvent("SupersonicPlugin", "initInterstitial", JSON.stringify({
      user_id: user_id
    }));
  };

  this.initVideoAd = function(user_id) {
    this.user_id = user_id;

    NATIVE.plugins.sendEvent("SupersonicPlugin", "initVideoAd", JSON.stringify({
      user_id: user_id
    }));
  };

  this.initOfferWallAd = function(user_id) {
    NATIVE.plugins.sendEvent("SupersonicPlugin", "initOfferWallAd", JSON.stringify({
      user_id: user_id
    }));
  };

  this.showVideoAd = function(placement_name) {
    rv_source = placement_name;

    NATIVE.plugins.sendEvent("SupersonicPlugin", "showRVAd", JSON.stringify({
      placementName: placement_name
    }));
  };

  this.showOWAd = function(userid) {
    NATIVE.plugins.sendEvent("SupersonicPlugin", "showOffersForUserID", JSON.stringify({
      userID: userid
    }));
  };
});

exports = new Supersonic();
