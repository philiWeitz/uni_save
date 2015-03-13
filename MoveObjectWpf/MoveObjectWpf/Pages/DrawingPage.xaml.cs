using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;

namespace MoveObjectWpf.Views
{
    /// <summary>
    /// Interaction logic for Page1.xaml
    /// </summary>
    public partial class DrawingPage : Page
    {
        public DrawingPage()
        {
            InitializeComponent();
            drawingCanvas.AddHandler(InkCanvas.MouseDownEvent, new MouseButtonEventHandler(InkCanvas_MouseDown), true);
        }

        private void InkCanvas_MouseDown(object sender, MouseButtonEventArgs e)
        {
            drawingCanvas.Strokes.Clear();
        }

        private void motionControlButton_Checked(object sender, RoutedEventArgs e)
        {
            motionControlButton.Content = "Stop Motion";
        }

        private void motionControlButton_Unchecked(object sender, RoutedEventArgs e)
        {
            motionControlButton.Content = "Start Motion";
        }

        private void drawingControlButton_Checked(object sender, RoutedEventArgs e)
        {
            drawingControlButton.Content = "Stop Drawing";
            drawingCanvas.IsEnabled = true;
        }

        private void drawingControlButton_Unchecked(object sender, RoutedEventArgs e)
        {
            drawingControlButton.Content = "Start Drawing";
            drawingCanvas.IsEnabled = false;
        }
    }
}