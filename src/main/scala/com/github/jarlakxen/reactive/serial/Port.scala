package com.github.jarlakxen.reactive.serial

import com.fazecast.jSerialComm.SerialPort
import scala.util.{ Try, Failure, Success }
import akka.util.ByteString
import java.io.IOException

/**
 * @author fviale
 */
class Port(port: SerialPort) {

  def systemName = port.getSystemPortName

  def open(baudRate: Int): Try[_] = {
    if (isClose) {
      port.setBaudRate(baudRate)
      port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0)
      if (port.openPort)
        Success(())
      else
        Failure(new IOException(s"Cannot open port '${port.getSystemPortName}'"))
    } else {
      Success(())
    }
  }

  def isOpen = port.isOpen
  def isClose = !isOpen

  def read(buffer: Array[Byte]): Try[Int] = {
    val bytes = port.readBytes(buffer, buffer.length)
    if (bytes == -1) {
      Failure(new IOException(s"There was an error reading the port '${port.getSystemPortName}'"))
    } else {
      Success(bytes)
    }
  }

  def write(data: ByteString): Try[Int] = {
    val bytes = port.writeBytes(data.toArray, data.length)
    if (bytes == -1) {
      Failure(new IOException(s"There was an error writing to the port '${port.getSystemPortName}'"))
    } else {
      Success(bytes)
    }
  }

  def close: Try[_] = {
    if (port.closePort())
      Success(())
    else
      Failure(new IOException(s"Cannot open port '${port.getSystemPortName}'"))
  }

}