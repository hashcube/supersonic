import device;
import event.Emitter as Emitter;
import util.setProperty as setProperty;

var isOnline = navigator.onLine;

var isRVConnected = false;

var RVsource = null;
/*
 * Flag for RewardVideo init status
 */
var isRVAvailable = false;

/*
 * Flag for OfferWall init status
 */
var isOWAvailable = false;

var Supersonic = Class(Emitter, function (supr) {
	this.init = function() {
		supr(this, 'init', arguments);

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
	};

	this.isOWAdAvailable = function() {
		return isOWAvailable == true;
	};

	this.isRVAdAvailable = function() {
		return isRVAvailable == true;
	};

	this.showRVAd = function(placementName) {
        RVsource = placementName;

		NATIVE.plugins.sendEvent("SupersonicPlugin", "showRVAd", JSON.stringify({
			placementName: placementName
		}));
	};

	this.showOWAd = function(userid) {
		NATIVE.plugins.sendEvent("SupersonicPlugin", "showOffersForUserID", JSON.stringify({
            userID: userid
        }));
    };
});

var supersonic = new Supersonic;

function onRWAvailabilityChange(isRVConnected) {
	logger.log("{supersonic} video availability callback js");
	var available = isRVConnected && isOnline;

	if (available != isRVAvailable) {
		isRVAvailable = available;

		if (isRVAvailable) {
			logger.log("Rewarded Video is now available");
		} else {
			logger.log("Rewarded Video is now unavailable");
		}
	}
};

NATIVE.events.registerHandler('supersonicRVAdClosed', function(evt) {
	supersonic.onVideoClosed(RVsource, evt.placement);
	RVsource = null;
});

NATIVE.events.registerHandler('supersonicOnRVAvailabilityChange', function(evt) {
	logger.log(evt);
	onRWAvailabilityChange(evt.available);
});

window.addEventListener("online", function() {
	isOnline = true;

	onRWAvailabilityChange(isRVConnected);
});

window.addEventListener("offline", function() {
	isOnline = false;

	onRWAvailabilityChange(isRVConnected);
});



exports = supersonic;