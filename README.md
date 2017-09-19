# `cordova-plugin-android-downloadmanager`

[![Build Status](https://travis-ci.org/emilbayescordova-plugin-android-downloadmanager/.svg?branch=master)](https://travis-ci.org/emilbayescordova-plugin-android-downloadmanager/)

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
  destinationUri: ''
}
```

### `downloadManager.query([filter], cb)`

`cb(err, downloadsArray)`

```
{
  ids: ["1"],
  status: 0
}
```

### `downloadManager.remove(ids, [cb])`

`ids` should be an array of string id's. `cb(err, removedCount)`



## Install

```sh
npm install cordova-plugin-android-downloadmanager
```

## License

[ISC](LICENSE)
