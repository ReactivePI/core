# Core library for Reactive PI

Reactive PI is a library based on akka and scala that allows developers to use the various I/O ports on their raspberry PI from Scala. 

## How to use this library
There are two methods to use this library. You can use the core components from this repository or you can use the components from the [actors](https://github.com/ReactivePI/actors) repository. The core components allow you to use just the sensors. This is suitable in cases where you're working locally in your PI and don't want the extra overhead of [Akka](https://akka.io).

Alternatively we offer integration with Akka so that you can turn your sensors into actors. This usage scenario is especially interesting if you want your PI to be part of a cluster and access the sensors from a different machine. 

## Quickstart
### Setting up dependencies
To use this library, add a reference to it in your `build.sbt` file:

``` sbt
libraryDependencies += "reactivepi" %% "core" % "0.2"
```

### Accessing GPIO
To access the GPIO pins on your Raspberry PI you need to create a new instance of the GPIODriver:

``` scala
val pin0 = GPIODriver.input(0)
val pin1 = GPIODriver.output(1)
```

Information about the exact pinout of the GPIO pins can be found here: https://www.raspberrypi.org/documentation/usage/gpio/README.md

**Please note:** Once a pin is configured as input or output you need to close it first, before you can reconfigure it. Also, make sure that you call `close` on the pin instances to unregister their mapping. If you fail to do this, you need to restart your Raspberry PI in order to free up the resources!

To read information from a GPIO pin, use the `read` method:

``` scala
val reading = pin0.read()
```

This returns a byte value for the pin. The value returned is either `0x1` or `0x0`. 

To write data to a GPIO output pin, use the `low` or `high` method:

``` scala
pin1.low()
pin1.high()
```

### Accessing I2C
A lot of sensors that you can use with the Raspberry PI are based in [I2C](https://en.wikipedia.org/wiki/I%C2%B2C), a bus system for electronics. We support the use of I2C through the `I2CDriver` class.

To use this class you need to open a connection to a device on the I2C bus of your Raspberry PI.
Every device on the I2C bus has a unique address and the Raspberry PI features 2 I2C buses of which the second is exposed on the circuitboard by default.

``` scala
val device = I2CDevice.open(1,0x18)
```

The `open` method accepts the index of the bus you want to connect to as the first argument and a second argument to specify the device address on the bus. Often you need to lookup the address of the device in the manual of the device itself. This is because many sensors have a fixed address.

Once you've opened a connection to the I2C bus you can start the communicate with your sensor.
For example, to read data from the device you can use the `read` method to get data from the device:


``` scala
val data = device.read(0, 2)
```

This call to the `read` method reads two bytes of data from register 0. Some device have registers, while others don't. So if you don't need to use registers, you can also call `read(2)` to just read two bytes of data from the device.

To write data to the device, you can use the `write` method:

``` scala
device.write(0, Array[Byte](0,255))
```

This writes two bytes of data to register 0 on the device. Alternative you can write two bytes of data to a device without any registers like so:

``` scala
device.write(Array[Byte](0,255))
```

## Development setup
### Compiling the native device code
This library uses a C library that contains logic to work with the I2C buses on your Raspberry PI. In order to compile this code you need to have `make` and the build essentials installed on a Raspberry PI. Please be aware that it is not possible to compile the native code on your regular computer.

First clone this repository to your Raspberry PI.
Then run the following steps to compile the native code:

 * `sbt nativeGenerateHeaders`
 * `make`
 
 Copy the file `target/native/reactivepi.so` to `resources/reactivepi.so` after you've compiled the code.
 This is required to include the native library in the final JAR.
 
 ### Compiling the Scala code
 To compile the scala code, first make sure `resources/reactivepi.so` exists. Then run these steps to compile and install ReactivePI locally:
 
  * `sbt package`
  * `sbt publishLocal`
