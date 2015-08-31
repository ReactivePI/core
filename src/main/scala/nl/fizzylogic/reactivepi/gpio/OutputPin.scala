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

import java.io._

/**
 * Use the output pin class to configure a pin on the
 * GPIO connector of your raspberry PI as output
 * @param pinNumber Pin number to use
 */
class OutputPin(pinNumber: Int) extends GPIOPin(pinNumber, "out") {
  /**
   * Sets the output pin to a low state
   */
  def low(): Unit = {
    try {
      val pinValueWriter = new PrintWriter(new File(pinValueFilePath))

      pinValueWriter.write("0")
      pinValueWriter.close()
    } catch {
      case e:IOException => throw new GPIOException(
        s"Failed to set the pin state for pin ${pinNumber}: ${e.getMessage()}")
    }
  }

  /**
   * Sets the output pin to a high state
   */
  def high(): Unit = {
    try {
      val pinValueWriter = new PrintWriter(new File(pinValueFilePath))

      pinValueWriter.write("1")
      pinValueWriter.close()
    } catch {
      case e:IOException => throw new GPIOException(
        s"Failed to set the pin state for pin ${pinNumber}: ${e.getMessage()}")
    }
  }
}
