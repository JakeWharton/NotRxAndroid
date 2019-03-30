RxBinding
=========

RxJava binding APIs for Android UI widgets from the platform and support libraries.

Getting Started
---------------
**Observing Clicks**
```
    RxView.clicks(button)
        .debounce(300, TimeUnit.MILLISECONDS)
        .subscribe();
```

**Observing an EditText**
```
    RxTextView.textChanges(editText)
        .debounce(300, TimeUnit.MILLISECONDS)
        .subscribe();
```
For Kotlin, these bindings are implemented using extension functions,
so just call .clicks()/.textChanges() on your View type.

Download
--------

Platform bindings:
```groovy
implementation 'com.jakewharton.rxbinding3:rxbinding:3.0.0-alpha2'
```

AndroidX library bindings:
```groovy
implementation 'com.jakewharton.rxbinding3:rxbinding-core:3.0.0-alpha2'
implementation 'com.jakewharton.rxbinding3:rxbinding-appcompat:3.0.0-alpha2'
implementation 'com.jakewharton.rxbinding3:rxbinding-drawerlayout:3.0.0-alpha2'
implementation 'com.jakewharton.rxbinding3:rxbinding-leanback:3.0.0-alpha2'
implementation 'com.jakewharton.rxbinding3:rxbinding-recyclerview:3.0.0-alpha2'
implementation 'com.jakewharton.rxbinding3:rxbinding-slidingpanelayout:3.0.0-alpha2'
implementation 'com.jakewharton.rxbinding3:rxbinding-swiperefreshlayout:3.0.0-alpha2'
implementation 'com.jakewharton.rxbinding3:rxbinding-viewpager:3.0.0-alpha2'
```

Google 'material' library bindings:
```groovy
implementation 'com.jakewharton.rxbinding3:rxbinding-material:3.0.0-alpha2'
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].


License
-------

    Copyright (C) 2015 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.





 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
