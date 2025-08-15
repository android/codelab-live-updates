# Codelab sample for Live updates

This codelab is for the Notifications [Live Updates](https://developer.android.com/develop/ui/views/notifications/live-update) feature.
Please see the guided codelab [here](https://developer.android.com/devsite/codelabs/notifications-rich-experience-live-updates#1) and the accompanying video [here](https://youtu.be/_Akf_u08p7U?si=Et8exUp1rehVZzQQ)

[Jetsnack][jetsnack] is a sample snack ordering app built with [Jetpack Compose][compose]. In this repository, the app has been extended for the purpose of simulating a checkout and order tracking experience with notifications. The starter_code branch uses standard notifications for the order
tracking experience and the main branch is the improved user experience that uses the ProgressStyle template and Live Update feature for enhanced visibility. Please see the document [Live Updates][live-updates] for more details on Live Updates.

To try out this sample app, use the latest stable version
of [Android Studio](https://developer.android.com/studio).

This sample showcases:

* How to use [ProgressStyle][progress_style] notification template
* Apply the [Live Update][live_updates] criteria for increased visibility
 
## Screenshots

<img src="screenshots/screenshot_1.png"/>
<img src="screenshots/screenshot_2.png"/>
<img src="screenshots/screenshot_2.png/>

## License

```
Copyright 2025 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
[live_updates]: https://developer.android.com/develop/ui/views/notifications/live-update
[progress_style]: https://developer.android.com/reference/android/app/Notification.ProgressStyle
[compose]: https://developer.android.com/jetpack/compose
[coil]: https://coil-kt.github.io/coil/
[jetsnack]: https://github.com/android/compose-samples/tree/main/Jetsnack
