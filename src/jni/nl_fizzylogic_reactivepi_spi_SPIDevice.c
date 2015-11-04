#include <jni.h>
#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <linux/spi/spidev.h>

#import "nl_fizzylogic_reactivepi_spi_SPIDevice.h"

JNIEXPORT jint JNICALL Java_nl_fizzylogic_reactivepi_spi_SPIDevice_initializeDevice
    (JNIEnv * env, jobject instance, jstring devicePath, jint speed, jint mode) {
    // To make things simple, use 8 bits per word.
    // In a future version of the device this may change to a setting.
    // The reason: Some devices use 12 bits or 16 bits per unit sent/received.
    int bitsPerWord = 8;

    char path[256];
    int stringLength = (*env)->GetStringLength(devicePath);
    (*env)->GetStringUTFRegion(env,path,0,length, devicePath);

    int handle = open(path, O_RDWR);

    // Check if the file handle is valid. Return a negative device handle
    // if it is below zero. Below zero in this case means that things are broken.
    if(handle < 0) {
        return -1000;
    }

    // Set the device operation mode, this determines the clock and sampling mode
    // that is used over SPI. This is specific to the device connected to the channel.
    // Please check the datasheet to find out how and what you need to use here.
    if(ioctl(handle, SPI_IOC_WR_MODE, &mode) < 0) {
        return -1001;
    }

    // Set the bits per word. This controls how many bits per word you can send or receive.
    // Setting this higher then 8 results in more bytes being used to build a single word.
    if(ioctl(handle, SPI_IOC_WR_BITS_PER_WORD, &bitsPerWord) < 0) {
        return -1002;
    }

    // Set the clock speed for the SPI. This controls how fast the data is transferred to the device.
    if(ioctl(handle, SPI_IOC_WR_MAX_SPEED_HZ, &speed) < 0) {
        return -1003;
    }

    return handle;
}

JNIEXPORT jbyteArray JNICALL Java_nl_fizzylogic_reactivepi_spi_SPIDevice_transferData
  (JNIEnv * env, jobject instance, jint handle, jint channel, jint speed, jbyteArray data, jint length) {

    spi_ioc_transfer spi;

    // Clear the memory of the struct, this is described in the header
    // which you can see at: http://lxr.free-electrons.com/source/include/uapi/linux/spi/spidev.h
    memset(&spi, 0, sizeof(spi));

    // Load the buffered data from the Java environment.
    // At the same time, initialize an empty buffer for receiving data.
    jbyte *txBuffer = (*env)->GetByteArrayElements(env, data, 0);
    unsigned char rxBuffer[length];

    // Build a transfer structure for the message to send across SPI.
    // First the TX buffer is transferred to the SPI connected device.
    // Next the RX buffer is filled with data received from the SPI connected device.
    spi.tx_buf = (unsigned long)txBuffer;
    spi.rx_buf = (unsigned long)rxBuffer;
    spi.len = length;
    spi.speed_hz = speed;
    spi.delay_usecs = 0;
    spi.bits_per_word = 8;

    // Send the message (notice: just one message, you could queue up multiple here).
    int result = ioctl(handle, SPI_IOC_MESSAGE(1), spi);

    //TODO: Improve this, by raising an exception instead of just returning an empty buffer.
    if(result < 0) {
        return null;
    }

    // Convert the received data into a Java array and return that to the application.
    // This copies memory, so we can clean up the original buffer before leaving this memory.
    jbyteArray returnValue = (*env)->NewByteArray(length);
    (*env)->SetByteArrayRegion(returnValue, 0, length, buffer);

    // Clear the TX buffer through JNI.
    // This ensures that the Java bits don't leak memory.
    (*env)->releaseByteArrayElements(env, data, txBuffer, 0);

    return returnValue;
}

