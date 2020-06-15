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

```js
notificationListener.hasPermission(function(status) {
    console.log(status.hasPermission);
}, function() {
    console.error("Error calling Plugin");
});
```

#### requestPermission

```js
notificationListener.requestPermission(function() {
    console.log("requestPermission done");
}, function() {
    console.error("Error calling Plugin");
});
```

#### isRunning

```js
notificationListener.isRunning(function(status) {
    console.log(status.isRunning);
}, function() {
    console.error("Error calling Plugin");
});
```

#### toggle

```js
notificationListener.toggle(function() {
    console.log("toggle done");
}, function() {
    console.error("Error calling Plugin");
});
```

#### addListener

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



