# tplink-cam-cli

Java CLI control application for TP-Link IP cameras, such as the [TP-Link NC250](http://www.tp-link.com/us/products/details/cat-5515_TL-NC250.html).

The primary purpose of this application is to enable remote control from smart home frameworks such as [openHAB](http://openhab.org). At the moment, it can switch the LED and motion/sound detection on and off, more to follow.

## Usage

`
java -jar tplink-cam-cli.jar camerahostname username password command parameter
`
* `hostname`: the IP / hostname for your TP-Link IP camera, e.g. `192.168.0.25`
* `username`: the user name of an administrator account, by default `admin`
* `password`: the cleartext (i.e. non-Base64 encoded) password of the account, by default `admin`

Possible value for `command` and `parameter` are:
* `led` `on` or `off`: to switch the frontside green LED on or off
* `motion` `on` or `off`: to enable or disable motion detection (make sure to configure associated settings and notification delivery first properly via the web UI)
* `sound` `on` or `off`: to enable or disable sound detection (make sure to configure settings and notification delivery first properly via the web UI)

## Development and build

The development environment and is Eclipse and m2e, use 'export as runnable JAR' from the project menu.

## Acknowledgements

This library was written by reverse engineering the web UI built into the camera. Additionally, https://github.com/reald/nc220 contains some useful information.
