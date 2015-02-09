import util.setProperty as setProperty;

var Supersonic = Class(function () {

	this.init = function(opts) {

		setProperty(this, "onCreditRecieved", {
			set: function(f) {
				if (typeof f === "function") {
					onCreditRecieved = f;
				} else {
					onCreditRecieved = null;
				}
			},
			get: function() {
				return onCreditRecieved;
			}
		});

		NATIVE.events.registerHandler("onCreditRecieved", function() {
			logger.log("{Supersonic} credit recieved");
			if (typeof onCreditRecieved === "function") {
				oncreditRecieved();
			}
		});
	}

	this.showOffersForUserID = function(userid) {
		NATIVE.plugins.sendEvent("SupersonicPlugin", "showOffersForUserID", JSON.stringify({"userID":userid}));
	};
});

exports = new Supersonic();
