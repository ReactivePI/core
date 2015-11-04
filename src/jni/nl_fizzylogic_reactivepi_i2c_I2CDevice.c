#include <stdio.h>
#include <linux/i2c-dev.h>
#include <fcntl.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <jni.h>

#import "nl_fizzylogic_reactivepi_i2c_I2CDevice.h"

JNIEXPORT jint JNICALL Java_nl_fizzylogic_reactivepi_i2c_I2CDevice_initializeDevice
    (JNIEnv * env, jobject instance, jstring path)
{
    char filename[256];
    int length = (*env)->GetStringLength(env, path);
    (*env)->GetStringUTFRegion(env,path,0,length, filename);

    // Open the device and return the device handle.
    // If this device handle is negative, it means that the device failed to initialize.
    return open(filename, O_RDWR);
}

JNIEXPORT void JNICALL Java_nl_fizzylogic_reactivepi_i2c_I2CDevice_closeDevice
    (JNIEnv * env, jobject instance, jint handle)
{
    close(handle);
}

JNIEXPORT jint JNICALL Java_nl_fizzylogic_reactivepi_i2c_I2CDevice_deviceWriteDirect
    (JNIEnv * env, jobject instance, jint handle, jint deviceAddress, jbyteArray data, jint offset, jint length)
{
    int i;
    int response;
    unsigned char buffer[length];

    // Tell the I2C bus that we want to talk to a specific device
    // located on the specified address.
    response = ioctl(handle,I2C_SLAVE,deviceAddress);

    if(response < 0) {
        return response;
    }
    
    jbyte *body = (*env)->GetByteArrayElements(env,data,0);
    
    for(i = 0; i < length; i++) {
        buffer[i] = body[i+offset];
    }

    // Release memory in the JVM to enable the GC to collect it.
    // Not doing this results in a memory leak!
    (*env)->releaseByteArrayElements(env, data, body, 0);

    return write(handle,buffer,length);
}

JNIEXPORT jint JNICALL Java_nl_fizzylogic_reactivepi_i2c_I2CDevice_deviceWrite
    (JNIEnv *env, jobject instance, jint handle, jint deviceAddress, jint deviceRegister, jbyteArray data, jint offset, jint length)
{
    int i;
    int response;
    unsigned char buffer[length + 1];

    // Tell the I2C bus that we want to talk to a specific device
    // located on the specified address.
    response = ioctl(handle, I2C_SLAVE, deviceAddress);

    if(response < 0) {
        return response;
    }

    jbyte *body = (*env)->GetByteArrayElements(env, data, 0);

    buffer[0] = deviceRegister;

    for(i = 0; i < length; i++) {
        buffer[i + 1] = body[i+offset];
    }

    // Release memory in the JVM to enable the GC to collect it.
    // Not doing this results in a memory leak!
    (*env)->releaseByteArrayElements(env, data, body, 0);

    return write(handle,buffer,length+1);
}

JNIEXPORT jint JNICALL Java_nl_fizzylogic_reactivepi_i2c_I2CDevice_deviceReadDirect
    (JNIEnv *env, jobject instance, jint handle, jint deviceAddress, jbyteArray data, jint offset, jint length)
{
    int i;
    int response;
    unsigned char buffer[length];

    response = ioctl(handle, I2C_SLAVE, deviceAddress);

    if(response < 0) {
        return response;
    }

    response = read(handle, buffer, length);

    if(response < 0) {
        return response;
    }

    jbyte *body = (*env)->GetByteArrayElements(env, data, 0);

    for(i = 0; i < length; i++) {
        body[i + offset] = buffer[i];
    }

    // Release memory in the JVM to enable the GC to collect it.
    // Not doing this results in a memory leak!
    (*env)->ReleaseByteArrayElements(env, data, body, 0);

    return response;
}

JNIEXPORT jint JNICALL Java_nl_fizzylogic_reactivepi_i2c_I2CDevice_deviceRead
    (JNIEnv *env, jobject instance, jint handle, jint deviceAddress, jint deviceRegister, jbyteArray data, jint offset, jint length)
{
    int i;
    int response;
    unsigned char buffer[length];

    // Write registry byte to device first
    response = ioctl(handle, I2C_SLAVE, deviceAddress);

    if(response < 0) {
        return response;
    }

    buffer[0] = deviceRegister;
    response = write(handle, buffer, 1);

    if(response != 1) {
        return response;
    }

    // Then read the response
    ioctl(handle, I2C_SLAVE, deviceAddress);
    response = read(handle, buffer, length);

    jbyte *body = (*env)->GetByteArrayElements(env, data, 0);

    for(i = 0; i < length; i++) {
        body[i + offset] = buffer[i];
    }

    (*env)->ReleaseByteArrayElements(env, data, body, 0);

    return response;
}
