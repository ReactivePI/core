//  Copyright 2015 Willem Meints
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package nl.fizzylogic.reactivepi.i2c;

import nl.fizzylogic.reactivepi.DriverInitializationException;
import nl.fizzylogic.reactivepi.NativeUtils;

import java.io.IOException;

/**
 * Allows communication with a I2C device connected
 */
public class I2CDevice {
    private int busHandle;
    private int deviceAddress;

    static {
        try {
            NativeUtils.loadLibraryFromJar("/reactivepi.so");
        } catch (IOException e) {
            throw new DriverInitializationException("Failed to load native driver. ");
        }
    }

    /**
     * Initializes a new instance of I2CDevice
     *
     * @param devicePath    Path to the device file
     * @param deviceAddress Address of the device on the bus
     */
    private I2CDevice(String devicePath, int deviceAddress) {
        this.deviceAddress = deviceAddress;
        this.busHandle = initializeDevice(devicePath);
    }

    /**
     * Opens access to a device registered under the specified address on the specified bus
     *
     * @param bus     I2C bus to access
     * @param address Address of the device on the bus
     * @return Returns the newly initialized I2C device
     */
    public static I2CDevice open(int bus, int address) {
        return new I2CDevice("/dev/i2c-" + bus, address);
    }

    /**
     * Closes the handle to the device
     */
    public void close() {
        closeDevice(busHandle);
    }

    /**
     * Reads a number of bytes from the device
     *
     * @param register Register on the device to read from
     * @param length   Number of bytes to read
     * @return The data buffer containing the read bytes
     */
    public byte[] read(int register, int length) {
        byte[] buffer = new byte[length];

        deviceRead(busHandle, deviceAddress, register, buffer, 0, length);

        return buffer;
    }

    /**
     * Reads a number of bytes from the device
     *
     * @param length Number of bytes to read
     * @return The data buffer containing the read bytes
     */
    public byte[] read(int length) {
        byte[] buffer = new byte[length];

        deviceReadDirect(busHandle, deviceAddress, buffer, 0, length);

        return buffer;
    }

    /**
     * Writes a number of bytes to the device
     *
     * @param register Register to write to
     * @param data     Data to write
     */
    public void write(int register, byte[] data) {
        deviceWrite(busHandle, deviceAddress, register, data, 0, data.length);
    }

    /**
     * Writes a number of bytes to the device
     * @param data  Data to write
     */
    public void write(byte[] data) {
        deviceWriteDirect(busHandle,deviceAddress,data,0,data.length);
    }

    /**
     * Initializes the native interface of the device
     *
     * @param devicePath The path to the device file to use
     * @return The native bus handle
     */
    private native int initializeDevice(String devicePath);

    /**
     * Closes the internal bus handle
     *
     * @param handle
     */
    private native void closeDevice(int handle);

    /**
     * Writes a number of bytes a device on to the I2C bus
     *
     * @param driverHandle  Internal bus handle to use
     * @param deviceAddress Address of the device on the bus
     * @param data          Data to write
     * @param offset        Offset of the data to write
     * @param length        Number of bytes to write
     * @return The result code of the native operation
     */
    private native int deviceWriteDirect(int driverHandle, int deviceAddress, byte[] data, int offset, int length);

    /**
     * Writes a number of bytes a device on to the I2C bus
     *
     * @param driverHandle  Internal bus handle to use
     * @param deviceAddress Address of the device on the bus
     * @param register      The register to write to
     * @param data          Data buffer to write
     * @param offset        Offset of the data to write
     * @param length        Number of bytes to write
     * @return The result code of the native operation
     */
    private native int deviceWrite(int driverHandle, int deviceAddress, int register, byte[] data, int offset, int length);

    /**
     * Reads a number of bytes from the I2C bus
     *
     * @param driverHandle Internal bus handle to use
     * @param data         Data to read
     * @param offset       Offset in the buffer
     * @param length       Number of bytes to read
     * @return The result code of the native operation
     */
    private native int deviceReadDirect(int driverHandle, int deviceAddress, byte[] data, int offset, int length);

    /**
     * Reads a number of bytes from the I2C bus
     *
     * @param driverHandle Internal bus handle to use
     * @param data         Data to read
     * @param register     Register to read from
     * @param offset       Offset in the buffer
     * @param length       Number of bytes to read
     * @return The result code of the native operation
     */
    private native int deviceRead(int driverHandle, int deviceAddress, int register, byte[] data, int offset, int length);
}
