# `cordova-plugin-android-downloadmanager`

[![Build Status](https://travis-ci.org/emilbayes/cordova-plugin-android-downloadmanager.svg?branch=master)](https://travis-ci.org/emilbayes/cordova-plugin-android-downloadmanager)

> Plugin to expose Android's DownloadManager in Javascript

## Usage

```js
var downloadManager = require('cordova-plugin-android-downloadmanager.DownloadManager')
```

## API

Please refer to [`DownloadManager`](https://developer.android.com/reference/android/app/DownloadManager.html)
for parameter use.

### `downloadManager.enqueue(req, [cb])`

`cb(err, idString)`

```
{
  uri: '',
  title: '',
  description: '',
  mimeType: '',

  visibleInDownloadsUi: true,
  notificationVisibility: 0,

  // Either of the next three properties
  destinationInExternalFilesDir: {
    dirType: '',
    subPath: ''
  },
  destinationInExternalPublicDir: {
    dirType: '',
    subPath: ''
  },
  destinationUri: '',
  headers: [{header: 'Authorization', value: 'Bearer iaksjfd89aklfdlkasdjf'}]
}
```

### `downloadManager.query([filter], cb)`

You can query the SQLite database that backs DownloadManager, but the native Android `query` method only supports two filters:

```
{
  ids: ["1"],
  status: 0
}
```

This will invoke the callback of the signature `cb(err, entryArray)` with each `entry` being objects of the form:

```js
{
  "id": String,
  "title": String,
  "description": String,
  "mediaType": String,
  "localFilename": String,
  "localUri": String,
  "mediaproviderUri": String,
  "uri": String,
  "lastModifiedTimestamp": Number,
  "status": Number,
  "reason": Number,
  "bytesDownloadedSoFar": Number,
  "totalSizeBytes": Number
}
```

### `downloadManager.remove(ids, [cb])`

`ids` should be an array of string id's. `cb(err, removedCount)`

### `downloadManager.addCompletedDownload(req, [cb])`

`cb(err, idString)`

```
{
  title: '',
  description: '',
  isMediaScannerScannable: false,
  mimeType: '',
  path: '',
  length: 0,
  showNotification: true
}
```
Note: Don't include any `file://` prefix in the path.

## Install

```sh
npm install cordova-plugin-android-downloadmanager
```

## License

[ISC](LICENSE)
