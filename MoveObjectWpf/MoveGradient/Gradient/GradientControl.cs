using AForge.Imaging.Filters;
using MoveObjectWpf.StickSlip;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media;
using System.Windows.Shapes;

namespace MoveGradient.Gradient
{
    class GradientControl
    {
        private GradientImage gradientImg;
        

        public void setImage(String path)
        {
            System.Drawing.Bitmap fromFile = new System.Drawing.Bitmap(System.Drawing.Image.FromFile(path));

            Grayscale filter = new Grayscale(0.2125, 0.7154, 0.0721);

            // apply the filter
            System.Drawing.Bitmap grayImage = filter.Apply(fromFile);
            createGradientImage(grayImage);
        }


        public void stopActuation()
        {
            SerialPortUtil.getInstance().actuate(Actuator.None);
        }


        // TODO: handle border region (e.g. X/Y == 0)
        public void adjustActuation(Point current)
        {
            IList<Actuator> actuators = new List<Actuator>();
            
            if (current.X > 0 && current.X < gradientImg.Width - 1 && current.Y > 0 && current.Y < gradientImg.Height - 1) 
            {
                GradientPixel gradPixel = gradientImg.getPixel((int)current.X, (int)current.Y);
  
                if(gradPixel.Left < 0 && gradPixel.Left < gradPixel.Right) {
                    // to the left
                    actuators.Add(Actuator.Left);
                } else if(gradPixel.Right < 0) {
                    // to the right
                    actuators.Add(Actuator.Right);
                }
            
                if(gradPixel.Top < 0 && gradPixel.Top < gradPixel.Bottom) {
                    // to the top
                    actuators.Add(Actuator.Top);
                } else if(gradPixel.Bottom < 0) {
                    // to the bottom
                    actuators.Add(Actuator.Bottom);
                }

                SerialPortUtil.getInstance().actuate(actuators.ToArray());
            } else {
                SerialPortUtil.getInstance().actuate(Actuator.None);
            }

            printDebugOutput(actuators);
        }

        private void createGradientImage(System.Drawing.Bitmap grayImage)
        {
            gradientImg = new GradientImage(grayImage.Width, grayImage.Height);

            for (int y = 1; y < gradientImg.Height - 1; ++y)
            {
                for (int x = 1; x < gradientImg.Width - 1; ++x)
                {
                    byte curr = grayImage.GetPixel(x,y).R;
                    byte left = grayImage.GetPixel(x - 1, y).R;
                    byte top = grayImage.GetPixel(x, y - 1).R;
                    byte right = grayImage.GetPixel(x + 1, y).R;
                    byte bottom = grayImage.GetPixel(x, y + 1).R;

                    GradientPixel pixel = gradientImg.getPixel(x, y);

                    pixel.Left = left - curr;
                    pixel.Top = top - curr;
                    pixel.Right = right - curr;
                    pixel.Bottom = bottom - curr;                   
                }
            }
        }


        // TODO: only for debug purpose

        public Ellipse Left { get; set; }
        public Ellipse Top { get; set; }
        public Ellipse Right { get; set; }
        public Ellipse Bottom { get; set; }

        private void printDebugOutput(IList<Actuator> actuators)
        {
            if (null != Left)
            {
                setVisibility(Left, actuators.Contains(Actuator.Left));
            }
            if (null != Top)
            {
                setVisibility(Top, actuators.Contains(Actuator.Top));
            }
            if (null != Right)
            {
                setVisibility(Right, actuators.Contains(Actuator.Right));
            }
            if (null != Bottom)
            {
                setVisibility(Bottom, actuators.Contains(Actuator.Bottom));
            }
        }

        private void setVisibility(Ellipse actuatorSign, bool visible)
        {
            if (visible)
            {
                actuatorSign.Fill = Brushes.Red;
            }
            else
            {
                actuatorSign.Fill = Brushes.Transparent;
            }
        }
    }
}
