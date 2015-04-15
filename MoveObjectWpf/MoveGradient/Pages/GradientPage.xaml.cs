#define DEBUG

using AForge.Imaging.Filters;
using MoveGradient.Gradient;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace MoveGradient.Pages
{
    /// <summary>
    /// Interaction logic for GradientPage.xaml
    /// </summary>
    public partial class GradientPage : Page
    {
        // TODO: get this from the image object
        private static readonly String IMAGE_PATH = @"Images\GradientImg.png";

        private GradientControl gradientControl = new GradientControl();


        public GradientPage()
        {
            InitializeComponent();
            init();
        }


        public void init()
        {
            gradientControl.setImage(IMAGE_PATH);
            gradientControl.Left = actuatorLeft;
            gradientControl.Top = actuatorTop;
            gradientControl.Right = actuatorRight;
            gradientControl.Bottom = actuatorBottom;
        }

        /************** mouse events for debug purpose ***************************/

        private void imageGradient_MouseUpEvent(object sender, MouseButtonEventArgs e)
        {
            #if DEBUG
                // stop actuation
                gradientControl.stopActuation();
            #endif
        }

        private void imageGradient_MouseLeave(object sender, MouseEventArgs e)
        {
            #if DEBUG
                // stop actuation
                gradientControl.stopActuation();
            #endif
        }

        private void imageGradient_MouseDownEvent(object sender, MouseButtonEventArgs e)
        {
            #if DEBUG
                Point cursorPosition = e.GetPosition(imageGradient);
                gradientControl.adjustActuation(cursorPosition);
            #endif
        }

        private void imageGradient_MouseMove(object sender, MouseEventArgs e)
        {
            #if DEBUG
                Point cursorPosition = e.GetPosition(imageGradient);
                gradientControl.adjustActuation(cursorPosition);
            #endif
        }
    }
}
