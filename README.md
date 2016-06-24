# Lifecycle
A binder which let you manage async operations against Android components lifecycle (i.e. Activity of Fragment rotation). 

* Seamless execution call-rotation-response
* Easily integration with third-party library
* No crashes after Activity/Fragment rotation 

As Android developer, you often have to face the problem of managing lifecycles of differents components. The most common case is when a third-party library executes a long running operation off the UI-Thread and the user rotates the device causing your Activity or Fragment to be destroyed and recreated. This situation causes the third-party response to be lost because the caller referes to the destroyed Activity of Fragment. Using Lifecycle avoids to lose the control over the long running operation and offers to your app a seamless execution over Android components lifecycles.

```java

...
   /**
    * The third-party library doing async operation on worker thread
    */
    ThirdPartyWorkerThread workerThread;
   
    /**
     * Standard task execution without Lifecycle hook
     */
    private void executeTaskWithoutLifecycle() {
        workerThread.execute(new ThirdPartyCallback() {
        
            /**
             * Callback used for receiving async operation's result
             */
            public void onResponse(Object o) {
                ...
            }
        });
    }
    
    /**
     * Standard task execution without Lifecycle hook
     */
    private void executeTaskWithLifecycle() {
       workerThread.execute(Lifecycle.hook(this, new ThirdPartyCallback() {
            
            /**
             * Callback used for receiving async operation's result
             */
            public void onResponse(Object o) {
                ...
            }
        }));
    }
...

/**
 * Activity example
 */
class ExampleActivity extends Activity {
    
    /**
     * Bind Lifecycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Lifecycle.bind(this);
    }
    
    /**
     * Example. Running df.show(...) afetr Activity's onSaveInstanceState causes IllegaleStateException to be thrown.
     * Using DONT_DELIVER_ON_SAVED_INSTANCE execution mode avoids it. The execution of method doSomethindA is enqueued until
     * the Activity marks its instance state as restored.
     */
    public void runExample(View view) {
        ThirdPartyLibrary runnable = new ThirdPartyLibrary(Lifecycle.hook(this, new ThirdPartyCallback() {
            @Override
            public void doFoo() {
                ((TextView) findViewById(R.id.textView1)).setText(...);
                
                DialogFragment df = DialogFragment.newInstance();
                df.show(getFragmentManager(), TAG);
            }

            @Override
            public void doBar(String s) {
                ((TextView) findViewById(R.id.textView2)).setText(...);
            }

        }, new Handler(), Lifecycle.Delivery.DONT_DELIVER_ON_SAVED_INSTANCE));

        new Thread(runnable).start();
    }

    /**
     * Mark instance state as restored, if delivery execution mode is DONT_DELIVER_ON_SAVED_INSTANCE
     */
    @Override
    protected void onStart() {
        super.onStart();
        Lifecycle.restoredInstanceState(this);
    }

    /**
     * Mark instance state as saved, if delivery execution mode is DONT_DELIVER_ON_SAVED_INSTANCE
     */
    @Override
    protected void onStop() {
        super.onStop();
        Lifecycle.savedInstanceState(this);
    }

    /**
     * Unbind Lifecycle
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Lifecycle.unbind(this);
    }
}
```

Download
--------

Gradle (jCenter):
```groovy
compile 'com.martellux:lifecycle:0.2.0'
```

License
-------

    Copyright 2016 Alessandro Martellucci

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: https://search.maven.org/remote_content?g=com.martellux&a=lifecycle&v=LATEST
