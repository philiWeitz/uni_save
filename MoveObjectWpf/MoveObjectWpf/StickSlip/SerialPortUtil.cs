using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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
        private static SerialPortUtil instance;


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

    
        public void actuate(params Actuator[] actuators)
        {
            byte value = 0;

            foreach(Actuator actuator in actuators) {           
                value |= (byte) actuator;
            }
        }
    }
}
