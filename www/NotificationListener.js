var SERVICE_NAME = 'NotificationListener';

function NotificationListener() {}

NotificationListener.prototype = {
    hasPermission: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, SERVICE_NAME, 'hasPermission', []);
    },
    requestPermission: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, SERVICE_NAME, 'requestPermission', []);
    },
    isRunning: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, SERVICE_NAME, 'isRunning', []);
    },
    toggle: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, SERVICE_NAME, 'toggle', []);
    },
    addListener: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, SERVICE_NAME, 'addListener', []);
    }
};

module.exports = new NotificationListener();