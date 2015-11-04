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

package nl.fizzylogic.reactivepi.spi;

import nl.fizzylogic.reactivepi.DriverInitializationException;
import nl.fizzylogic.reactivepi.NativeUtils;

import java.io.IOException;

/**
 * Enables data transfer over SPI on the Raspberry PI.
 */
public class SPIDevice {
    private final int deviceHandle;
    private final int channel;
    private final int speed;

    static {
        try {
            NativeUtils.loadLibraryFromJar("/reactivepi.so");
        } catch (IOException e) {
            throw new DriverInitializationException("Failed to load native driver. ");
        }
    }

    /**
     * Initializes a new instance of SPIDevice
     *
     * @param channel The channel to communicate across
     * @param speed   The speed on the SPI bus to use
     * @param mode    The device mode to use
     */
    private SPIDevice(int channel, int speed, DeviceMode mode) {
        this.speed = speed;
        this.channel = channel;

        String devicePath = String.format("/dev/spi0.%s", channel);
        deviceHandle = initializeDevice(devicePath, speed, mode.getValue());

        // There could be a number of problems with the driver initialization,
        // but right now I'm just going to tell you something broke.. badly.
        //TODO: Will need to improve on this later.
        if (deviceHandle < 0) {
            throw new DriverInitializationException(
                    "Failed to initialize driver");
        }
    }

    /**
     * Opens the SPI device setting the device mode to any of the suppported
     * device modes. Please check the DeviceMode enumeration for available
     * device modes.
     *
     * @param channel Channel to use for communication
     * @param speed   The speed at which the clock should run
     * @return Returns the device
     */
    public static SPIDevice open(int channel, int speed, DeviceMode mode) {
        if (channel < 0 || channel > 1) {
            throw new IllegalArgumentException("Please specify a channel between 0 and 1");
        }

        if (speed <= 0) {
            throw new IllegalArgumentException("Invalid speed specified");
        }

        return new SPIDevice(channel, speed, mode);
    }

    /**
     * Transfers data over SPI to the connected device
     * reading back data that is available on the device
     *
     * @param data Data to write
     * @return Data read from the device
     */
    public byte[] transfer(byte[] data) {
        return transferData(channel, speed, data, data.length);
    }

    /**
     * Initializes the device
     *
     * @param path Path to the device driver
     * @return The driver handle
     */
    private native int initializeDevice(String path, int speed, int mode);

    /**
     * Transfers data over SPI
     *
     * @param channel Channel to communicate across
     * @param data    Data to send
     * @param length  Length of the data to send
     * @return The data that was transferred back
     */
    private native byte[] transferData(int channel, int speed, byte[] data, int length);
}
