using System.Collections;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Ink;
using System.Windows.Input;
using MoveObjectWpf.Properties;
using MoveObjectWpf.StickSlip;

namespace MoveObjectWpf.Views
{
    /// <summary>
    /// Interaction logic for Page1.xaml
    /// </summary>
    public partial class DrawingPage : Page
    {
        private static readonly int MIN_DISTANCE = int.Parse(Resource.MIN_PIXEL_DISTANCY);

        private StickSlipControl stickSlipControl;
        private IEnumerator stylusEnumerator = null;

        public DrawingPage()
        {
            InitializeComponent();
            initUI();
        }

        private void initUI()
        {
            stickSlipControl = new StickSlipControl(this);

            drawingCanvas.AddHandler(InkCanvas.MouseDownEvent, new MouseButtonEventHandler(InkCanvas_MouseDown), true);
        }

        private void InkCanvas_MouseDown(object sender, MouseButtonEventArgs e)
        {
            drawingCanvas.Strokes.Clear();
        }

        private void drawingControlButton_Checked(object sender, RoutedEventArgs e)
        {
            drawingControlButton.Content = "Stop Drawing";
            canvasOverlay.Visibility = System.Windows.Visibility.Hidden;

            stylusEnumerator = null;
        }

        private void drawingControlButton_Unchecked(object sender, RoutedEventArgs e)
        {
            drawingControlButton.Content = "Start Drawing";
            canvasOverlay.Visibility = System.Windows.Visibility.Visible;

            setStylusEnumerator();
        }

        private void setStylusEnumerator()
        {
            if (null == stylusEnumerator && drawingCanvas.Strokes.Count > 0)
            {
                // get the first stroke
                IEnumerator strokeEnumerator = drawingCanvas.Strokes.GetEnumerator();
                strokeEnumerator.MoveNext();

                Stroke stroke = (Stroke)strokeEnumerator.Current;

                // get the first stylusPoint
                if (stroke.StylusPoints.Count > 0)
                {
                    stylusEnumerator = stroke.StylusPoints.GetEnumerator();
                    stylusEnumerator.MoveNext();
                }
            }
        }

        private void onMouseMove(Point cursorPosition)
        {
            if (null != stylusEnumerator)
            {
                cursorPositionTextBlock.Text = "X: " + cursorPosition.X.ToString() + " - Y: " + cursorPosition.Y.ToString();

                Point destination = ((StylusPoint)stylusEnumerator.Current).ToPoint();
                double distance = PointUtil.getDistance(cursorPosition, destination);

                // get next point
                if (distance < MIN_DISTANCE)
                {
                    bool hasNext = stylusEnumerator.MoveNext();

                    // no next point -> have reached the final destination
                    if (!hasNext)
                    {
                        stylusEnumerator = null;
                        return;
                    }

                    // get next point
                    destination = ((StylusPoint)stylusEnumerator.Current).ToPoint();
                }

                stickSlipControl.adjustActuation(cursorPosition, destination);
            }
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
            Point cursorPosition = e.GetTouchPoint(canvasOverlay).Position;
            onMouseMove(cursorPosition);
        }

        private void canvasOverlay_TouchEnter(object sender, TouchEventArgs e)
        {
            Point cursorPosition = e.GetTouchPoint(canvasOverlay).Position;
            onMouseMove(cursorPosition);
        }

        private void canvasOverlay_TouchDown(object sender, TouchEventArgs e)
        {
            Point cursorPosition = e.GetTouchPoint(canvasOverlay).Position;
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
            Point cursorPosition = e.GetPosition(canvasOverlay);
            onMouseMove(cursorPosition);
        }

        private void canvasOverlay_StylusDown(object sender, StylusDownEventArgs e)
        {
            Point cursorPosition = e.GetPosition(canvasOverlay);
            onMouseMove(cursorPosition);
        }

        private void canvasOverlay_StylusEnter(object sender, StylusEventArgs e)
        {
            Point cursorPosition = e.GetPosition(canvasOverlay);
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
                Point cursorPosition = e.GetPosition(canvasOverlay);
                onMouseMove(cursorPosition);
            #endif
        }

        private void canvasOverlay_MouseMove(object sender, MouseEventArgs e)
        {
            #if DEBUG
                Point cursorPosition = e.GetPosition(canvasOverlay);
                onMouseMove(cursorPosition);
            #endif
        }
    }
}