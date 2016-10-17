# Game Closure DevKit Module: Supersonic / ironSource


This module allows you to display interstitial, video ads and offerwalls
from the Supersonic mediation service.

Both iOS and Android targets are supported.

## Installation

Install the module into your application using the standard
devkit install process.

~~~
devkit install https://github.com/hashcube/supersonic
~~~

## Setup

Setup supersonic by configuring the supersonicApp key in manifest.json.
The supersonicAppKey can be fetched from the Supersonic/ironsource dashboard

#### Manifest.json
~~~
  "android": {
    "supersonicAppKey": "key",
  },
~~~

~~~
  "ios": {
    "supersonicAppKey": "key",
  },
~~~

You must add all of the providers you wish to bundle with your application
to the `addons.supersonic.<PLATFORM>.providers` lists or they will not be
included in the build and you will not be able to use them in your game.
~~~
"addons": {
    "supersonic": {
        "android": {
            "providers": [
                "chartboost",
                "facebook",
                "vungle"
            ]
        },
        "ios": {
            "providers": [
                "adcolony",
                "applovin",
                "facebook"
            ]
        }
    }
}
~~~

See the `Pre-Integrated Providers` section below for a list of working
providers you can add to these list.

## Pre-Integrated Providers

The plugin comes with several ad providers already
available in order to make using the plugin as easy as possible.

Adding one of the included providers is usually as easy as adding
the provider name to the manifest `addons.supersonic.[platform].providers`
list and adding any necessary SDK keys. The supersonic module will
use the providers list to automatically create all of your configuration

To use any of these, in the game please add it the `addons` block in manifest.json
as mentioned before.

## Integrating Additional Providers

To integrate additional providers supportd by supersonic, you'll need to
manually add all of the files and configuration to the correct locations in
the build/providers/<provider_name> folder.

EXTREMELY IMPORTANT - the auto-configuration being done for providers means
the standard android and ios folders are deleted before build every time and
created as necessary. DO NOT PUT FILES YOU NEED IN THESE FOLDERS (or check out
a previous version of the plugin).

You will likely need a combination of config.json, manifest.xml, manifest.xsl,
and various file changes to create a working integration. Follow the existing
integrations as a guide.

## NOTES on admob for android

The annotations was removed from the admob adapter to solve multiple references to Landroid/support/AnimRes
