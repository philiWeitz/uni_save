using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using MoveOptimization.Optimizer;

namespace MoveOptimization.Pages
{
    /// <summary>
    /// Interaction logic for OptimizePage.xaml
    /// </summary>
    public partial class OptimizePage : Page
    {
        private StickSlipOptimizer optimizer;

        public OptimizePage()
        {
            InitializeComponent();
            init();
        }

        private void init()
        {
            optimizer = new StickSlipOptimizer();
        }

        private void ButtonOptimize_Click(object sender, RoutedEventArgs e)
        {
            if (ButtonOptimize.IsChecked.Value)
            {
                optimizer.startOptimization();
                ButtonOptimize.Content = "Stop";
            }
            else
            {
                optimizer.stopOptimization();
                ButtonOptimize.Content = "Start";
            }
        }

        /*******************************************************************/

        private void Rectangle_MouseLeave(object sender, MouseEventArgs e)
        {
            optimizer.updatePosition(null);
        }

        private void Rectangle_MouseEnter(object sender, MouseEventArgs e)
        {
            optimizer.updatePosition(e.GetPosition((IInputElement)sender));
        }

        private void Rectangle_MouseMove(object sender, MouseEventArgs e)
        {
            optimizer.updatePosition(e.GetPosition((IInputElement)sender));
        }
    }
}