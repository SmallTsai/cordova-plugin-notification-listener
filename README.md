Android notification listener Cordova plugin
================================

Installation
--------

```bash
cordova plugin add https://github.com/SmallTsai/cordova-plugin-notification-listener.git
```

Supported Platforms
--------

- Android

Usage
--------

### API

```js
var notificationListener = cordova.plugins.NotificationListener;
notificationListener.hasPermission(successCallback, errorCallback);
notificationListener.requestPermission(successCallback, errorCallback);
notificationListener.isRunning(successCallback, errorCallback);
notificationListener.toggle(successCallback, errorCallback);
notificationListener.addListener(successCallback, errorCallback);
```

### Examples

```js
var notificationListener = cordova.plugins.NotificationListener;
```

#### hasPermission

To check wheither your app has notification listener access permission or not.

```js
notificationListener.hasPermission(function(status) {
    console.log(status.hasPermission);
}, function() {
    console.error("Error calling Plugin");
});
```

#### requestPermission

To request permission and it will show a comfirm dialog to redirect permission access UI.

```js
notificationListener.requestPermission(function() {
    console.log("requestPermission done");
}, function() {
    console.error("Error calling Plugin");
});
```

Or short

```js
notificationListener.requestPermission();
```

#### isRunning

To check our notification listener service is still running.

```js
notificationListener.isRunning(function(status) {
    console.log(status.isRunning);
}, function() {
    console.error("Error calling Plugin");
});
```

#### toggle

To toggle the permission of notification listener. 
Sometime, the notification listener service did run on background, even you reopen the app.Once the service is not working. you can call this action to toggle the permission. It will take few seconds to start up service, depend on different phone.

```js
notificationListener.toggle(function() {
    console.log("toggle done");
}, function() {
    console.error("Error calling Plugin");
});
```

Or short

```js
notificationListener.toggle();
```

#### addListener

Register a notification listener for your app. you can add your business logic in callback function.

```js
notificationListener.addListener(function(data) {
    console.log(data);
}, function() {
    console.error("Error calling Plugin");
});
```

License
--------
The MIT License (MIT)



