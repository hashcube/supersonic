import util.setProperty as setProperty;

exports = new (Class(function () {

  this.init = function(opts) {

    setProperty(this, "onCreditReceived", {
      set: function(f) {
        onCreditReceived = typeof f === "function" ? f : null;
      },
      get: function() {
        return onCreditReceived;
      }
    });

    NATIVE.events.registerHandler("onCreditReceived", function(credit) {
      if (typeof onCreditReceived === "function") {
        onCreditReceived();
      }
    });
  }

  this.showOffersForUserID = function(userid) {
    NATIVE.plugins.sendEvent("SupersonicPlugin", "showOffersForUserID", JSON.stringify({"userID":userid}));
  };
}))();
