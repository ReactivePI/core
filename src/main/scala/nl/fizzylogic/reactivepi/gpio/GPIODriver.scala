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

/**
 * Provides access to the GPIO expansion header on the raspberry PI
 */
object GPIODriver {
  /**
   * Registers a pin as input
   * @param pinNumber Pin number to register
   * @return  Pin input driver
   */
  def input(pinNumber: Int): InputPin = {
    new InputPin(pinNumber)
  }

  /**
   * Registers a pin as output
   * @param pinNumber Pin number to register
   * @return  Pin output driver
   */
  def output(pinNumber: Int): OutputPin = {
    new OutputPin(pinNumber)
  }
}
