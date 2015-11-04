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

/**
 * Specifies the various settings for the clock and data sampling
 * that the SPI interface needs to use for the connected device.
 *
 * The first setting is CPOL (Clock polarity) which controls whether the
 * clock signal starts high and drops every tick or that it starts low
 * and rises each tick. Setting this to 1 makes the clock start high.
 *
 * The second setting is the CPHA which controls whether data is sampled
 * on the leading edge or the trailing edge of the clock signal.
 *
 * These two settings are combined into four modes of operation here.
 * However, when you check the datasheet of the device you want to work
 * with you will notice that it doesn't tell you which mode to use.
 * It tells you what the CPOL and CHPA settings are through a timing diagram.
 *
 * When you want to use the SPI device, please make sure that you check the timing
 * diagram of the device to find the correct mode.
 */
public enum DeviceMode {
    /**
     * Clock starts low and the data is sampled on the leading edge.
     */
    DEVICE_MODE_0(0),

    /**
     * Clock starts low and the data is sampled on the trailing edge
     */
    DEVICE_MODE_1(1),

    /**
     * The clock starts high and the data is sampled on the leading edge
     */
    DEVICE_MODE_2(2),

    /**
     * The clock starts high and the data is sampled on the trailing edge
     */
    DEVICE_MODE_3(3);

    private int value;

    /**
     * Initializes the device mode with the correct value
     * @param value
     */
    private DeviceMode(int value) {
        this.value = value;
    }

    /**
     * Gets the device mode value
     * @return  Returns 0, 1, 2 or 3 depending on the value for the device mode.
     */
    public int getValue() {
        return value;
    }
}
