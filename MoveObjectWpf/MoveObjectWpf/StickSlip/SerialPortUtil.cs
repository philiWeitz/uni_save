using System;
using System.Windows;
using FTD2XX_NET;
using log4net;
using MoveObjectWpf.Properties;

namespace MoveObjectWpf.StickSlip
{
    public enum Actuator
    {
        None = 0,
        Top = 1 << 0,
        Bottom = 1 << 1,
        Left = 1 << 2,
        Right = 1 << 3
    }

    internal class SerialPortUtil
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(SerialPortUtil));
        private static SerialPortUtil instance;

        private FTDI device;
        private byte lastBitsWritten = 0xff;

        public static SerialPortUtil getInstance()
        {
            if (null == instance)
            {
                instance = new SerialPortUtil();
            }
            return instance;
        }

        private SerialPortUtil()
        {
        }

        ~SerialPortUtil()
        {
            if (null != device && device.IsOpen)
            {
                device.Close();
            }
        }

        private FTDI getFtdiDevice()
        {
            if (null == device)
            {
                UInt32 ftdiDeviceCount = 0;
                FTDI.FT_STATUS ftStatus = FTDI.FT_STATUS.FT_OK;

                device = new FTDI();
                ftStatus = device.GetNumberOfDevices(ref ftdiDeviceCount);

                if (ftStatus == FTDI.FT_STATUS.FT_OK && ftdiDeviceCount > 0)
                {
                    ftStatus = device.OpenByIndex(0);

                    if (ftStatus == FTDI.FT_STATUS.FT_OK)
                    {
                        String comPort = "NONE";
                        ftStatus = device.GetCOMPort(out comPort);
                        Console.WriteLine("Opended device on port " + comPort);

                        ftStatus = device.SetBaudRate(uint.Parse(Resource.BOUD_RATE));
                        if (ftStatus != FTDI.FT_STATUS.FT_OK)
                        {
                            logger.Error("Couldn't set FTDI boud rate");
                        }

                        ftStatus = device.SetBitMode(0xff, 0x1);
                        actuate(Actuator.None);

                        if (ftStatus != FTDI.FT_STATUS.FT_OK)
                        {
                            logger.Error("Couldn't set FTDI to bit mode");
                        }
                    }
                    else
                    {
                        logger.Error("Couldn't open device (index 0)");
                    }
                }
                else
                {
                    logger.Error("No FTDI device found");
                    MessageBox.Show("Error: No FTDI device found");
                }
            }

            return device;
        }

        public void actuate(params Actuator[] actuators)
        {
            byte value = 0;

            foreach (Actuator actuator in actuators)
            {
                value |= (byte)actuator;
            }

            try
            {
                if (value != lastBitsWritten)
                {
                    uint bytesWritten = 0;

                    // write bits to FTDI device
                    getFtdiDevice().Write(new byte[] { value }, 1, ref bytesWritten);

                    if (bytesWritten == 1)
                    {
                        lastBitsWritten = value;
                    }
                    else
                    {
                        logger.Error("Couldn't write bytes to FTDI device");
                    }
                }
            }
            catch (Exception e)
            {
                logger.Error("Couldn't write bytes to FTDI device", e);
            }
        }
    }
}