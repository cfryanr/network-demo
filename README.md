## About this App

Upon startup, this app immediately attempts to open outgoing HTTP connections to CAPI and to example.com.

See `src/main/java/com/example/demo/DemoApplication.java` for implementation.

This app exists to demonstrate that these outgoing connections do not work reliably on
[cf-for-k8s v0.1.0](https://github.com/cloudfoundry/cf-for-k8s/releases/tag/v0.1.0).

## Steps to Reproduce

1. Clone this repo.
   We'll assume that you cloned it to `/Users/pivotal/workspace/network-demo` for the rest of this doc.

1. Compile the app. Don't forget to repeat this step each time you change the code while debugging.

   ```
   cd /Users/pivotal/workspace/network-demo
   ./mvnw install
   ```

1. When pushing this app to cf-for-k8s, both outgoing connections fail
   16 out of 20 times during app startup using the following loop:

   ```
   for i in {1..20}; do echo "Running attempt #$i"; cf delete -f network-demo; cf push network-demo -p /Users/pivotal/workspace/network-demo/target/demo-0.0.1-SNAPSHOT.jar; kubectl logs $(kubectl get pods -n cf-workloads | grep network-demo | cut -d' ' -f1) -n cf-workloads -c opi; done
   ```

   Outgoing connections to CAPI and to example.com will either both fail or both succeed.

   When they fail, the exception for each outgoing connection is:
   ```
   java.net.ConnectException: Connection refused (Connection refused)
       at java.base/java.net.PlainSocketImpl.socketConnect(Native Method)
       at java.base/java.net.AbstractPlainSocketImpl.doConnect(Unknown Source)
       at java.base/java.net.AbstractPlainSocketImpl.connectToAddress(Unknown Source)
       at java.base/java.net.AbstractPlainSocketImpl.connect(Unknown Source)
       at java.base/java.net.SocksSocketImpl.connect(Unknown Source)
       at java.base/java.net.Socket.connect(Unknown Source)
       at java.base/sun.security.ssl.SSLSocketImpl.connect(Unknown Source)
       at java.base/sun.security.ssl.SSLSocketImpl.<init>(Unknown Source)
       at java.base/sun.security.ssl.SSLSocketFactoryImpl.createSocket(Unknown Source)
       at com.example.demo.DemoApplication.connectTo(DemoApplication.java:32)
       at com.example.demo.DemoApplication.main(DemoApplication.java:18)
   ```

1. Note that sleeping for 2 seconds upon app startup before trying to open the sockets
   causes the outgoing connections to be successful more reliably, e.g. did not fail
   in 20 attempts using the same `for` loop command as above.

## Desired Behavior

These outgoing HTTP requests should not always fail for the first few seconds of the app's run.
