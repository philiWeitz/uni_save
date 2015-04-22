//#define DEBUG

using System.Collections;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Ink;
using System.Windows.Input;
using MoveObjectWpf.Properties;
using MoveObjectWpf.StickSlip;
using System.Windows.Media;
using System;

namespace MoveObjectWpf.Views
{
    /// <summary>
    /// Interaction logic for Page1.xaml
    /// </summary>
    public partial class DrawingPage : Page
    {
        private static int MIN_DISTANCE = int.Parse(Resource.MIN_PIXEL_DISTANCY);

        private StickSlipControl stickSlipControl;

        private IEnumerator strokeEnumerator;
        private IEnumerator stylusEnumerator = null;
        private StrokeCollection pathStrokes = new StrokeCollection();


        public DrawingPage()
        {
            InitializeComponent();
            initUI();
        }

        private void initUI()
        {
            stickSlipControl = new StickSlipControl(this);
            sliderOn.Value = double.Parse(Resource.ON_TIME_IN_MS);
            sliderOff.Value = double.Parse(Resource.OFF_TIME_IN_MS);
            sliderPeak.Value = double.Parse(Resource.PEAK_TIME_IN_MS);
            testBoxPixels.Text = MIN_DISTANCE.ToString();
        }


        private void drawingControlButton_Checked(object sender, RoutedEventArgs e)
        {
            drawingControlButton.Content = "Stop Drawing";

            pathStrokes.Clear();
            drawingCanvas.Strokes.Clear();

            drawingCanvas.DefaultDrawingAttributes.Color = Colors.LightBlue;

            stylusEnumerator = null;
            strokeEnumerator = null;
        }

        private void drawingControlButton_Unchecked(object sender, RoutedEventArgs e)
        {
            drawingControlButton.Content = "Start Drawing";

            pathStrokes = drawingCanvas.Strokes.Clone();
            drawingCanvas.DefaultDrawingAttributes.Color = Colors.Red;

            getNextStylusPoint();
        }

        private bool getNextStylusPoint()
        {
            if(pathStrokes.Count > 0) {

                // there is some data 
                if(null == strokeEnumerator) {
                    strokeEnumerator = pathStrokes.GetEnumerator();
                    strokeEnumerator.Reset();
                    stylusEnumerator = null;
                } 

                // get next stylus point
                if (null != stylusEnumerator && stylusEnumerator.MoveNext())
                {
                    return true;
                }
                else if (strokeEnumerator.MoveNext())
                {
                    stylusEnumerator = ((Stroke)strokeEnumerator.Current).StylusPoints.GetEnumerator();
                    stylusEnumerator.Reset();

                    return stylusEnumerator.MoveNext();
                }
                else
                {
                    stylusEnumerator = null;
                    strokeEnumerator = null;
                }
            }

            return false;
        }

        private void onMouseMove(Point cursorPosition)
        {
            if (null != stylusEnumerator)
            {
                cursorPositionTextBlock.Text = "X: " + (int) cursorPosition.X + " - Y: " + (int) cursorPosition.Y;

                Point destination = ((StylusPoint)stylusEnumerator.Current).ToPoint();
                double distance = PointUtil.getDistance(cursorPosition, destination);

                // get next point
                if (distance < MIN_DISTANCE)
                {
                    if (getNextStylusPoint())
                    {
                        // get next point
                        destination = ((StylusPoint)stylusEnumerator.Current).ToPoint();
                    }
                }

                stickSlipControl.adjustActuation(cursorPosition, destination);
            }
        }

        private void sliderPeak_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {
            labelPeak.Content = "Peak (" + sliderPeak.Value + ")";
            SerialPortUtil.PEAK_TIME_IN_MS = (int)sliderPeak.Value;
        }

        private void sliderOn_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {
            labelOn.Content = "On (" + sliderOn.Value + ")";
            SerialPortUtil.ON_TIME_IN_MS = (int)sliderOn.Value;
        }

        private void sliderOff_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {
            labelOff.Content = "Off (" + sliderOff.Value + ")";
            SerialPortUtil.OFF_TIME_IN_MS = (int)sliderOff.Value;
        }

        private void clearButton_Click(object sender, RoutedEventArgs e)
        {
            drawingCanvas.Strokes.Clear();
            drawingCanvas.Strokes = pathStrokes.Clone();

            strokeEnumerator = null;
            getNextStylusPoint();
        }

        private void testBoxPixels_TextChanged(object sender, TextChangedEventArgs e)
        {
            try
            {
                int distance = int.Parse(testBoxPixels.Text);
                MIN_DISTANCE = distance;
            }
            catch (Exception) { }
        }

        /************** touch screen events ***************************/

        private void canvasOverlay_TouchLeave(object sender, TouchEventArgs e)
        {
            // stop actuation
            stickSlipControl.stopActuation();
        }

        private void canvasOverlay_TouchUp(object sender, TouchEventArgs e)
        {
            // stop actuation
            stickSlipControl.stopActuation();
        }

        private void canvasOverlay_TouchMove(object sender, TouchEventArgs e)
        {
            Point cursorPosition = e.GetTouchPoint(drawingCanvas).Position;
            onMouseMove(cursorPosition);
        }

        private void canvasOverlay_TouchEnter(object sender, TouchEventArgs e)
        {
            Point cursorPosition = e.GetTouchPoint(drawingCanvas).Position;
            onMouseMove(cursorPosition);
        }

        private void canvasOverlay_TouchDown(object sender, TouchEventArgs e)
        {
            Point cursorPosition = e.GetTouchPoint(drawingCanvas).Position;
            onMouseMove(cursorPosition);
        }


        /************** stylus events ***************************/

        private void canvasOverlay_StylusLeave(object sender, StylusEventArgs e)
        {
            // stop actuation
            stickSlipControl.stopActuation();
        }

        private void canvasOverlay_StylusUp(object sender, StylusEventArgs e)
        {
            // stop actuation
            stickSlipControl.stopActuation();
        }

        private void canvasOverlay_StylusMove(object sender, StylusEventArgs e)
        {
            Point cursorPosition = e.GetPosition(drawingCanvas);
            onMouseMove(cursorPosition);
        }

        private void canvasOverlay_StylusDown(object sender, StylusDownEventArgs e)
        {
            Point cursorPosition = e.GetPosition(drawingCanvas);
            onMouseMove(cursorPosition);
        }

        private void canvasOverlay_StylusEnter(object sender, StylusEventArgs e)
        {
            Point cursorPosition = e.GetPosition(drawingCanvas);
            onMouseMove(cursorPosition);
        }


        /************** mouse events for debug purpose ***************************/

        private void canvasOverlay_MouseUpEvent(object sender, MouseButtonEventArgs e)
        {
            #if DEBUG
                // stop actuation
                stickSlipControl.stopActuation();
            #endif
        }

        private void canvasOverlay_MouseLeave(object sender, MouseEventArgs e)
        {
            #if DEBUG
                // stop actuation
                stickSlipControl.stopActuation();
            #endif
        }

        private void canvasOverlay_MouseDownEvent(object sender, MouseButtonEventArgs e)
        {
            #if DEBUG
                Point cursorPosition = e.GetPosition(drawingCanvas);
                onMouseMove(cursorPosition);
            #endif
        }

        private void canvasOverlay_MouseMove(object sender, MouseEventArgs e)
        {
            #if DEBUG
                Point cursorPosition = e.GetPosition(drawingCanvas);
                onMouseMove(cursorPosition);
            #endif
        }
    }
}