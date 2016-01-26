# Lifecycle
A binder which let you manage async operations against Android components lifecycle (i.e. Activity of Fragment rotation). 

* Seamless execution call-rotation-response
* Easily integration with third-party library
* No crashes after Activity/Fragment rotation 

As Android developer, you often have to face the problem of managing lifecycles of differents components. The most common case is when a third-party library executes a long running operation off the UI-Thread and the user rotates the device causing your Activity or Fragment to be destroyed and recreated. This situation causes the third-party response to be lost because the caller referes to the destroyed Activity of Fragment. Using Lifecycle avoids to lose the control over the long running operation and offers to your app a seamless execution over Android components lifecycles.
