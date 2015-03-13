using System.Collections;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Ink;
using System.Windows.Input;
using MoveObjectWpf.StickSlip;

namespace MoveObjectWpf.Views
{
    /// <summary>
    /// Interaction logic for Page1.xaml
    /// </summary>
    public partial class DrawingPage : Page
    {
        private const int MIN_DISTANCE = 10; 

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

        private void canvasOverlay_MouseUpEvent(object sender, MouseButtonEventArgs e)
        {
            // stop actuation
            stickSlipControl.stopActuation();
            // set the enumerator back to null;
            stylusEnumerator = null;
        }

        private void canvasOverlay_MouseDownEvent(object sender, MouseButtonEventArgs e)
        {
            Point cursorPosition = e.GetPosition(canvasOverlay);
            onMouseMove(cursorPosition);
        }

        private void canvasOverlay_MouseMove(object sender, MouseEventArgs e)
        {
            Point cursorPosition = e.GetPosition(canvasOverlay);
            onMouseMove(cursorPosition);            
        }

        private void setStylusEnumerator()
        {
            if (drawingCanvas.Strokes.Count > 0)
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
    }
}