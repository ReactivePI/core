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

package nl.fizzylogic.reactivepi.gpio

import java.io.{File, IOException, PrintWriter}

abstract class GPIOPin(pinNumber: Int, direction: String) {
  val pinDirectionFilePath = s"/sys/class/gpio/gpio${pinNumber}/direction"
  val pinRegistrationFilePath = "/sys/class/gpio/export"
  val pinUnregistrationFilePath = "/sys/class/gpio/unexport"
  val pinValueFilePath = s"/sys/class/gpio/gpio${pinNumber}/value"

  try {
    val pinRegistrationWriter = new PrintWriter(new File(pinRegistrationFilePath))
    val pinModeWriter = new PrintWriter(new File(pinDirectionFilePath))

    // Write the pin number to /sys/class/gpio/export to make the pin active.
    pinRegistrationWriter.write(pinNumber.toString())
    pinRegistrationWriter.flush()
    pinRegistrationWriter.close()

    // Write the word 'out/in' to /sys/class/gpio/gpio{pinnumber}/direction to make it an output or input pin.
    pinModeWriter.write(direction)
    pinRegistrationWriter.flush()
    pinModeWriter.close()
  } catch {
    case e: IOException => throw new GPIOException(
      s"Failed to configure pin ${pinNumber}: ${e.getMessage()}")
  }

  /**
   * Closes access to the output pin
   */
  def close(): Unit = {
    try {
      val pinRegistrationWriter = new PrintWriter(new File(pinUnregistrationFilePath))

      // Write the number of the pin to /sys/class/gpio/unexport to close access to it
      pinRegistrationWriter.write(pinNumber.toString())
      pinRegistrationWriter.flush()
      pinRegistrationWriter.close()
    } catch {
      case e:IOException => throw new GPIOException(
        s"Failed to close access to pin ${pinNumber}: ${e.getMessage()}")
    }
  }
}
