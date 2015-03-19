using System;
using System.Threading;
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
        private static readonly int PEAK_TIME_IN_MS = int.Parse(Resource.PEAK_TIME_IN_MS);
        private static readonly int AVERAGE_TIME_IN_MS = int.Parse(Resource.AVERAGE_TIME_IN_MS);

        private static readonly ILog logger = LogManager.GetLogger(typeof(SerialPortUtil));

        private static SerialPortUtil instance;

        private static bool running = true;
        private static byte toWrite = 0x0;

        private FTDI device;
        private readonly Thread updateThread;
        

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
            updateThread = new Thread(new ThreadStart(ControllerUpdate));
            updateThread.IsBackground = true;
            updateThread.Start();
        }

        ~SerialPortUtil()
        {
            if (null != device && device.IsOpen)
            {
                device.Close();
            }

            running = false;
            updateThread.Abort();
            updateThread.Join();
        }

        public void actuate(params Actuator[] actuators)
        {
            byte value = 0;

            foreach (Actuator actuator in actuators)
            {
                value |= (byte)actuator;
            }

            toWrite = value;
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

        private void ControllerUpdate()
        {
            while (running)
            {
                if (toWrite > 0)
                {
                    byte writeToController = toWrite;
                    uint bytesWritten = 0;

                    // start with peak voltage for 1s (110000B | toWrite)
                    writeToController |= 0xC0;
                    getFtdiDevice().Write(new byte[] { writeToController }, 1, ref bytesWritten);
                    logger.Debug("Sending " + Convert.ToString(writeToController, 2).PadLeft(8, '0'));
                    Thread.Sleep(PEAK_TIME_IN_MS);

                    // start with peak voltage for 1s (100000B | toWrite)
                    writeToController ^= 0x40;
                    getFtdiDevice().Write(new byte[] { writeToController }, 1, ref bytesWritten);
                    logger.Debug("Sending " + Convert.ToString(writeToController, 2).PadLeft(8, '0'));
                    Thread.Sleep(AVERAGE_TIME_IN_MS);

                    if (bytesWritten != 1)
                    {
                        logger.Error("Couldn't write bytes to FTDI device");
                    }
                }
                else
                {
                    Thread.Sleep(AVERAGE_TIME_IN_MS + PEAK_TIME_IN_MS);
                }
            }
        }
    }
}