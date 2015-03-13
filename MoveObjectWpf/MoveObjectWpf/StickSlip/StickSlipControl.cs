

using System.Windows;
using System.Windows.Media;
using MoveObjectWpf.Views;
namespace MoveObjectWpf.StickSlip
{
    internal class StickSlipControl
    {
        private DrawingPage parent;


        public StickSlipControl(DrawingPage parent)
        {
            this.parent = parent;
        }


        public void adjustActuation(Point currentPosition, Point destination)
        {
            Actuator topBottom = Actuator.None;
            Actuator leftRight = Actuator.None;

            Point subtract = PointUtil.subtractPoints(currentPosition, destination);

            // decide top or bottom actuation
            if (subtract.X < 0)
            {
                // left actuator
                leftRight = Actuator.Left;
            }
            else
            {
                // right actuator
                leftRight = Actuator.Right;
            }

            // decide left or right actuation
            if (subtract.Y < 0)
            {
                // top actuator
                topBottom = Actuator.Top;
            }
            else
            {
                // bottom actuator
                topBottom = Actuator.Bottom;
            }

            // calculate alpha angle to actuate either up-down, left-right or both
            double alphaAngle = PointUtil.getAlphaAngle(destination, currentPosition);

            setActuatorColorToTransparent();

            if (alphaAngle < 40)
            {
                // actuate leftRight
                SerialPortUtil.getInstance().actuate(leftRight);
                setActuatorColor(leftRight);
            }
            else if (alphaAngle > 50)
            {
                // actuate topBottom
                SerialPortUtil.getInstance().actuate(topBottom);
                setActuatorColor(topBottom);
            }
            else
            {
                // actuate both
                SerialPortUtil.getInstance().actuate(leftRight, topBottom);
                setActuatorColor(leftRight);
                setActuatorColor(topBottom);
            }
        }

        public void stopActuation()
        {
            SerialPortUtil.getInstance().actuate(Actuator.None);
            setActuatorColorToTransparent();
        }




        // debug output function
        private void setActuatorColor(Actuator actuator)
        {
            switch (actuator)
            {
                case Actuator.Top:
                    parent.actuatorTop.Fill = Brushes.Red;
                    break;
                case Actuator.Bottom:
                    parent.actuatorBottom.Fill = Brushes.Red;
                    break;
                case Actuator.Left:
                    parent.actuatorLeft.Fill = Brushes.Red;
                    break;
                case Actuator.Right:
                    parent.actuatorRight.Fill = Brushes.Red;
                    break;
            }
        }


        // debug function
        private void setActuatorColorToTransparent()
        {
            parent.actuatorBottom.Fill = Brushes.Transparent;
            parent.actuatorLeft.Fill = Brushes.Transparent;
            parent.actuatorRight.Fill = Brushes.Transparent;
            parent.actuatorTop.Fill = Brushes.Transparent;
        }
    }
}