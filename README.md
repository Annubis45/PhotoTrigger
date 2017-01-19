# PhotoTrigger

## Table of contents

* [Intro](#intro)
* [Usage](#usage)
* [BT Module](#module)
  * [Schematic](#module-schematics)
  * [Binary](#module-binary)
  * [Protocol](#protocol)
* [Android Application](#android)
* [Improvements](#improvements)
* [License](#license)


## Intro

Making a reflex camera controlable by an android application.
It controls the focus and the shutter of the camera.

## Usage

 1. Connect the module to the camera and put it on.
 2. Connect your phone to the bt module via BT 
   * name: PhotoTriggerBT 
   * pass: 1342
 3. Use the application to control the camera.


## BT Module

### Schematic
<img src="https://github.com/Annubis45/PhotoTrigger/blob/master/arduino/schematic.JPG" alt="Module Schematic" width="480" />

### Module Binary

The code of the arduino is in the arduino folder

### Protocol

The BT module recieve string commands:
* F: activate the focus
* f: desactivate the focus
* S: activate the shutter
* s: desactivate the shutter
* P: take a photo with 1 sec for the focus
* P + integer: take a photo waitig for X sec for the focus
* R : reset all
* fs or sf: desactivate Focus and Shutter
* A + integer: set on the timelaps mode and take a photo every X sec.

## Android Application

This is my first android application. 
It use few button to send command to the BT module

## Improvements

It is possible to create a Iphone or a Desktop application to control the device.

## License

This project is fully open.

Annubis45.
