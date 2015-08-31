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
import java.nio.file.{Files, Paths}

/**
 * Use the input pin class to get access to the value of a GPIO pin
 */
class InputPin(pinNumber: Int) extends GPIOPin(pinNumber, "in") {
  /**
   * Reads the value of the GPIO pin
   * @return  1 when the pin state is high; Otherwise 0.
   */
  def read(): Byte = {
    try {
      val readBytes = Files.readAllBytes(Paths.get(pinValueFilePath))
      readBytes(0)
    } catch {
      case e:IOException => throw new GPIOException(
        s"Failed to close access to pin ${pinNumber}: ${e.getMessage()}")
    }
  }
}
